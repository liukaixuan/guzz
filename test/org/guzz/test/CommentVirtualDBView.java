/**
 * 
 */
package org.guzz.test;

import org.guzz.connection.AbstractVirtualDBView;
import org.guzz.exception.GuzzException;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CommentVirtualDBView extends AbstractVirtualDBView {

	public String getPhysicsDBGroupName(Object tableCondition) {
		//强制要求必须设置表分切条件，避免编程时疏忽。
		if(tableCondition == null){ 
			throw new GuzzException("null table conditon not allowed.") ;
		}
		
		User u = (User) tableCondition ;
		
		//如果用户ID为偶数，记入commentDB1, 否则写入commentDB2
		int i = u.getId() % 2 + 1 ;
		
		if(i == 1){
			return "default" ;
		}else{
			return "cargoDB.cargo2" ;
		}
	}

}
