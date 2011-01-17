/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.guzz.builder;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Properties;

import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceException;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.GuzzContextImpl;
import org.guzz.annotations.GenericGenerator;
import org.guzz.annotations.GenericGenerators;
import org.guzz.annotations.Parameter;
import org.guzz.annotations.Table;
import org.guzz.connection.DBGroup;
import org.guzz.exception.DataTypeException;
import org.guzz.exception.GuzzException;
import org.guzz.exception.IllegalParameterException;
import org.guzz.id.Configurable;
import org.guzz.id.IdentifierGenerator;
import org.guzz.id.IdentifierGeneratorFactory;
import org.guzz.id.SequenceIdGenerator;
import org.guzz.id.TableMultiIdGenerator;
import org.guzz.lang.NullValue;
import org.guzz.orm.Business;
import org.guzz.orm.CustomTableView;
import org.guzz.orm.ShadowTableView;
import org.guzz.orm.mapping.ObjectMappingUtil;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.rdms.SimpleTable;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.util.ArrayUtil;
import org.guzz.util.Assert;
import org.guzz.util.StringUtil;
import org.guzz.util.javabean.BeanCreator;
import org.guzz.util.javabean.BeanWrapper;
import org.guzz.util.javabean.JavaBeanWrapper;
import org.guzz.web.context.GuzzContextAware;

