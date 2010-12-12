/**
 * 
 */
package org.guzz.service.db.impl;

import org.guzz.jdbc.JDBCTemplate;
import org.guzz.service.core.impl.IncUpdateBusiness;
import org.guzz.service.db.SlowUpdateServer;
import org.guzz.transaction.WriteTranSession;

/**
 * 
 * Handle warnings and exceptions while running {@link SlowUpdateServer} service.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface UpdateExceptionHandlerService {
	
	/**
	 * notify that a update operation affected 0 rows in the main database.
	 * 
	 * @param writeSession the current rw session.
	 * @param jdbcTemplate jdbc connection to the underly database
	 * @param business temp record to be updated.
	 * 
	 * @return true: re-execute the update operation again. <br/>false: ignore it, you have already handled it .
	 */
	public boolean recordNotFoundInMainDB(WriteTranSession writeSession, JDBCTemplate jdbcTemplate, IncUpdateBusiness business) ;
	
	/**
	 * Handle the exceptions.
	 * 
	 * @throws Exception re-throw the original exception or throw a new one, if you want all operations to be undone, and wait for manual handling.
	 */
	public void exceptionCaught(Exception e) throws Exception ;

}
