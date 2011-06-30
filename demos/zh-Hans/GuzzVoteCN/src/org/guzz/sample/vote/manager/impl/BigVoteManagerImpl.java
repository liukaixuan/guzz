/**
 * BigVoteManagerImpl.java created at 2009-9-21 下午05:39:23 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.guzz.Service;
import org.guzz.dao.GuzzBaseDao;
import org.guzz.lang.NullValue;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.sample.vote.action.model.BigVoteTree;
import org.guzz.sample.vote.action.model.VoterInfo;
import org.guzz.sample.vote.business.BigVote;
import org.guzz.sample.vote.business.TerritoryVoteLog;
import org.guzz.sample.vote.business.VoteExtraProperty;
import org.guzz.sample.vote.business.VoteItem;
import org.guzz.sample.vote.business.VoteLog;
import org.guzz.sample.vote.business.VoteTerritory;
import org.guzz.sample.vote.exception.VoteException;
import org.guzz.sample.vote.manager.IAntiCheatPolicyChecker;
import org.guzz.sample.vote.manager.IAntiCheatPolicyManager;
import org.guzz.sample.vote.manager.IBigVoteManager;
import org.guzz.sample.vote.manager.IUserInputValidator;
import org.guzz.sample.vote.manager.IVoteExtraPropertyManager;
import org.guzz.sample.vote.util.VoteAssert;
import org.guzz.service.core.SlowUpdateService;
import org.guzz.service.dir.CityRuleMatcher;
import org.guzz.service.dir.IPLocationService;
import org.guzz.service.dir.impl.LocationResult;
import org.guzz.service.dir.matcher.ForeignCountryRuleMatcher;
import org.guzz.service.log.LogService;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.StringUtil;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class BigVoteManagerImpl extends GuzzBaseDao implements IBigVoteManager {
	
	private IVoteExtraPropertyManager voteExtraPropertyManager ;
	
	private IAntiCheatPolicyManager antiCheatPolicyManager ;
		
	private SlowUpdateService slowUpdateService ;
	
	private LogService logService ;
		
	private Cache voteTreeCache ;
	
	private IPLocationService ipLocationService ;
		
	private CityRuleMatcher overSeaRuleMatcher = new ForeignCountryRuleMatcher() ;

	public void addBigVote(BigVote vote) {
		super.insert(vote) ;
	}

	public BigVote getBigVoteForUpdate(int voteId) {
		return (BigVote) super.getForUpdate(BigVote.class, voteId) ;
	}
	
	public void recomputeVoteCount(int voteId){
		//在每次update时，重新统计总的票数保持和投票项一致。
		SearchExpression se =SearchExpression.forClass(VoteItem.class) ;
		se.and(Terms.eq("voteId", voteId)) ;
		se.setCountSelectPhrase("sum(voteNum)") ;
		int voteNum = (int) super.count(se) ;
		
		TransactionManager tm = super.getTransactionManager() ;
		
		String sql = "update @@" + BigVote.class.getName() +  " set @voteNum = :voteNum where @id = :id" ;
		CompiledSQL cs = tm.getCompiledSQLBuilder().buildCompiledSQL(BigVote.class, sql) ;
		
		WriteTranSession session =  tm.openRWTran(true) ;
		
		try{			
			session.executeUpdate(cs.bind("voteNum", voteNum).bind("id", voteId)) ;
		}finally{
			session.close() ;
		}
	}

	public void updateBigBote(BigVote vote) {
		super.update(vote) ;
	}

	public BigVote getBigVoteForReadonly(int voteId) {
		return (BigVote) super.getForRead(BigVote.class, voteId) ;
	}

	public void makeAVote(int voteId, int[] items, int cityId, VoterInfo info) throws VoteException {
		VoteAssert.assertBigger(voteId, 0, "投票号不存在！") ;
		VoteAssert.assertBigger(items.length, 0, "请选择最少一个投票项！") ;
		
		BigVoteTree tree = getCachedVoteTree(voteId) ;
		VoteAssert.assertNotNull(tree, "投票不存在") ;
		BigVote vote = tree.getBigVote() ;
		VoteAssert.assertNotNull(vote, "投票不存在") ;
		
		VoteAssert.assertTrue(vote.isOpenToPublicNow(), "投票已经关闭") ;
		
		VoteTerritory userCity = null ;
		String userIP = info.getXIP() ;
		
		//检查用户是否作弊
		this.assertNotCheat(tree, info) ;
		
		
		//获取用户所在城市
		if(vote.isTerritoryIPAutoDetectedMode()){
			LocationResult result = (LocationResult) this.ipLocationService.findLocation(userIP).getOrCancel(3, TimeUnit.SECONDS) ;
			
			if(result != null){
				List<VoteTerritory> m_cities = tree.getVoteTerritories() ;
				for(VoteTerritory vt : m_cities){
					if(result.cityName.contains(vt.getName())){
						userCity = vt ;
						break ;
					}
				}
				
				if(userCity == null){
					//没有符合条件的城市，检查是否是“海外”
					if(tree.getOverSeaTerritory() != null && overSeaRuleMatcher.matchRule(result.cityName)){
						userCity = tree.getOverSeaTerritory() ;
					}
				}
			}
			
			if(userCity == null){
				userCity = tree.getOtherTerritory() ;
			}
		}else{
			VoteAssert.assertBigger(cityId, 0, "请选择城市！") ;
			userCity = tree.getVoteTerritory(cityId) ;
			
			VoteAssert.assertNotNull(userCity, "请选择投票地区") ;
		}
		
		//检查投票自定义属性数据是否正确。
		String extraPropsXML = checkParamAndCreateLogXML(tree.getExtraProperties(), info) ;
		
		int validVoteCount = 0 ;
		
		Date now = new Date() ;
		
		for(int i = 0 ; i < items.length && (validVoteCount < vote.getMaxItemsPerVote() || vote.getMaxItemsPerVote() <= 0) ; i++){
			int itemId = items[i] ;
			if(itemId < 1) continue ;
			
			VoteItem item = tree.getVoteItem(itemId) ;
			
			if(item == null){
				continue ;
			}
			
			//1. 投票项得票数+1
			slowUpdateService.updateCount(VoteItem.class, null, "voteNum", itemId, 1) ;
	
			//2. 地区投票记录+1
			if(userCity != null){
				slowUpdateService.updateCount(TerritoryVoteLog.class, null, "voteNum", itemId + "_" + userCity.getId() , 1) ;
			}
			
			//3. 投票日志增加1条
			VoteLog log = new VoteLog() ;
			log.setVoteId(vote.getId()) ;
			
			//保持一次投票的多个投票项日志时间完全一致（毫秒级），以便于以后的日志统计。
			log.setCreatedTime(now) ;
			log.setIP(StringUtil.dealNull(info.getXIP())) ;
			log.setItemName(item.getName()) ;
			if(userCity != null){
				log.setTerritoryName(userCity.getName()) ;
			}else{
				log.setTerritoryName("unknown") ;
			}
			log.setExtraPropsXML(StringUtil.dealNull(extraPropsXML)) ;
			
			logService.log(log) ;
			
			//
			validVoteCount++ ;
		}
		
		if(userCity != null){
			//4. 地区唯一投票+1
			slowUpdateService.updateCount(VoteTerritory.class, null, "votePeople", userCity.getId(), 1) ;
			
			//5. 来自此地区投出票数 +validVoteCount
			slowUpdateService.updateCount(VoteTerritory.class, null, "voteNum", userCity.getId(), validVoteCount) ;
		}
		
		//6. 投票的唯一投票人数+1
		slowUpdateService.updateCount(BigVote.class, null, "votePeople", voteId, 1) ;
		
		//7. 投票的总得票数+validVoteCount
		slowUpdateService.updateCount(BigVote.class, null, "voteNum", voteId, validVoteCount) ;
		
		//8. 为成功投票做标记。
		this.markOneSuccessVote(tree, info) ;
		
	}
		
	protected void assertNotCheat(BigVoteTree tree, VoterInfo info){
		List<IAntiCheatPolicyChecker> checkers = tree.getAntiCheckCheckers() ;
		if(checkers == null) return ;
		
		for(int i = 0 ; i < checkers.size() ; i++){
			checkers.get(i).checkCanVote(tree, info) ;
		}
	}
	
	protected void markOneSuccessVote(BigVoteTree tree, VoterInfo info){
		List<IAntiCheatPolicyChecker> checkers = tree.getAntiCheckCheckers() ;
		if(checkers == null) return ;
		
		for(int i = 0 ; i < checkers.size() ; i++){
			checkers.get(i).markOneVote(tree, info) ;
		}
	}
	
	/**
	 * 检查自定义属性值格式是否符合要求，并且返回自定义属性组成的xml值。
	 * 此值将用于存储到日志中。
	 */
	protected String checkParamAndCreateLogXML(List<VoteExtraProperty> extraProps, VoterInfo info){		
		Document document = DocumentHelper.createDocument();
        org.dom4j.Element root = document.addElement("extraProps");
        
		if(!extraProps.isEmpty()){
			for(int i = 0 ; i < extraProps.size() ; i++){
				VoteExtraProperty prop = extraProps.get(i) ;
				String paramValue = info.getParamValue(prop.getParamName()) ;
				String validatorName = prop.getValidRuleName() ;
				
				org.dom4j.Element domElement = root.addElement(prop.getParamName()) ;
				
				if(paramValue != null){
					paramValue = paramValue.trim() ;
					if(paramValue.length() == 0){
						paramValue = null ;
					}
				}
				
				if(paramValue == null){
					VoteAssert.assertFalse(prop.isMustProp(), prop.getShowName() + "不能为空！") ;
				}else if(StringUtil.notEmpty(validatorName)){
					IUserInputValidator iv = this.voteExtraPropertyManager.getUserInputValidator(validatorName) ;
					VoteAssert.assertNotNull(iv, "投票验证规则不存在：" + validatorName) ;
					
					iv.checkValid(prop, paramValue) ;
				}
				
				domElement.addText(StringUtil.dealNull(paramValue)) ;
			}
		}
		
		return root.asXML() ;
	}
	
	public BigVote getCachedVote(int voteId){
		BigVoteTree tree = getCachedVoteTree(voteId) ;
		
		if(tree == null){
			return null ;
		}else{
			return tree.getBigVote() ;
		}
	}

	//获取缓存的cache对象
	public BigVoteTree getCachedVoteTree(int voteId){
		String key = String.valueOf(voteId) ;
		Element element = this.voteTreeCache.get(key) ;
		
		//如果cache已经过期或者还没有存入。
		if(element == null){
			BigVoteTree tree = getCachedVoteTreeNoCache(voteId) ;
			
			if(tree != null){
				element = new Element(key, tree) ;
			}else{
				element = new Element(key, new NullValue()) ;
			}
			
			this.voteTreeCache.put(element) ;
			
			return tree ;
		}
		
		//读取cache
		Object value = element.getObjectValue() ;
		
		if(value instanceof NullValue){
			return null ;
		}else{
			return (BigVoteTree) value ;
		}
	}
	
	//从数据库读取新的投票对象
	public BigVoteTree getCachedVoteTreeNoCache(int voteId){
		BigVote bv = getBigVoteForReadonly(voteId) ;
		if(bv == null){
			return null ;
		}
		
		BigVoteTree tree = new BigVoteTree() ;
		tree.setBigVote(bv) ;
		
		//读取投票项目
		tree.setVoteItems(listVoteItems(voteId)) ;
		
		List<VoteTerritory> cities = listVoteTerritories(voteId) ;
		tree.setVoteTerritories(cities) ;
		for(VoteTerritory city : cities){
			if("海外".equalsIgnoreCase(city.getName())){
				tree.setOverSeaTerritory(city) ;
			}else if("其他".equalsIgnoreCase(city.getName())){
				tree.setOtherTerritory(city) ;
			}
		}

		tree.setExtraProperties(this.voteExtraPropertyManager.listByVoteId(voteId)) ;
		
		//load anti cheat
		tree.setAntiCheckCheckers(this.antiCheatPolicyManager.listPolicyCheckers(voteId)) ;
		
		return tree ;
	}
	
	public List<VoteItem> listVoteItems(int voteId){
		ReadonlyTranSession session = getTransactionManager().openDelayReadTran() ;
		
		try{
			SearchExpression se = SearchExpression.forLoadAll(VoteItem.class) ;
			se.and(Terms.eq("voteId", voteId)) ;
			se.setOrderBy("id asc") ;
			return session.list(se) ;	
		}finally{
			session.close() ;
		}
	}
	
	public List<VoteTerritory> listVoteTerritories(int voteId){
		ReadonlyTranSession session = getTransactionManager().openDelayReadTran() ;
		
		try{
			SearchExpression se = SearchExpression.forLoadAll(VoteTerritory.class) ;
			se.and(Terms.eq("voteId", voteId)) ;
			se.setOrderBy("id asc") ;
			return session.list(se) ;	
		}finally{
			session.close() ;
		}
	}
	
	//////////////////////////VoteItem/////////////////////////////////////////
	
	public void addVoteItem(VoteItem item){
		int voteId = item.getVoteId() ;
		
		//事务操作
		WriteTranSession session = getTransactionManager().openRWTran(false) ;
		
		try{
			BigVote vote = (BigVote) session.findObjectByPK(BigVote.class, voteId) ;
			VoteAssert.assertNotNull(vote, "投票不存在。投票号：" + voteId) ;
			
			//1. 增加投票项
			int itemId = (Integer) session.insert(item) ;			
			
			//为此投票的每个地区，增加地区投票记录
			ReadonlyTranSession readSession = getTransactionManager().openNoDelayReadonlyTran() ;
			try{
				SearchExpression se = SearchExpression.forLoadAll(VoteTerritory.class) ;
				se.and(Terms.eq("voteId", voteId)) ;
				List<VoteTerritory> cities = readSession.list(se) ;
				
				for(VoteTerritory city : cities){
					TerritoryVoteLog log = new TerritoryVoteLog() ;
					log.setId(itemId + "_" + city.getId()) ;
					log.setItemId(itemId) ;
					log.setTerritoryId(city.getId()) ;
					log.setVoteId(voteId) ;
					log.setVoteNum(0) ;
					
					session.insert(log) ;
				}
				
				//更新手工干预结果。
				if(vote.getAddedVoteNum() > 0){
					vote.setAddedVoteNum(vote.getAddedVoteNum() + item.getAddedVoteNum()) ;
					session.update(vote) ;
				}				
			}finally{
				readSession.close() ;
			}
			
			session.commit() ;
			
		}catch(RuntimeException e){
			session.rollback() ;
			
			throw e ;
		}finally{
			session.close() ;
		}
	}
	
	public void addVoteTerritory(VoteTerritory city){
		int voteId = city.getVoteId() ;
		
		//事务操作
		WriteTranSession session = getTransactionManager().openRWTran(false) ;
		
		try{
			BigVote vote = (BigVote) session.findObjectByPK(BigVote.class, voteId) ;
			VoteAssert.assertNotNull(vote, "投票不存在。投票号：" + voteId) ;
			
			//1. 增加投票项
			session.insert(city) ;
			
			//为此地区的每个投票，增加地区投票记录
			ReadonlyTranSession readSession = getTransactionManager().openNoDelayReadonlyTran() ;
			try{
				SearchExpression se = SearchExpression.forLoadAll(VoteItem.class) ;
				se.and(Terms.eq("voteId", voteId)) ;
				List<VoteItem> items = readSession.list(se) ;
				
				for(VoteItem item : items){
					TerritoryVoteLog log = new TerritoryVoteLog() ;
					log.setId(item.getId() + "_" + city.getId()) ;
					log.setItemId(item.getId()) ;
					log.setTerritoryId(city.getId()) ;
					log.setVoteId(voteId) ;
					log.setVoteNum(0) ;
					
					session.insert(log) ;
				}
				
				//取消自动更新，只允许在投票项处干预，地区只能被动修改以保持数据一致性。以避免数据错乱，造成得票率超过100%出现。
				
//				//更新手工干预结果。
//				if(city.getAddedVoteNum() > 0){
//					vote.setAddedVoteNum(vote.getAddedVoteNum() + city.getAddedVoteNum()) ;					
//					session.update(vote) ;
//				}
			}finally{
				readSession.close() ;
			}
			
			session.commit() ;
			
		}catch(RuntimeException e){
			session.rollback() ;
			
			throw e ;
		}finally{
			session.close() ;
		}
	}
	
	public void addAllChineseProvincesAsVoteTerritories(int voteId){
		String[] chinaCities = new String[]{
				"北京", "天津", "河北", "山西", "内蒙古", "辽宁", 
				"吉林", "黑龙江", "上海", "江苏", "浙江", "安徽", 
				"福建", "江西", "山东", "河南", "湖北", "湖南", 
				"广东", "广西", "海南", "重庆", "四川", "贵州", 
				"云南", "西藏", "陕西", "甘肃", "青海", "宁夏",
				"新疆", "兵团", "香港", "澳门", "台湾"
		} ;
		
		for(int i = 0 ; i < chinaCities.length ; i++){
			VoteTerritory t = new VoteTerritory() ;
			t.setVoteId(voteId) ;
			t.setName(chinaCities[i]) ;
			
			this.addVoteTerritory(t) ;
		}
	}
	
	public void addAdditionalVoteTerritoriesForAutoIP(int voteId){
		String[] otherCities = new String[]{
				"海外", "其他"
		} ;
		
		for(int i = 0 ; i < otherCities.length ; i++){
			VoteTerritory t = new VoteTerritory() ;
			t.setVoteId(voteId) ;
			t.setName(otherCities[i]) ;
			
			this.addVoteTerritory(t) ;
		}
	}
	
	public VoteItem getVoteItem(int itemId) {
		return (VoteItem) super.getForUpdate(VoteItem.class, Integer.valueOf(itemId)) ;
	}

	public VoteTerritory getVoteTerritory(int territoryId) {
		return (VoteTerritory) super.getForUpdate(VoteTerritory.class, Integer.valueOf(territoryId)) ;
	}

	public void updateVoteItem(VoteItem item) {
		super.update(item) ;
		
		//更新手工干预哦结果。
		SearchExpression se = SearchExpression.forClass(VoteItem.class) ;
		se.and(Terms.eq("voteId", item.getVoteId())) ;
		se.setCountSelectPhrase("sum(addedVoteNum)") ;				
		long addedVoteNum = super.count(se) ;
		
		
		BigVote vote = (BigVote) super.getForUpdate(BigVote.class, item.getVoteId()) ;
		VoteAssert.assertNotNull(vote, "投票不存在！") ;
		vote.setAddedVoteNum((int) addedVoteNum) ;
		
		super.update(vote) ;
		
	}

	public void updateVoteTerritory(VoteTerritory city) {
		super.update(city) ;
		
		//取消自动更新，只允许在投票项处干预，地区只能被动修改以保持数据一致性。以避免数据错乱，造成得票率超过100%出现。
		
//		//更新手工干预哦结果。
//		SearchExpression se = SearchExpression.forClass(VoteTerritory.class) ;
//		se.and(Terms.eq("voteId", city.getVoteId())) ;
//		se.setCountSelectPhrase("sum(addedVoteNum)") ;				
//		long addedVoteNum = super.count(se) ;
//		
//		BigVote vote = (BigVote) super.getForUpdate(BigVote.class, city.getVoteId()) ;
//		VoteAssert.assertNotNull(vote, "投票不存在！") ;
//		vote.setAddedVoteNum((int) addedVoteNum) ;
//		
//		super.update(vote) ;
	}
	
	public void startup(){
		ipLocationService = (IPLocationService) this.getGuzzContext().getService("IPService") ;
		
		//将投票更新操作写入日志
		if(slowUpdateService == null){
			slowUpdateService = (SlowUpdateService) this.getGuzzContext().getService(Service.FAMOUSE_SERVICE.SLOW_UPDATE) ;
		}
		
		if(logService == null){
			logService = (LogService) this.getGuzzContext().getService("logService") ;
		}
		
		CacheManager.create() ;
		
		this.voteTreeCache = CacheManager.getInstance().getCache("voteTreeCache") ;
		
		if(this.voteTreeCache == null){
			throw new VoteException("[voteTreeCache] not found in ehcache config file.") ;
		}
	}
	
	public void shutdown(){
		CacheManager.getInstance().shutdown() ;
	}

	public IVoteExtraPropertyManager getVoteExtraPropertyManager() {
		return voteExtraPropertyManager;
	}

	public void setVoteExtraPropertyManager(IVoteExtraPropertyManager voteExtraPropertyManager) {
		this.voteExtraPropertyManager = voteExtraPropertyManager;
	}

	public IAntiCheatPolicyManager getAntiCheatPolicyManager() {
		return antiCheatPolicyManager;
	}

	public void setAntiCheatPolicyManager(IAntiCheatPolicyManager antiCheatPolicyManager) {
		this.antiCheatPolicyManager = antiCheatPolicyManager;
	}
	
}
