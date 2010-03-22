package mm.smy.bicq ;

/**
* this class is for listen the BICQ messages in port.
* We collect all UDP messages,and pass it to main thread.
* Nothing else.
* @author XF
* @e-mail myreligion@163.com
* @date   2003-8-15   
* @copyright Copyright 2003 XF All Rights Reserved.
*/
/**
*Now we are trying to store the TempHold in an Array.
*Use it in circle type.
*The array like a buffer, if we receive too many messages than we could deal with
*some will be lost.
*
*If some is lost in the transporting or the message is too too large
*Or any error ocuurs, the remaining message will be treated as used ones. and will be 
*replaced by new income TempHolds.
*
*We define the Array to be Waiting, and init its size to N, a static final private int value.
*/

import java.net.* ;
import java.io.* ;
import java.util.* ;
import mm.smy.bicq.* ;
import mm.smy.bicq.message.* ;

public final class Monitor implements Runnable{
	private int timeout = -1 ; // -1 indicates never!
	private int port = SendMessage.DEFAULT_RECEIVE_PORT ;
	private Monitorable mm = null ; // the object all packets will be sent to.
	private boolean running = true ; // if we should stop listening.
	
	private int maxsize = SendMessage.MAX_PACKAGE_SIZE ; //the max data.content size an UDP can carry. 

	//we the mainmanager starts this thread, we report Exception to it
	//but when the mainmanger's try==catch has passed.
	//while worked well, the net suddenly shut down....
	//we have to report the Exceptions to a certain method.	
	private boolean isInited = false ; 
	
	DatagramSocket in = null ; 
	
	private static final int N = 30 ;
	private DatagramPacket[] UDP_Queue = new DatagramPacket[N] ;
	
	//this one is for the inner class to share information. See explain on the top.
	private static Hashtable waiting = new Hashtable(3) ; //cache multiform pages message.
	
	
	public Monitor(Monitorable m_mm){
		mm = m_mm ;	
	}
	public Monitor(Monitorable m_mm, int m_port){
		mm = m_mm ;
		port = m_port ;
	}
	public void setPort(int m_port){
		port = m_port ;
	}
	public void setTimeOut(int seconds){
		timeout = seconds * 1000 ;
	}
	
	//only for make timeout unable.
	public void setTimeout(boolean m_timeout){
		if (!m_timeout)
			timeout = -1 ;
	}
	
	public void setIsInited(boolean m_is){
		isInited = m_is ;
	}
	public boolean isInited(){
		return isInited ;
	}
	
	public void setMaxSize(int m_size){
		maxsize = m_size ;	
	}
	
	public int getMaxSize(){
		return maxsize ;	
	}
	
	public void run(){
		if( in == null ){
			try{
				//open the listening port...
				start() ;
			}catch(Exception e){
				System.out.println("Starting Monitor failed..==>" + e.getMessage() ) ;
				System.exit(1) ;
			}
			System.out.println("Starting monitor successed") ;
		}
	}
	
	private void start() throws SocketException,IOException,SocketTimeoutException{
		//Open message process thread...
		FormMessage fm = new FormMessage() ;
		try{
			Thread t = new Thread(fm) ;
			t.start() ;
			//wait(5000) ;
		}catch(Exception e){
			System.out.println("Monitor Exception while open message process thread ==>" + e.getMessage() ) ;
		}
		System.out.println("Monitor starts working.................") ;
		byte[] b = new byte[maxsize] ;
		//maybe throw SocketException,SecurityException this means:
		//establish connection failed
		// We must tell the user.
		//This must be the isInited = false ;
		in = new DatagramSocket(port) ;
		System.out.println("$$$$$$$open Monitor port at:" + port) ;
		DatagramPacket packet = null ;
		//在waiting[] 中的当前位置，将由此处插入新的消息。
		int current_point = 0 ;
		
		while(running){// continue listen....
			try{
				if (timeout > 0 ){
					in.setSoTimeout(timeout) ;
				}
				packet = new DatagramPacket(b,maxsize) ;
				
			//	System.out.println("Trying listening.....") ;
				in.receive(packet);
				System.out.println("=========Monitor recieve a message") ;
				UDP_Queue[current_point] = packet ;
				System.out.println("Monitor put an UDP message to Queue:" + current_point ) ;
				current_point++ ;
				if (current_point >= N){
					current_point = 0 ;
				}
			}catch(SocketException e){
				//if timeout is set, we report a new Exception
				//else keep listen....					
				if (isInited){
					mm.sendMonitorException(e) ;
				}else{
					throw e ;
				}
			}catch(SocketTimeoutException e){
				if (isInited)
					mm.sendMonitorException(e) ;
				else{
					throw e ;
				}
			}catch(IOException e){
				if (isInited)
					mm.sendMonitorException(e) ;
				else{
					throw e ;
				}
			}
			//open a new thread to format the byte message.
			//if ( packet != null){
			//	FormMessage fm = new FormMessage(packet) ;
				//Thread t = new Thread(fm) ;
				//t.start() ;
			//	fm.run() ;
			//}
			//if not running, we go below.	
		}
		System.out.println("Monior close() is involved.") ;
		//close the connection
		try{
			in.close() ;
		}catch(Exception e){
			System.out.println("SocketException occurs while close listening DatagramSocket-->" + e.getMessage()) ;
		}
		
		isInited = false ;				
	}
	
