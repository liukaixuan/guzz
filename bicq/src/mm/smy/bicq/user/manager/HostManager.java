package mm.smy.bicq.user.manager ;

/**
* deal with the reading host's information from the net.
* @author XF
* @e-mail myreligion@163.com
* @date   2003-8-14   
* @copyright Copyright 2003 XF All Rights Reserved.
*/

import mm.smy.bicq.user.* ;
import mm.smy.util.* ;
import mm.smy.bicq.MainManager ;
import mm.smy.bicq.login.LoginException ;

public class HostManager{
	private int number ;
	private boolean isPrepared = false ; //是否已经初始化。
	private Host host = null ;
	private int loadstate = UserNetManager.UNDEFINE ;
	
	private MainManager m = null ;
	private UserNetManager unm = null ;
	
	public HostManager(MainManager m_mm){
		m = m_mm ;
		number = m.getHost().getNumber() ;
		unm = m.getUserNetManager() ;
	}
	
	public void setIsPrepared(boolean m_is){
		isPrepared = m_is ;	
	}
	
	/**
	* 阻止方法，如果isPrepared = false。读取本地纪录，如果本地纪录不存在。读取网络，并且一直等待。
	* 该方法可用于启动时初始化host。
	* 如果超时时依然无法获得消息，返回null。
	* 改方法用于启动是更新本地host资料，启动以后的更新参见 UserNetManager.class
	*/
	public Host getHost() throws LoginException{
		if(isPrepared) return host ;
		//提取本地资料.....
		HostFile file = new HostFile(number) ;
		if(file.isDataExsits()){
			try{
				host = file.read() ;
				if(host != null ){
					isPrepared = true ;
					return host ;
				}
			}catch(Exception e){
				System.out.println("本地用户资料损坏==〉" + e.getMessage() ) ;
				javax.swing.JOptionPane.showMessageDialog(null,"本地资料损坏","错误提示",javax.swing.JOptionPane.ERROR_MESSAGE ) ;	
			}
			
		}
		//不存在，或是读取时出错，我们从网络上下载，并且阻止进程。
		
		System.out.println("download host's information from the server....") ;	
			
		SmyTimer timer = new SmyTimer() ;
		timer.setTimerListener(new HostWaitListener(timer)) ;
		timer.setInterval(UserNetManager.DEFAULT_INTERVAL) ;
		timer.setTotalTime(UserNetManager.DEFAULT_TOTAL_TIME + 3000) ;
		timer.startTimer() ;
		
		loadstate = UserNetManager.WAITING ;
		unm.readHostNet() ;
		
		while(true){
				try{
					synchronized(this){
						wait(500) ;	
					}					
				}catch(Exception e){
					System.out.println("Exception in wait() in HostManager") ;						
				}

			if(loadstate == UserNetManager.TIMEOUT){
				//break ;
				throw new LoginException("HostManager reports:读取host资料时网络超时！") ;	
			}else if(loadstate == UserNetManager.FINISHED){
				isPrepared = true ;
				loadstate = UserNetManager.UNDEFINE ;
				System.out.println("成功的下载了host资料，在HostManager中。") ;
				return m.getHost() ;
				//break ;	
			}else if(loadstate == UserNetManager.UNDEFINE){
				mm.smy.bicq.debug.BugWriter.log("HostManager",new Exception("状态错误"),"在读host的等待中发现loadstate=undefine，该状态不应该出现！") ;	
				throw new LoginException("程序错误，出现了不该有的状态，请检查日志。") ;
			}
		}
		
	}
	
	private class HostWaitListener implements TimerListener{
		private SmyTimer timer = null ;
		
		public HostWaitListener(SmyTimer m_timer){
			timer = m_timer ;
		}
		
		public void timeElapsed(){
			if(unm.getHosResult() != unm.WAITING){
				timer.stopTimer() ;
				System.out.println("&&&&&&&&&&&&&&&&&&timer is stopped in the hostmanager") ;
				loadstate = unm.getHosResult() ;
			}
		}
		
		public void timeOut(){
			timer.stopTimer() ;
			System.out.println("&&&&&&&&&&&&&&&&&&timeout in the hostmanager") ;
			loadstate = unm.TIMEOUT ;
		}
		
	}
	
	
}

