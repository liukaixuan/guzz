package example.business;

import org.guzz.exception.GuzzException;
import org.guzz.orm.AbstractShadowTableView;

public class MessageShadowTableView extends AbstractShadowTableView {

	public String toTableName(Object tableCondition) {
		if (tableCondition == null) { // 强制要求必须设置表分切条件，避免编程时疏忽。
			throw new GuzzException("null table conditon is not allowed.");
		}

		Integer userId = (Integer) tableCondition;

		//tb_message_${userId}
		return "tb_message_" + userId.intValue() ;
	}

}
