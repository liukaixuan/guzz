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
package org.guzz.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.guzz.asm.EnchancerTest;
import org.guzz.builder.TestMainConfigBuilder;
import org.guzz.connection.C3P0DSTest;
import org.guzz.dao.se.TestInterpreter;
import org.guzz.dialect.TestH2Dialect;
import org.guzz.impl.TestLocalFileConfigServer;
import org.guzz.jdbc.TestObjectBatcher;
import org.guzz.jdbc.TestSQLBatcher;
import org.guzz.orm.mapping.TestHbmXMLBuilder;
import org.guzz.orm.mapping.TestLazyLoad;
import org.guzz.orm.rdms.TestShadowTableView;
import org.guzz.orm.rdms.TestCustomTableView;
import org.guzz.orm.se.TestBeanMap;
import org.guzz.orm.se.TestDaoRead;
import org.guzz.orm.se.TestDaoWrite;
import org.guzz.orm.se.TestSearchExpression;
import org.guzz.orm.sql.impl.TestInnerSQLBuilder;
import org.guzz.orm.sql.impl.TestSQLCompiler;
import org.guzz.orm.type.TestBlobType;
import org.guzz.orm.type.TestClobType;
import org.guzz.pojo.loader.TestBlobLoader;
import org.guzz.pojo.loader.TestClobLoader;
import org.guzz.transaction.TestTranSession;
import org.guzz.util.lb.TestRound;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestGuzz extends TestCase {
	
	public static Test suite() {
//		GuzzTestCase.configFile = "classpath:guzzmain_test1.xml" ;
		GuzzTestCase.configFile = "classpath:guzzmain_test1_annotation.xml" ;
		
		TestSuite s= new TestSuite();
		
		s.addTestSuite(TestSearchExpression.class) ;
		s.addTestSuite(TestSQLCompiler.class) ;
		s.addTestSuite(TestHbmXMLBuilder.class) ;
		s.addTestSuite(TestInterpreter.class) ;
		s.addTestSuite(C3P0DSTest.class) ;
		s.addTestSuite(TestTranSession.class) ;
		s.addTestSuite(TestMainConfigBuilder.class) ;
		s.addTestSuite(TestDaoRead.class) ;
		s.addTestSuite(TestInnerSQLBuilder.class) ;
		s.addTestSuite(TestBeanMap.class) ;
		s.addTestSuite(TestDaoWrite.class) ;
		s.addTestSuite(TestRound.class) ;
		s.addTestSuite(TestLocalFileConfigServer.class) ;
		s.addTestSuite(TestObjectBatcher.class) ;
		s.addTestSuite(TestSQLBatcher.class) ;
		s.addTestSuite(TestLazyLoad.class) ;
		s.addTestSuite(TestShadowTableView.class) ;
		s.addTestSuite(TestCustomTableView.class) ;
		s.addTestSuite(TestH2Dialect.class) ;
		s.addTestSuite(EnchancerTest.class) ;
		
		s.addTestSuite(TestClobLoader.class) ;
		s.addTestSuite(TestBlobLoader.class) ;

		s.addTestSuite(TestBlobType.class) ;
		s.addTestSuite(TestClobType.class) ;
		
		return s ;
	}

}
