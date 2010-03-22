package mm.smy.bicq ;

/**
* send all messages interfaced from Message
* message send default to port : 5201
*                        using : 5200
* @author XF
* @e-mail myreligion@163.com
* @date   2003-8-13   
* @copyright Copyright 2003 XF All Rights Reserved.
*/
import java.net.* ;
import java.io.* ;
import java.util.* ;
import mm.smy.bicq.message.* ;
import mm.smy.bicq.user.* ;

public class SendMessage extends Thread{
	//public datas
	public static final byte[] NULL_BYTES = "##null___".getBytes() ; //当传送的内容为null时，用该数据代替null
	public static final int MAX_PACKAGE_SIZE = 400 ; //UDP 最大可容的数据大小（不包括UDP头部分）
	public static final int DEFAULT_SENDOUT_PORT = 5200 ;
	public static final int DEFAULT_RECEIVE_PORT = 5201 ;
	
	private DatagramSocket sendSocket = null ;
	private InetAddress serverIP = null ;
	private int serverPort = SendMessage.DEFAULT_RECEIVE_PORT ;
	private int port = SendMessage.DEFAULT_SENDOUT_PORT ;

	private	int maxsize = this.MAX_PACKAGE_SIZE ; //获得UDP最大数据报大小
	
	//the default send.
	private Host host = null ;
	
	public void setHost(Host h){ //必须初始化
		host = h ;
	}
	
	//
	private User toUser = null ; //the user the message sends to
	private InetAddress toIP  = null ;
	private int toPort = SendMessage.DEFAULT_RECEIVE_PORT ;

//constructors
	public SendMessage(int m_port)throws IOException,SecurityException{
		port = m_port ;
		maxsize = this.MAX_PACKAGE_SIZE - 28 ; //去掉数据部分from,to,size,type,hashcode,page的大小，此时maxsize是content的大小
		
		this.start() ;
		sendSocket = new DatagramSocket(port) ;	
		
		System.out.println("$$$$$$$open SendMessage port at:" + port) ;
	}
	public SendMessage() throws IOException,SecurityException {
		//constructor
		maxsize = this.MAX_PACKAGE_SIZE - 28 ; //去掉数据部分from,to,size,type,hashcode,page的大小，此时maxsize是content的大小
		
		this.start() ;
		sendSocket = new DatagramSocket(port) ;
		
		System.out.println("$$$$$$$open SendMessage port at:" + port) ;
	}
//one UDP packet's max size.	
	public void setMaxSize(int m_size){
		maxsize = m_size - 28 ; //去掉数据部分from,to,size,type,hashcode,page的大小，此时maxsize是content的大小	
	}
	
	public int getMaxSize(){ return maxsize + 28 ; }
	
//set the server's information.
	public void setServer(InetAddress m_serverIP,int m_port){
		serverIP = m_serverIP ;
		serverPort = m_port ;
	}
	
//由于同时发送的消息冲突严重，我们建立一个缓冲区。
//该语法实现于2004-4-22
	private Vector messagecache = new Vector(20) ; //消息缓冲区

	public void run(){
		System.out.println("run() here") ;
		while(true){
			synchronized(this){
				try{
					while(messagecache.size() > 0){
						Message message = (Message) messagecache.remove(messagecache.size() - 1) ;
						if( message != null){
							sendMessage(message) ;
							System.out.println("((((((((((((((((((Send a Message." + message.getType()) ;
						}
					
					}
					wait(100) ;
				//	System.out.println("check.....") ;
				}catch(Exception e){
					System.out.println("Exception to send message.") ;
				}
			}
		}
	}
	
