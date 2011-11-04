/**
 * 
 */
package org.guzz.transaction;

import org.guzz.GuzzContext;
import org.guzz.dao.WriteTemplate;
import org.guzz.exception.GuzzException;
import org.guzz.util.Assert;
import org.guzz.web.context.GuzzContextAware;
import org.guzz.web.context.spring.SpringWriteTemplate;

/**
 * 
 * Return a WriteTemplate worked with Spring managed transaction.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SpringTranSessionLocatorImpl implements TranSessionLocator, GuzzContextAware {
	
	private TransactionManager transactionManager ;

	public WriteTemplate currentWriteTemplate() {
		try {
			return new SpringWriteTemplate(this.getTransactionManager()) ;
		} catch (Exception e) {
			throw new GuzzException("failed to create SpringWriteTemplate.", e) ;
		}
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		setTransactionManager(guzzContext.getTransactionManager()) ;
	}

	public TransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void startup() {
		Assert.assertNotNull(this.transactionManager, "transactionManager must not be null.") ;
	}

	public void shutdown() throws Exception {
		
	}
	
}


