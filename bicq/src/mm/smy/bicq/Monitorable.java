package mm.smy.bicq ;

/**
* 可对Monitor进行监控。
*
*
*
*/

import mm.smy.bicq.message.ReceivedMessage ;

public interface Monitorable{
	public void sendReceivedMessage(ReceivedMessage rm)	;
	public void sendMonitorException(Exception e) ;
	public void close() ;

}