	public  void send(Message message) throws IOException{
		if(message != null){
			messagecache.add(message) ;
		}
	}
	
	
	private void sendMessage(Message message) throws IOException{
		int toNumber  ; // the message sends to.
		toUser = message.getTo() ; 
		if ( toUser == null ){ //no destination, send it to the server.
			toIP = serverIP ;
			toPort = serverPort ;
			toNumber = 1000 ; //the server.
		}else{
			toIP = toUser.getIP() ;
			toPort = toUser.getPort() ;
			toNumber = toUser.getNumber() ;
			if (toIP == null || toPort <= 0 || toPort > 65535){ // not online, or hidden/temp leave
				toIP = serverIP ;
				toPort = serverPort ;
				//toNumber = 1000 ; //这儿是错误的，因为服务器必须知道你是发给谁的；所以这儿应该用用户的号，而不是服务器的。
			}
		}
//		System.out.println("++++++++++++++sendmessage debug+++++++++++++++++++++++") ;
//		System.out.println("toIP:" + toIP) ;
//		System.out.println("toPort:" + toPort) ;
//		System.out.println("toNumber:" + toNumber) ;
		
		byte[] content = message.getByteContent() ; //获得消息正文
		if(content == null){
			content = this.NULL_BYTES ;
		}
		int length = content.length  ; //消息长度
		
		if (length <= maxsize ){ //OK, can send in one packet
			ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
			DataOutputStream dos = new DataOutputStream(baos) ;
			try{
				//如果我们要做消息转发的话，那么就应该看看from是谁，如果是null的话，用host
				//这儿的if判断的作用主要用于服务器上，client段可能暂时用不到，也有一定的模拟假消息的安全风险。
				
				if(message.getFrom() == null || message.getFrom().getNumber() <= 0){
					dos.writeInt(host.getNumber()) ; //from
				}else{
					dos.writeInt(message.getFrom().getNumber()) ;	
				}
				
				//修改2。我们已经把服务器发送的类分解出来了。所以这儿只考虑用户的。不许匿他人名发送。
				//dos.writeInt(host.getNumber()) ;
				
				dos.writeInt(toNumber) ;         //to
				dos.writeInt(message.getType()) ;//message type
//				System.out.println("SendOut indicates the message type is " + message.getType() ) ;
				dos.writeInt(content.length) ;   //content size
				dos.writeInt(-1) ;               //page
				dos.writeLong(new Date().getTime()) ; //hashcode
				dos.write(content,0,length) ;        //content
				content = baos.toByteArray() ;
			}catch(IOException e){
				throw new IOException("Failed to convert message to bytes") ;
			}finally{
				try{
					baos.close() ;
					dos.close() ;
				}catch(Exception e){
					//ignore
				}
			}
			DatagramPacket packet = new DatagramPacket(content,content.length,toIP,toPort) ; // the content has changed!!!!
			sendSocket.send(packet) ;
			
			System.out.println("SendMessage sends OK ") ;
			
		}else{
			//page:从第1页到最大页数
			int totalpage = 1 ;
			if (length%maxsize == 0 ){
				totalpage = length/maxsize ;
			}else{
				totalpage = (length - (length%maxsize))/maxsize + 1 ;
			}
			System.out.println("SendOut message too lager. divided to " + totalpage + " parts.") ;
			byte[] tempbyte = null ;
			int currentpage = 1 ;
			long hashcode = new Date().getTime() ;
			while(currentpage <= totalpage ){
				System.out.println("SendOut sends message part " + currentpage ) ;
				int leavenumber = length - (currentpage - 1)*maxsize ; //未发送的字节数目
			
				ByteArrayOutputStream baos2 = new ByteArrayOutputStream() ;
				DataOutputStream dos2 = new DataOutputStream(baos2) ;			
				try{
					dos2.writeInt(host.getNumber()) ; //from
					dos2.writeInt(toNumber) ;         //to
					dos2.writeInt(message.getType()) ;//message type
					dos2.writeInt(leavenumber>maxsize?maxsize:leavenumber) ;   //content size
					int temp = currentpage*10000 + totalpage ;
					dos2.writeInt(temp) ;               //page
					System.out.println("!!!!!!!!!!Send Out reports outpage in_packet:" + temp ) ;
					dos2.writeLong(hashcode) ; //hashcode
					dos2.write(content,(currentpage - 1)*maxsize, leavenumber>maxsize?maxsize:leavenumber) ; //content
					tempbyte = baos2.toByteArray() ;
				}catch(IOException e){
					throw new IOException("Failed to convert message to bytes") ;
				}finally{
					try{
						baos2.close() ;
						dos2.close() ;
					}catch(Exception e){
						//ignore
					}
				}			
				DatagramPacket packet2 = new DatagramPacket(tempbyte,tempbyte.length,toIP,toPort) ;
				sendSocket.send(packet2) ;
				currentpage++ ;
				try{
					wait(1000) ;
				}catch(Exception e){
					System.out.println("SendOut wait(long) throws==>" + e.getMessage() ) ;
				}	
			}
		}
	}
	
	public void close(){
		sendSocket.close() ;
	}
	
}
