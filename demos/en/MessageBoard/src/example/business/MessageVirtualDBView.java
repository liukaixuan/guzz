package example.business;

import org.guzz.connection.AbstractVirtualDBView;
import org.guzz.exception.GuzzException;

public class MessageVirtualDBView extends AbstractVirtualDBView {
    
    public String getPhysicsDBGroupName(Object tableCondition) {
		if (tableCondition == null) {
			throw new GuzzException("null table conditon is not allowed.");
		}
		
		int userId = (Integer) tableCondition;
		
		if(userId == 1){
			 //store lucy's messages in the default database.
			return "default" ;
		}else{
			 //store others in the userDB database.
			return "userDB" ;
		}
    }

}
