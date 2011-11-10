package org.guzz.transaction;

import java.sql.SQLException;

import org.guzz.GuzzContext;
import org.guzz.dao.GuzzReadCallback;
import org.guzz.dao.GuzzWriteCallback;
import org.guzz.exception.JDBCException;
import org.guzz.util.Assert;

public class SoloWriteTemplate extends AbstractWriteTemplate{
	
	private TransactionManager transactionManager ;
	
	public SoloWriteTemplate(){		
	}
	
	public SoloWriteTemplate(TransactionManager transactionManager){
		this.setTransactionManager(transactionManager) ;
	}
	
	public <T> T executeReadInMasterDB(GuzzReadCallback<T> action) {
		ReadonlyTranSession read = this.exportReadAPI() ;
		
		try {
			//Doesn't have to proxy it. 
			//Because if the caller close it in the callback, he won't be silly to operate on it again.
			return action.doRead(exportReadAPI()) ;
		}catch (SQLException e) {
			throw new JDBCException("failed to read from master database.", e, e.getSQLState())  ;
		}finally{
			read.close() ;
		}
	}

	public <T> T executeWrite(GuzzWriteCallback<T> action) {
		WriteTranSession write = this.currentSession(true) ;
		
		try {
			T t = action.doWrite(this.createSessionProxy(write)) ;
			
			write.commit() ;
			
			return t ;
		} catch (SQLException e) {
			write.rollback() ;
			
			throw new JDBCException("failed to write.", e, e.getSQLState())  ;
		}catch (RuntimeException ex) {
			write.rollback() ;
			
			// Callback code threw application exception...
			throw ex;
		}finally{
			write.close() ;
		}
	}

	@Override
	protected <T> T doExecute(GuzzWriteCallback<T> action, boolean enforceNativeSession) throws RuntimeException {
		Assert.assertNotNull(action, "Callback object must not be null");

		WriteTranSession session = this.currentSession(true) ;

		try {
			T result = action.doWrite(enforceNativeSession ? session : this.createSessionProxy(session));
			session.commit() ;
			
			return result;
		}
		catch (SQLException ex) {
			session.rollback() ;
			
			throw new JDBCException("failed to write.", ex, ex.getSQLState())  ;
		}catch (RuntimeException ex) {
			session.rollback() ;
			
			// Callback code threw application exception...
			throw ex;
		}finally{
			session.close() ;
		}
	}

	public ReadonlyTranSession exportReadAPI() {
		return ((WriteTranSessionImpl)getTransactionManager().openRWTran(false)).exportNativeReadAPI() ;
	}

	@Override
	protected WriteTranSession getSession() {
		return getTransactionManager().openRWTran(false);
	}

	public TransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.setTransactionManager(guzzContext.getTransactionManager()) ;
	}
	
}