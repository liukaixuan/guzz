<%@page pageEncoding="UTF-8"%><%@page session="false"%><%@page import="org.guzz.sample.vote.action.model.BigVoteTree"%><%@page import="java.util.List"%><%@page import="org.guzz.sample.vote.business.*"%><%@page import="com.google.gson.Gson"%><%@page import="com.google.gson.GsonBuilder"%><%@page import="java.util.HashMap"%><%@page import="java.util.Map"%><%@page import="java.util.LinkedList"%><%

	BigVoteTree tree = (BigVoteTree) request.getAttribute("bigVoteTree") ;

	if(tree == null){
		return ;
	}
	
	boolean debug = request.getParameter("debug") != null ;

	BigVote bigVote = tree.getBigVote() ;
	List<VoteItem> items = tree.getVoteItems() ;
	List<VoteTerritory> cities = tree.getVoteTerritories() ;
	
	//由于原始对象中包含人工干预内容，这些内容不能向外暴露。此处将对象转换为Map，然后对外提供。
	
	HashMap<String, Object> m_bigVote = new HashMap<String, Object>() ;
	m_bigVote.put("id", bigVote.getId()) ;
	m_bigVote.put("voteName", bigVote.getName()) ;
	m_bigVote.put("voteNum", bigVote.getVoteNum() + bigVote.getAddedVoteNum()) ;
	m_bigVote.put("votePeople", bigVote.getVotePeople()) ;
	
	LinkedList<Map<String, Object>> m_items = new LinkedList<Map<String, Object>>() ;
	
	for(int i = 0 ; i < items.size() ; i++){
		VoteItem vi = items.get(i) ;
		HashMap<String, Object> m_vi = new HashMap<String, Object>() ;
		
		m_vi.put("id", vi.getId()) ;
		m_vi.put("name", vi.getName()) ;
		m_vi.put("showName", vi.getShowName()) ;
		m_vi.put("voteNum", vi.getVoteNum() + vi.getAddedVoteNum()) ;
		
		m_items.addLast(m_vi) ;
	}
	
	LinkedList<Map<String, Object>> m_cities = new LinkedList<Map<String, Object>>() ;
	
	for(int i = 0 ; i < cities.size() ; i++ ){
		VoteTerritory vt = cities.get(i) ;
		HashMap<String, Object> m_vt = new HashMap<String, Object>() ;
		
		m_vt.put("id", vt.getId()) ;
		m_vt.put("name", vt.getName()) ;
		m_vt.put("voteNum", vt.getVoteNum() + vt.getAddedVoteNum()) ;
		m_vt.put("votePeople", vt.getVotePeople()) ;
		
		m_cities.addLast(m_vt) ;
	}
	
	//合成json使用的对象。	
	HashMap<String, Object> root = new HashMap<String, Object>() ;
	root.put("vote", m_bigVote) ;
	root.put("voteItems", m_items) ;
	root.put("voteCities", m_cities) ;	
	
	//gson
	GsonBuilder builder = new GsonBuilder()
	.serializeNulls()
	.excludeFieldsWithoutExposeAnnotation();
	
	if(debug){
		builder.setPrettyPrinting() ;
	}
	
	Gson gson = builder.setVersion(1.0).create();
 
	String json = gson.toJson(root) ;
	
	out.print("var voteJson = " + json + " ;\n") ;

%>