/**
 * 
 * parse orm definition in JPA2.0 annotation style.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class JPA2AnnotationsBuilder {
	private static final Log log = LogFactory.getLog(JPA2AnnotationsBuilder.class) ;
	
	protected static void parseClassForAttributes(GuzzContextImpl gf, POJOBasedObjectMapping map, Business business, DBGroup dbGroup, SimpleTable st, Class domainClass){
		//逐层分析，先分析分类。
		Class parentCls = domainClass.getSuperclass() ;
		if(parentCls != null && parentCls.isAnnotationPresent(MappedSuperclass.class)){
			parseClassForAttributes(gf, map, business, dbGroup, st, parentCls) ;
		}
		
		javax.persistence.Access access = (javax.persistence.Access) domainClass.getAnnotation(javax.persistence.Access.class) ;
		AccessType accessType = null ;
				
		if(access == null){
			//检查有没有@Id，如果有以@Id为准判断按照field还是property统计映射。
			boolean hasColumnAOnField = false ;
			boolean hasColumnAOnProperty = false ;
			
			//detect from @Id, field first.
			Field[] fs = domainClass.getDeclaredFields() ;
			
			for(Field f : fs){
				if(f.isAnnotationPresent(Transient.class)) continue ;
				if(f.isAnnotationPresent(javax.persistence.Id.class)){
					accessType = AccessType.FIELD ;
					break ;
				}else if(f.isAnnotationPresent(javax.persistence.Column.class)){
					hasColumnAOnField = true ;
				}else if(f.isAnnotationPresent(org.guzz.annotations.Column.class)){
					hasColumnAOnField = true ;
				}
			}
			
			if(accessType == null){
				Method[] ms = domainClass.getDeclaredMethods() ;
				for(Method m : ms){
					if(m.isAnnotationPresent(Transient.class)) continue ;
					if(m.isAnnotationPresent(javax.persistence.Id.class)){
						accessType = AccessType.PROPERTY ;
						break ;
					}else if(m.isAnnotationPresent(javax.persistence.Column.class)){
						hasColumnAOnProperty = true ;
					}else if(m.isAnnotationPresent(org.guzz.annotations.Column.class)){
						hasColumnAOnProperty = true ;
					}
				}
			}
			
			//没有@Id定义。以@Column定义为准。如果@Column也没有，按照field处理。
			if(accessType == null){
				if(hasColumnAOnField){
					accessType = AccessType.FIELD ;
				}else if(hasColumnAOnProperty){
					accessType = AccessType.PROPERTY ;
				}else{
					accessType = AccessType.FIELD ;
				}
			}
		}else{
			accessType = access.value() ;
		}
		
		//orm by field
		if(accessType == AccessType.FIELD){
			Field[] fs = domainClass.getDeclaredFields() ;
			
			for(Field f : fs){
				if(f.isAnnotationPresent(Transient.class)) continue ;
				if(Modifier.isTransient(f.getModifiers())) continue ;
				if(Modifier.isStatic(f.getModifiers())) continue ;
				
				if(f.isAnnotationPresent(javax.persistence.Id.class)){
					addIdMapping(gf, map, st, dbGroup, f.getName(), domainClass, f) ;
				}else{
					addPropertyMapping(gf, map, st, f.getName(), f, f.getType()) ;
				}
			}
		}else{
			Method[] ms = domainClass.getDeclaredMethods() ;
			for(Method m : ms){
				if(m.isAnnotationPresent(Transient.class)) continue ;
				if(Modifier.isTransient(m.getModifiers())) continue ;
				if(Modifier.isStatic(m.getModifiers())) continue ;
				if(Modifier.isPrivate(m.getModifiers())) continue ;
				
				String methodName = m.getName() ;
				String fieldName = null ;
				
				if(m.getParameterTypes().length != 0){
					continue ;
				}else if(Void.TYPE.equals(m.getReturnType())){
					continue ;
				}
				
				if(methodName.startsWith("get")){
					fieldName = methodName.substring(3) ;
				}else if(methodName.startsWith("is")){//对is boolean的支持
					Class retType = m.getReturnType() ;
					
					if(boolean.class.isAssignableFrom(retType)){
						fieldName = methodName.substring(2) ;
					}else if(Boolean.class.isAssignableFrom(retType)){
						fieldName = methodName.substring(2) ;
					}
				}
				
				//not a javabean read method
				if(fieldName == null){
					continue ;
				}
				
				fieldName = java.beans.Introspector.decapitalize(fieldName) ;
				
				if(m.isAnnotationPresent(javax.persistence.Id.class)){
					addIdMapping(gf, map, st, dbGroup, fieldName, domainClass, m) ;
				}else{
					addPropertyMapping(gf, map, st, fieldName, m, m.getReturnType()) ;
				}
			}
		}
		
		//处理attribute override
		AttributeOverride gao = (AttributeOverride) domainClass.getAnnotation(AttributeOverride.class) ;
		AttributeOverrides gaos = (AttributeOverrides) domainClass.getAnnotation(AttributeOverrides.class) ;
		AttributeOverride[] aos = gao == null ? new AttributeOverride[0] : new AttributeOverride[]{gao} ;
		if(gaos != null){
			ArrayUtil.addToArray(aos, gaos.value()) ;
		}
		
		for(AttributeOverride ao : aos){
			String name = ao.name() ;
			Column col = ao.column() ;
			
			TableColumn tc = st.getColumnByPropName(name) ;
			Assert.assertNotNull(tc, "@AttributeOverride cann't override a attribute that doesn't exist. The attribute is:" + name) ;
			
			//update is remove and add
			st.removeColumn(tc) ;
			
			//change the column name in the database.
			tc.setColName(col.name()) ;
			
			st.addColumn(tc) ;
		}
	}
	
	protected static void addIdMapping(GuzzContextImpl gf, POJOBasedObjectMapping map, SimpleTable st, DBGroup dbGroup, String name, Class domainClas, AnnotatedElement element){
		javax.persistence.Column pc = (javax.persistence.Column) element.getAnnotation(javax.persistence.Column.class) ;
		org.guzz.annotations.Column gc = (org.guzz.annotations.Column) element.getAnnotation(org.guzz.annotations.Column.class) ;
		
		String type = gc == null ? null : gc.type() ;
		String column = pc == null ? null : pc.name() ;
				
		if(StringUtil.isEmpty(column)){
			column = name ;
		}
		
		TableColumn col = st.getColumnByPropName(name) ;
		boolean newId = false ;
		
		if(col == null){
			newId = true ;
			col = new TableColumn(st) ;
		}else{
			log.info("override @Id in the parent class of [" + st.getBusinessName() + "].") ;
		}
		
		col.setColName(column) ;
		col.setPropName(name) ;
		col.setType(type) ;
		col.setAllowInsert(true) ;
		col.setAllowUpdate(true) ;
		col.setLazy(false) ;
		map.initColumnMapping(col, null) ;

		if(newId){
			st.addPKColumn(col) ;
		}		

		//@Id generator
		javax.persistence.GeneratedValue pgv = (javax.persistence.GeneratedValue) element.getAnnotation(javax.persistence.GeneratedValue.class) ;
		if(pgv == null){
			pgv = (javax.persistence.GeneratedValue) domainClas.getAnnotation(javax.persistence.GeneratedValue.class) ;
		}
		
		//If @GeneratedValue is not defined, use auto.
		GenerationType gt = GenerationType.AUTO ;
		String generatorName = null ;
		
		if(pgv != null){
			gt = pgv.strategy() ;
			generatorName = pgv.generator() ;
		}
		
		Properties idProperties = new Properties() ;
		String igCls ;
		
		if(gt == GenerationType.AUTO){
			//检查是不是指向了guzz扩展的@GenericGenerator
			if(StringUtil.notEmpty(generatorName)){
				GenericGenerator ggg = (GenericGenerator) element.getAnnotation(GenericGenerator.class) ;
				if(ggg != null && !generatorName.equals(ggg.name())){
					ggg = null ;
				}
				
				if(ggg == null){
					//retreive @Id from GlobalContext
					Object g = gf.getGlobalIdGenerator(generatorName) ;
					
					//should be GenericGenerator
					if(!(g instanceof GenericGenerator)){
						throw new IllegalParameterException("The Id Generator [" + generatorName + "] should be of type @org.guzz.annotations.GenericGenerator. domain class:" + domainClas.getName()) ;
					}
					
					ggg = (GenericGenerator) g ;
				}
				
				igCls = ggg.strategy() ;
				Parameter[] ps = ggg.parameters() ;
					
				for(Parameter p : ps){
					idProperties.setProperty(p.name(), p.value()) ;
				}
			}else{
				//native的generator由dialect来定。
				igCls = "native" ;
			}
		}else if(gt == GenerationType.IDENTITY){
			igCls = "identity" ;
		}else if(gt == GenerationType.SEQUENCE){
			igCls = "sequence" ;
			
			javax.persistence.SequenceGenerator psg = (javax.persistence.SequenceGenerator) element.getAnnotation(javax.persistence.SequenceGenerator.class) ;
			if(psg == null){
				Object sg = gf.getGlobalIdGenerator(generatorName) ;
				Assert.assertNotNull(sg, "@javax.persistence.SequenceGenerator not found for sequenced @Id. domain class:" + domainClas.getName()) ;
				
				if(sg instanceof SequenceGenerator){
					psg = (SequenceGenerator) sg ;
				}else{
					throw new IllegalParameterException("The Id Generator [" + generatorName + "] should be of type @javax.persistence.SequenceGenerator. domain class:" + domainClas.getName()) ;
				}
			}
			
			idProperties.setProperty(SequenceIdGenerator.PARAM_SEQUENCE, psg.sequenceName()) ;
			
			idProperties.setProperty("catalog", psg.catalog()) ;
			idProperties.setProperty("allocationSize", String.valueOf(psg.allocationSize())) ;
			idProperties.setProperty("initialValue", String.valueOf(psg.initialValue())) ;
			
			//we need db_group param, but the JPA won't give us.
		}else if(gt == GenerationType.TABLE){
			igCls = "hilo.multi" ;
			
			TableGenerator pst = (TableGenerator) element.getAnnotation(TableGenerator.class) ;
			if(pst == null){
				Object sg = gf.getGlobalIdGenerator(generatorName) ;
				Assert.assertNotNull(sg, "@javax.persistence.TableGenerator not found for hilo.multi @Id. domain class:" + domainClas.getName()) ;
				
				if(sg instanceof TableGenerator){
					pst = (TableGenerator) sg ;
				}else{
					throw new IllegalParameterException("The Id Generator [" + generatorName + "] should be of type @javax.persistence.TableGenerator. domain class:" + domainClas.getName()) ;
				}
			}
			
			idProperties.setProperty("catalog", pst.catalog()) ;
			idProperties.setProperty("schema", pst.schema()) ;
			idProperties.setProperty(TableMultiIdGenerator.TABLE, pst.table()) ;
			idProperties.setProperty(TableMultiIdGenerator.PK_COLUMN_NAME, pst.pkColumnName()) ;
			idProperties.setProperty(TableMultiIdGenerator.PK_COLUMN_VALUE, pst.pkColumnValue()) ;
			idProperties.setProperty(TableMultiIdGenerator.COLUMN, pst.valueColumnName()) ;
			idProperties.setProperty(TableMultiIdGenerator.MAX_LO, String.valueOf(pst.allocationSize())) ;
			//we need db_group param, but the JPA won't give us.
			
			idProperties.setProperty("initialValue", String.valueOf(pst.initialValue())) ;
		}else{
			throw new GuzzException("unknown @javax.persistence.GenerationType:" + gt) ;
		}
		
		if("native".equals(igCls)){
			igCls = dbGroup.getDialect().getNativeIDGenerator() ;
		}
		
		String realClassName = (String) IdentifierGeneratorFactory.getGeneratorClass(igCls) ;
		Assert.assertNotNull(realClassName, "unknown Id generator:" + igCls) ;
		
		IdentifierGenerator ig = (IdentifierGenerator) BeanCreator.newBeanInstance(realClassName) ;
		
		if(ig instanceof Configurable){
			((Configurable) ig).configure(dbGroup.getDialect(), map, idProperties) ;						
		}
		
		//register callback for GuzzContext's full starting.
		if(ig instanceof GuzzContextAware){
			gf.registerContextStartedAware((GuzzContextAware) ig) ;
		}
		
		st.setIdentifierGenerator(ig) ;
	}
	
	protected static void addPropertyMapping(GuzzContextImpl gf, POJOBasedObjectMapping map, SimpleTable st, String name, AnnotatedElement element, Class dataType){
		javax.persistence.Column pc = element.getAnnotation(javax.persistence.Column.class) ;
		javax.persistence.Basic pb = element.getAnnotation(javax.persistence.Basic.class) ;
		javax.persistence.Enumerated pe = element.getAnnotation(javax.persistence.Enumerated.class) ;
		org.guzz.annotations.Column gc = element.getAnnotation(org.guzz.annotations.Column.class) ;
		
		String type = gc == null ? null : gc.type() ;
		String nullValue = gc == null ? null : gc.nullValue() ;
		String column = pc == null ? null : pc.name() ;
		boolean lazy = pb == null ? false : pb.fetch() == FetchType.LAZY ;
		Class loader = gc == null ? null : gc.loader() ;
		
		if(dataType.isEnum()){
			EnumType etype = EnumType.ORDINAL ;
			
			if(pe != null){
				etype = pe.value() ;
			}
			
			if(etype == EnumType.ORDINAL){
				type = "enum.ordinal|" + dataType.getName() ;
			}else{
				type = "enum.string|" + dataType.getName() ;
			}
		}
		
		boolean insertIt = pc == null ? true : pc.insertable() ;
		boolean updateIt = pc == null ? true : pc.updatable() ;
		
		if(StringUtil.isEmpty(column)){
			column = name ;
		}
		
		TableColumn col = st.getColumnByPropName(name) ;
		if(col != null){
			log.warn("field/property [" + name + "] already exsits in the parent class of [" + st.getBusinessName() + "]. Ignore it.") ;
			
			return ;
		}
		
		if(loader == null || NullValue.class.isAssignableFrom(loader)){
			loader = null ;
		}
		
		try{
			col = ObjectMappingUtil.createTableColumn(gf, map, name, column, type, loader == null ? null : loader.getName()) ;
			col.setNullValue(nullValue) ;
			col.setAllowInsert(insertIt) ;
			col.setAllowUpdate(updateIt) ;
			col.setLazy(lazy) ;
			
			st.addColumn(col) ;
		}catch(DataTypeException dte){
			//遇到了不支持的JPA关联集合类型（Map, Set等）
			if(log.isDebugEnabled()){
				log.debug("Unsupported data type is found in annotation, property is:[" + name + "], business is:[" + st.getBusinessName() + "]. Ignore this property.", dte) ;
			}else{
				log.warn("Ignore unsupported data type in annotation, property is:[" + name + "], business is:[" + st.getBusinessName() + "], msg is:" + dte.getMessage()) ;
			}
		}
	}
	
	/**
	 * Build the {@link Business} and the {@link Table} information of the domain class.
	 * <p/>
	 * We have to seperate this operation from the {@link #parseClassForAttributes(GuzzContextImpl, POJOBasedObjectMapping, Business, DBGroup, SimpleTable, Class)}
	 *  to get the final "dbGroup" after the inherited tree.
	 */
	protected static void parseClassForEntityTable(DomainInfo info, Class domainClass){
		//逐层分析，先分析分类。
		Class parentCls = domainClass.getSuperclass() ;
		if(parentCls != null && parentCls.isAnnotationPresent(MappedSuperclass.class)){
			parseClassForEntityTable(info, parentCls) ;
		}
		
		//分析本层类
		org.guzz.annotations.Entity ge = (org.guzz.annotations.Entity) domainClass.getAnnotation(org.guzz.annotations.Entity.class) ;
		org.guzz.annotations.Table gt = (org.guzz.annotations.Table) domainClass.getAnnotation(org.guzz.annotations.Table.class) ;
		javax.persistence.Table pt = (javax.persistence.Table) domainClass.getAnnotation(javax.persistence.Table.class) ;
		
		if(ge != null){
			info.businessName = ge.businessName() ;
			
			Class m_interpreter = ge.interpreter() ;
			if(m_interpreter != null && !NullValue.class.isAssignableFrom(m_interpreter)){
				info.interpreter = m_interpreter ;
			}
		}
		
		if(pt != null){
			if(StringUtil.notEmpty(pt.name())){
				info.tableName = pt.name() ;
			}
		}
		
		if(gt != null){
			if(StringUtil.notEmpty(gt.dbGroup())){
				info.dbGroup = gt.dbGroup() ;
			}
			if(StringUtil.notEmpty(gt.name())){
				info.tableName = gt.name() ;
			}
			if(gt.shadow() != null){
				info.shadow = gt.shadow() ;
			}
			
			info.dynamicUpdate = gt.dynamicUpdate() ;
		}
		
	}
	
	static class DomainInfo{
		public Class interpreter ;
		public Class shadow ;
		public String tableName ;
		public boolean dynamicUpdate ;
		public String dbGroup ;
		public String businessName ;
	}
		
	public static POJOBasedObjectMapping parseDomainClass(final GuzzContextImpl gf, String dbGroupName, String businessName, Class domainCls) throws ClassNotFoundException{
		javax.persistence.Entity pe = (javax.persistence.Entity) domainCls.getAnnotation(javax.persistence.Entity.class) ;
		if(pe == null){
			throw new PersistenceException("no @javax.persistence.Entity annotation found for class:[" + domainCls + "]") ;
		}
		
		DomainInfo info = new DomainInfo() ;
		parseClassForEntityTable(info, domainCls) ;
	
		//xml definition own high priority.
		String m_dbGroupName = StringUtil.isEmpty(dbGroupName) ? info.dbGroup : dbGroupName ; 
		String m_businessName = StringUtil.isEmpty(businessName) ? info.businessName : businessName ; 		
		String tableName = info.tableName ;
		Class shadow = info.shadow ;
		boolean dynamicUpdate = info.dynamicUpdate ;
		
		if(StringUtil.isEmpty(tableName)){
			//According to JPA spefication, we use class's short name as the table name.
			tableName = domainCls.getSimpleName() ;
		}	
		
		//The Business name must be defined either in annotation or guzz.xml, or both.
		Assert.assertNotEmpty(m_businessName, "business name must be defined. you can define it either with org.guzz.annotations.Entity annotation in domain class or a-bussiness's attribute in guzz.xml") ;
		Business business = gf.instanceNewGhost(m_businessName, m_dbGroupName, info.interpreter, domainCls) ;
		
		DBGroup dbGroup = gf.getDBGroup(business.getDbGroup()) ;
		Assert.assertNotNull(dbGroup, "unknown dbGroup:[" + business.getDbGroup() + "] for domain class:" + domainCls.getName()) ;
		
		final SimpleTable st = new SimpleTable(dbGroup.getDialect()) ;
		st.setTableName(tableName) ;
		st.setDynamicUpdate(dynamicUpdate) ;
		
		final POJOBasedObjectMapping map = new POJOBasedObjectMapping(gf, dbGroup, st) ;
		
		business.setTable(st) ;
		business.setMapping(map) ;
		
		//关联business名称
		if(business.getName() != null){
			st.setBusinessName(business.getName()) ;
		}else{
			st.setBusinessName(business.getDomainClass().getName()) ;
		}
		
		//构建table信息
		JavaBeanWrapper configBeanWrapper = BeanWrapper.createPOJOWrapper(domainCls) ;
		business.setConfiguredBeanWrapper(configBeanWrapper) ;
		
		map.setBusiness(business) ;
		
		if(shadow != null && !NullValue.class.isAssignableFrom(shadow)){
			ShadowTableView sv = (ShadowTableView) BeanCreator.newBeanInstance(shadow) ;
			sv.setConfiguredTableName(tableName) ;
			
			//CustomTableView是一类特殊的ShadowTableView
			if(sv instanceof CustomTableView){
				CustomTableView ctv = (CustomTableView) sv ;
				ctv.setConfiguredObjectMapping(map) ;
				
				st.setCustomTableView(ctv) ;
			}

			st.setShadowTableView(sv) ;
			gf.getShadowTableViewManager().addShadowView(sv) ;
		}
		
		//构建映射属性。
		parseClassForAttributes(gf, map, business, dbGroup, st, domainCls) ;
		
		//check that the class must own a @Id.
		Assert.assertNotNull(st.getIdentifierGenerator(), "no @javax.persistence.Id annotation found for class:[" + domainCls + "]") ;
		
		return map ;
	}
	
	/**
	 * Only retrieve {@link SequenceGenerator} and {@link TableGenerator} in the type declaration.
	 */
	public static void parseForIdGenerators(final Map idGenerators, Class domainCls) throws ClassNotFoundException{
		javax.persistence.Entity pe = (javax.persistence.Entity) domainCls.getAnnotation(javax.persistence.Entity.class) ;
		javax.persistence.MappedSuperclass pm = (javax.persistence.MappedSuperclass) domainCls.getAnnotation(javax.persistence.MappedSuperclass.class) ;
				
		if(pe == null && pm == null){
			log.debug("Parsing for id generator ends at class:" + domainCls.getName()) ;
			return ;
		}
		
		//parse the super class first, then add/replace it from the subclass.
		Class superCls = domainCls.getSuperclass() ;
		if(superCls != null && superCls.isAnnotationPresent(javax.persistence.MappedSuperclass.class)){
			parseForIdGenerators(idGenerators, superCls) ;
		}
		
		//parse for this class.
		SequenceGenerator psg = (SequenceGenerator) domainCls.getAnnotation(SequenceGenerator.class) ;
		TableGenerator 	  tsg = (TableGenerator) domainCls.getAnnotation(TableGenerator.class) ;
		
		GenericGenerator[] ggs = new GenericGenerator[0] ;
		
		//add @GenericGenerators
		if(domainCls.getPackage().isAnnotationPresent(GenericGenerators.class)){
			ggs = (GenericGenerator[]) ArrayUtil.addToArray(ggs, ((GenericGenerators) domainCls.getPackage().getAnnotation(GenericGenerators.class)).value()) ;
		}
		if(domainCls.isAnnotationPresent(GenericGenerators.class)){
			ggs = (GenericGenerator[]) ArrayUtil.addToArray(ggs, ((GenericGenerators) domainCls.getAnnotation(GenericGenerators.class)).value()) ;
		}
		
		//add @GenericGenerator
		if(domainCls.getPackage().isAnnotationPresent(GenericGenerator.class)){
			ggs = (GenericGenerator[]) ArrayUtil.addToArray(ggs, (GenericGenerator) domainCls.getPackage().getAnnotation(GenericGenerator.class)) ;
		}
		if(domainCls.isAnnotationPresent(GenericGenerator.class)){
			ggs = (GenericGenerator[]) ArrayUtil.addToArray(ggs, (GenericGenerator) domainCls.getAnnotation(GenericGenerator.class)) ;
		}
		
		if(psg != null){
			String name = psg.name() ;
			if(idGenerators.get(name) != null && log.isDebugEnabled()){
				log.debug("override @Id annotation:[" + idGenerators.get(name) + "] with @SequenceGenerator, name is:" + name) ;
			}
			
			idGenerators.put(name, psg) ;
		}
		
		if(tsg != null){
			String name = tsg.name() ;
			if(idGenerators.get(name) != null && log.isDebugEnabled()){
				log.debug("override @Id annotation:[" + idGenerators.get(name) + "] with @TableGenerator, name is:" + name) ;
			}
			
			idGenerators.put(name, tsg) ;
		}
		
		if(ggs.length > 0){
			for(GenericGenerator gg : ggs){
				String name = gg.name() ;
				if(idGenerators.get(name) != null && log.isDebugEnabled()){
					log.debug("override @Id annotation:[" + idGenerators.get(name) + "] with @GenericGenerator, name is:" + name) ;
				}
				
				idGenerators.put(name, gg) ;
			}
		}
	}
	
}