	public void close(){ // close the listen
		running = false ;
	}
	
	
	class FormMessage implements Runnable{
		private DatagramPacket in_packet = null ;
		private byte[] in_temp = null ;
		private int in_from = -1 ;
		private int in_to   = -1 ;
		private int in_page = 0 ;
		private long in_hashcode = -1 ;
		private int in_type = MessageType.UNKNOWN_TYPE ;
		private byte[] in_content = null ;
		private InetAddress in_IP = null ;
		private int in_port = 5200 ;
		
		
		public FormMessage(){}
		
		public void run(){
			System.out.println("FormMessage starts working.....................:running:" + running) ;
			while(true){
				try{
					synchronized(this){
						wait(500) ;	
					}					
				}catch(Exception e){
					System.out.println("Exception in wait() in Monitor form message") ;	
					
				}
				
				
				
				for (int i = 0 ; i < N ; i++ ){
					if (UDP_Queue[i] != null){
						
						System.out.println("Monitor DUP_Queue:" + i + " is not null, proces...") ;
						in_packet = UDP_Queue[i] ;
						UDP_Queue[i] = null ;
						System.out.println("in_packet is null:" + (in_packet == null)) ;
						ReceivedMessage rm = form() ;
						if (rm != null){ 
							mm.sendReceivedMessage(rm);
						}//otherwise, the message has many pages. we will wait.
					//	System.out.println("message anyl OK") ;	
					}
					//System.out.println("UDP_Queue[" + i + "] is null...") ;
				}
			}
		}
		
		private synchronized ReceivedMessage form(){
			in_IP = in_packet.getAddress() ;
			in_port = in_packet.getPort() ;
			
			byte[] b = null ;
			
			ByteArrayInputStream bais = new ByteArrayInputStream(in_packet.getData()) ; 
			DataInputStream dis = new DataInputStream(bais) ; 
			try{
				in_from = dis.readInt() ; 
				in_to   = dis.readInt() ; 
				in_type = dis.readInt() ;
				int in_size = dis.readInt() ;
				if (in_size > 0 ){
					b   = new byte[in_size] ; //data.content size
				}
				in_page = dis.readInt() ;
				in_hashcode = dis.readLong() ;
				if(in_size > 0){
					dis.readFully(b) ;
					if(b.equals(SendMessage.NULL_BYTES)){
						in_content = null ;
					}else{
						in_content = b ;
					}
				}else{
					in_content = null ;
				}
			}catch(IOException e){
				//throw new IOException("Failed to convert message back") ;
				System.out.println("IOException:Failed to convert message.==>" + e.getMessage() ) ;
			}finally{
				try{
					bais.close() ;
					dis.close() ;
				}catch(Exception e){
					//ignore
				}
			}
		//OK, now we check wether it's a single page or multi one
			if( in_page <= 1){ // one page, return the rm ;		
				ReceivedMessage rm = new ReceivedMessage() ;				
				rm.setFrom(in_from) ;
				rm.setTo(in_to) ;
				rm.setType(in_type) ;
				rm.setHashcode(in_hashcode) ;
				rm.setContent(in_content) ;
				rm.setIP(in_IP) ;
				rm.setPort(in_port) ;
				
				return rm ;
			}
		//otherwise it has many pages. we store it in the wating hashtable, the hashcode indicates it's name.
			int totalpage = in_page%10000 ;
			int currentpage = (in_page - totalpage)/10000 ;
			//System.out.println("message hashcode:" +  in_hashcode ) ;
			TempHold th = null ;
			//System.out.println("*****************Monitor: process page:  " + currentpage + "||in_page is: " + in_page) ;
			
			if (waiting.containsKey(new Long(in_hashcode))){ // the recode is in.....
				th = (TempHold) waiting.remove(new Long(in_hashcode)) ;
			 	th.addData(currentpage,in_content) ;
			 	if(!th.isFull()){
			 		waiting.put(new Long(in_hashcode),th) ;
			 		//System.out.println("message waiting... in containsKey()") ;
			 		return null ;
			 	}
			 }else{ //not recorded. add new one.
			 	th = new TempHold(totalpage) ;
			 	//System.out.println("\n\nTempHold created. page is:" + currentpage) ;
			 	th.addData(currentpage,in_content) ;
			 	if(!th.isFull()){
			 		waiting.put(new Long(in_hashcode),th) ;
			 	//	System.out.println("message new ; waiting.....") ;
			 		return null ;
			 	}
			 }
			// not return until now, it means the th is full now, we send it out to the mainmanager.
			waiting.remove(new Long(in_hashcode)) ;
			
			ReceivedMessage rm2 = new ReceivedMessage() ;
			rm2.setIP(in_IP) ;
			rm2.setPort(in_port) ;
			rm2.setFrom(in_from) ;
			rm2.setTo(in_to) ;
			rm2.setType(in_type) ;
			rm2.setHashcode(in_hashcode) ;
			rm2.setContent(th.getTotalBytes()) ;	
			//System.out.println("Mulit Message received finished...") ;			
			return rm2 ;
		}
	}
	
}
		




