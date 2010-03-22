package mm.smy.bicq.message ;

import java.io.*;
import java.util.* ;
//import java.net.InetAddress ;
import mm.smy.bicq.* ;

/**
*分段传送的消息的暂存，用于存放/合并各段消息，构成统一的一个消息正文整体
*
*
*
*
*
*/

public class TempHold implements Serializable{
	
	private boolean[] flag = null ;
	private byte[][] fill = null ;
	private String name = null ;
	
	private boolean isInited = false ;
	private int maxsize = 1 ;

/*	
	private InetAddress ip = null ;
	private int port = SendMessage.DEFAULT_RECEIVE_PORT ;
	
	public InetAddress getIP(){ return ip ; }
	public void setIP(InetAddress m_ip) { ip = m_ip ; }
	public int getPort() { return port ; }
	public void setPort(int m_port) { port = m_port ; }
*/	
	private TempHold(){}
	
	public TempHold(int maxsize){
		this.maxsize = maxsize ;
		init() ;
		System.out.println("TempHold init maxsize:" + maxsize ) ;
	}
	
	private void init(){
		flag = new boolean[maxsize] ;
		Arrays.fill(flag,false) ;
		fill = new byte[maxsize][] ;
		isInited = true ;
	}
	
	public boolean isFull(){
		if (!isInited){
			return false ;
		}
		for( int i = 0 ; i < flag.length ; i++ )
			if(!flag[i])
				return false ;
		return true ;
	}
	
	public void addData(int part, byte[] b){
		if (!isInited){
			init() ;
			System.out.println("addData init....") ;
		}
		try{
			fill[part -1 ] = b ;
			flag[part - 1] = true ;
		}catch(Exception e){
			e.printStackTrace() ;
		}
		
		System.out.println("TempHold state reports:") ;
		System.out.println("isFull():" + this.isFull() ) ;
		System.out.println("flag: page is:" + part) ;
		for(int i = 0 ; i < flag.length ; i++ ){
			System.out.println("flag" + i + ":" + flag[i]) ;
		}
	}
	
	public String getName(){
		return name ;
	}
	
	public void setName(String m_name){
		name = m_name ;
	}
	
	public byte[] getTotalBytes(){
		if(!isInited){
			return null ;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
		DataOutputStream dos = new DataOutputStream(baos) ;
		byte[] back = null ;
		try{
			for(int i = 0 ; i < fill.length ; i++ ){
				if(fill[i] != null){
					dos.write(fill[i]) ;
				}
			}
			back = baos.toByteArray() ;
		}catch(IOException e){
			e.printStackTrace() ;
		}
		try{
			baos.close() ;
			dos.close() ;
		}catch(Exception e){
			//Ignore
		}
				
		return back ;
	}
	
}

