package mm.smy.util ;

/**
*
*
*
*
*
*
*
*/

public class SmyTimer implements Runnable{
	private TimerListener listener = null ;
	private int interval = 5 ; //默认的时间间隔是5ms
	private long totaltime = -1 ; //-1表示永远不超时
	private boolean isRunning = true ;
	private Thread t = null ;
	private Object name = null ;
	
	public SmyTimer(TimerListener tl,int m_interval,long total_time){ 
		listener = tl ;	
		interval = m_interval ;
		totaltime = total_time ;
	}
	public SmyTimer(TimerListener tl, int m_interval){
		listener = tl ;
		interval = m_interval ;
	}
	public SmyTimer(TimerListener tl){
		listener = tl ;
	}
	public SmyTimer(){
	
	}
	
	
	public void startTimer(){
		t = new Thread(this) ;
		t.setDaemon(true) ;
		t.start() ;
	}
	
	public void stopTimer(){
		isRunning = false ;
		t = null ;
	}

	public void run(){
		
		if(listener == null) throw new UnPreparedException("缺少TimerListener") ;
		
		if(interval <= 0 ) interval = 1 ;
		
		int i = 0 ;
		
		for(i = 0 ; isRunning && ((i < totaltime/interval)||(totaltime < 0)) ; i++){
			synchronized(this){
				try{
					wait(interval) ;
				}catch(Exception e)	
				{}
			}
			listener.timeElapsed() ;
		}
		if( (i >= totaltime/interval) && totaltime > 0){
			stopTimer() ;
			listener.timeOut() ;
		}
	}
	
	public void setTimerListener(TimerListener tl){
		listener = tl ;
	}
	public void setInterval(int m_interval){
		interval = m_interval ;
	}
	public void setTotalTime(long total_time){
		totaltime = total_time ;
	}
	
	public void setName(Object m_name){ name = m_name ; }
	public Object getName(){ return name ; }
	
	public TimerListener getTimerListener(){ return listener ; }
	public int getInterval(){ return interval ; }
	public long getTotalTime(){ return totaltime ; }
	
	
	
	
	
}
