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
package org.guzz.orm.mapping;

import java.util.List;

import org.guzz.Configuration;
import org.guzz.GuzzContext;
import org.guzz.GuzzContextImpl;
import org.guzz.bytecode.LazyPropChangeDetector;
import org.guzz.orm.rdms.SimpleTable;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.pojo.DynamicUpdatable;
import org.guzz.test.Book;
import org.guzz.test.DBBasedTestCase;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TestLazyLoad extends DBBasedTestCase {
	
	public void testLazyPropWithDynamicUpdate() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		TransactionManager tm = gf.getTransactionManager() ;
		
		SearchExpression se = SearchExpression.forClass(Book.class) ;
		se.and(Terms.eq("title", "book title 1")) ;
				
		ReadonlyTranSession session = tm.openDelayReadTran() ;
		
		WriteTranSession write = tm.openRWTran(true) ;
		
		List books = session.list(se) ;
		assertEquals(books.size(), 1) ;
		
		Book b = (Book) books.get(0) ;
		
//		assertTrue(b instanceof LazyPropChangeDetector) ;
		assertTrue(b instanceof DynamicUpdatable) ;
		
		assertEquals(b.getISDN(), "isdn-b1") ;
		assertEquals(((DynamicUpdatable) b).getChangedProps().length, 0) ;
//		assertEquals(((LazyPropChangeDetector) b).getChangedLazyProps().length, 0) ;
		
		assertEquals(b.getContent(), "book content 1") ;
		assertEquals(((DynamicUpdatable) b).getChangedProps().length, 0) ;
//		assertEquals(((LazyPropChangeDetector) b).getChangedLazyProps().length, 0) ;
		
		assertEquals(b.getContent(), "book content 1") ;
		assertEquals(b.getContent(), "book content 1") ;
		assertEquals(b.getContent(), "book content 1") ;
		assertEquals(((DynamicUpdatable) b).getChangedProps().length, 0) ;
//		assertEquals(((LazyPropChangeDetector) b).getChangedLazyProps().length, 0) ;
		
		b.setTitle("new title 1") ;
		assertEquals(((DynamicUpdatable) b).getChangedProps().length, 1) ;
//		assertEquals(((LazyPropChangeDetector) b).getChangedLazyProps().length, 0) ;
		
		//test that the lazily loaded property won't change without explicitly call setXXX(...)
		write.update(b) ;
		assertEquals(((Book) write.findObjectByPK(Book.class, 1)).getTitle(), "new title 1") ;
		assertEquals(((Book) write.findObjectByPK(Book.class, 1)).getContent(), "book content 1") ;
		
		b.setContent("new content 1") ;
		b.setContent("new content 1") ;
		b.setContent("new content 1") ;
		assertEquals(((DynamicUpdatable) b).getChangedProps().length, 2) ;
//		assertEquals(((LazyPropChangeDetector) b).getChangedLazyProps().length, 1) ;
		
		write.update(b) ;
		assertEquals(((Book) write.findObjectByPK(Book.class, 1)).getTitle(), "new title 1") ;
		assertEquals(((Book) write.findObjectByPK(Book.class, 1)).getContent(), "new content 1") ;
		
		write.close() ;
		session.close() ;
		((GuzzContextImpl) gf).shutdown() ;
	}
	
	public void testLazyPropNoDynamicUpdate() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		POJOBasedObjectMapping map = (POJOBasedObjectMapping) gf.getObjectMappingManager().getObjectMappingByName("book") ;
		((SimpleTable) map.getBusiness().getTable()).setDynamicUpdate(false) ;
		
		TransactionManager tm = gf.getTransactionManager() ;
		
		SearchExpression se = SearchExpression.forClass(Book.class) ;
		se.and(Terms.eq("title", "book title 1")) ;
				
		ReadonlyTranSession session = tm.openDelayReadTran() ;
		
		WriteTranSession write = tm.openRWTran(true) ;
		
		List books = session.list(se) ;
		assertEquals(books.size(), 1) ;
		
		Book b = (Book) books.get(0) ;
		
		assertTrue(b instanceof LazyPropChangeDetector) ;
		
		assertEquals(b.getISDN(), "isdn-b1") ;
		assertEquals(((LazyPropChangeDetector) b).getChangedLazyProps().length, 0) ;
		
		assertEquals(b.getContent(), "book content 1") ;
		assertEquals(((LazyPropChangeDetector) b).getChangedLazyProps().length, 0) ;
		
		assertEquals(b.getContent(), "book content 1") ;
		assertEquals(b.getContent(), "book content 1") ;
		assertEquals(b.getContent(), "book content 1") ;
		assertEquals(((LazyPropChangeDetector) b).getChangedLazyProps().length, 0) ;
		
		b.setTitle("new title 1") ;
		assertEquals(((LazyPropChangeDetector) b).getChangedLazyProps().length, 0) ;
		
		//test that the lazily loaded property won't change without explicitly call setXXX(...)
		write.update(b) ;
		assertEquals(((Book) write.findObjectByPK(Book.class, 1)).getTitle(), "new title 1") ;
		assertEquals(((Book) write.findObjectByPK(Book.class, 1)).getContent(), "book content 1") ;
		
		b.setContent("new content 1") ;
		b.setContent("new content 1") ;
		b.setContent("new content 1") ;
		assertEquals(((LazyPropChangeDetector) b).getChangedLazyProps().length, 1) ;
		
		write.update(b) ;
		assertEquals(((Book) write.findObjectByPK(Book.class, 1)).getTitle(), "new title 1") ;
		assertEquals(((Book) write.findObjectByPK(Book.class, 1)).getContent(), "new content 1") ;
		
		write.close() ;
		session.close() ;
		((GuzzContextImpl) gf).shutdown() ;
	}

}
