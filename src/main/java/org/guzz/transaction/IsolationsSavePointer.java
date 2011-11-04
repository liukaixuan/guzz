/**
 * 
 */
package org.guzz.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * SavePoniters hold all old isolations for Isolation-changed connections in {@link WriteTranSession}.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class IsolationsSavePointer {
	
	private final IsolationsSavePointer parentSavePointer ;
	
	private final int isolationLevel ;
		
	public IsolationsSavePointer(IsolationsSavePointer parent, int isolationLevel){
		this.parentSavePointer = parent ;
		this.isolationLevel = isolationLevel ;
	}
	
	/**
	 * Connection vs Isolation Level should be restored later.
	 */
	private Map<Connection, Integer> changedIsolations = new HashMap<Connection, Integer>() ;
	
	public boolean hasChangedConnections(){
		return !this.changedIsolations.isEmpty() ;
	}
	
	public void setIsolation(Connection conn, int newIsolationLevel) throws SQLException{
		int old = conn.getTransactionIsolation() ;
		
		//unchanged
		if(old == newIsolationLevel) return ;
		
		conn.setTransactionIsolation(newIsolationLevel) ;
		
		if(this.parentSavePointer != null && this.parentSavePointer.getIsolationLevel() == newIsolationLevel){
			//user reset it manually
			this.changedIsolations.remove(conn) ;
		}else if(old != isolationLevel){
			//changed multiple times(The developers fetched the Connection and set its transactionIsolation directly).
			//Only record the value when this savepointer was created.
			this.changedIsolations.put(conn, old) ;
		}
	}
	
	public void restoreIsolationsToParentLevel() throws SQLException{
		for(Map.Entry<Connection, Integer> e : this.changedIsolations.entrySet()){
			e.getKey().setTransactionIsolation(e.getValue()) ;
		}
	}

	public IsolationsSavePointer getParentSavePointer() {
		return this.parentSavePointer;
	}

	public int getIsolationLevel() {
		return this.isolationLevel;
	}

}
