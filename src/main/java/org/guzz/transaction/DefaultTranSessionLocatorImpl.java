/**
 * 
 */
package org.guzz.transaction;

import org.guzz.GuzzContext;
import org.guzz.dao.WriteTemplate;
import org.guzz.util.Assert;
import org.guzz.web.context.GuzzContextAware;

/**
 * 
 * Return a WriteTemplate that will open a new <code>WriteTranSession</code> for each operation.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class DefaultTranSessionLocatorImpl implements TranSessionLocator, GuzzContextAware {
	
	private TransactionManager transactionManager ;

	public WriteTemplate currentWriteTemplate() {
		return new SoloWriteTemplate(this.getTransactionManager()) ;
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


