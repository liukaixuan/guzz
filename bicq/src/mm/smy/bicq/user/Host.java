package mm.smy.bicq.user ;

/**
* the current BICQ user's information.
* @author XF
* @e-mail myreligion@163.com
* @date   2003-8-13   
* @copyright Copyright 2003 XF All Rights Reserved.
*/
import java.io.* ;
import mm.smy.bicq.message.* ;
import java.net.InetAddress ;

import java.util.Vector ;

public class Host extends User implements Serializable{
	//身份验证类型
	public static final int ALLOW_ANYONE = 401 ; //允许任何人把我设为好友
	public static final int MY_PERMIT    = 402 ; //需要我的通过验证
	public static final int NO_DISTURB   = 403 ; //不许任何人把我加为好友
	
	private int auth = this.ALLOW_ANYONE ; //本人的身份验证情况。
	
	public int getAuth(){ return auth ; }
	public void setAuth(int m_auth){ auth = m_auth ; }
		
	private String smtpserver = "" ;
	private int smtpport = 25 ;
	private String smtpnickname = "" ;
	private transient String smtppassword = "" ;//秘文，可保存在硬盘上
	private transient String password = "" ; //host's pasword.MD5
	
	//constructor
	public Host(){super();}
	public Host(int m_number){super(m_number) ;}
	public Host(int m_number,String m_nickname){
		super(m_number,m_nickname) ;
	}
	
	public void setSmtpServer(String m_servername){smtpserver = m_servername ;}
	public void setSmtpPort(int m_port){ smtpport = m_port ;}
	public void setSmtpNickname(String m_nickname){smtpnickname = m_nickname ;}
	public void setSmtpPassword(String m_password){smtppassword = m_password ;}
	
	public String getSmtpServer(){ return smtpserver ; }
	public int getSmtpPort(){ return smtpport ;}
	public String getSmtpNickname(){ return smtpnickname ; }
	public String getSmtpPassword(){ return smtppassword ; }
	//the user's password,
	public void setPassword(String m_password){ password = m_password ; } 
	public String getPassword(){return password ;}
	
	//用户所编辑的自定义的留言内容。我们用Vector搞定。默认是10条。下面是一些处理方法。
	Vector mywords = new Vector(10) ;
	
	public Vector getAllMyWords(){ return mywords ; }
	public void appendMyWord(String s){
		if(s == null) return ;
		mywords.add(s) ;		
	}
	public void clearMyWords(){
		mywords.clear() ;	
	}
	
	public void setMyWords(Vector v){
		if(v == mywords) return ; //地址相等，同一个Vector，返回。
		if(v == null){
			mywords = null ;
			return ;	
		}
		
		mywords.clear() ;
		mywords.addAll(v) ;
		return ;
	}
	
	
	//override
	public Host copyFrom(Host h){
		super.copyFrom(h) ;
		auth = h.getAuth() ;
		smtpserver = h.getSmtpServer() ;
		smtpport = h.getSmtpPort() ;
		smtpnickname = h.getSmtpNickname() ;
		smtppassword = h.getSmtpPassword() ;
		password = h.getPassword() ;
		mywords = h.getAllMyWords() ; //复制地址
		return this;	
	}
	
	public Host copyInfor(Host h){
		super.copyInfor(h) ;
		auth = h.getAuth() ;
		return this ;	
	}
	
	//serializable
	public User toObject(byte[] b){		
		if (b == null || b.length == 0)
			return null ;
		ByteArrayInputStream bin = new ByteArrayInputStream(b) ;
		DataInputStream dis = new DataInputStream(bin) ;
		try{
			int temp_length = dis.readInt() ;
			byte[] b_up = new byte[temp_length] ;
			dis.read(b_up) ;
			super.toObject(b_up) ;
			auth = dis.readInt() ;			
		}catch(Exception e){
			System.out.println("mm.smy.bicq.user.Host:toObject() has thrown an Exception==>" + e.getMessage()) ;
		}finally{
			if (bin != null){
				try{
					bin.close() ;
				}catch(Exception e){}
			}
			if (dis != null){
				try{
					dis.close() ;
				}catch(Exception e){}
			}
		}
		
		return this ;
	}
	
	/*
	* 第一个位置我们写入父类的byte[].length，然后写入父类的byte[]
	* 然后在写入本类的东西
	*
	*
	*/
	public byte[] toBytes(){
		byte[] b = super.toBytes() ;
		
		ByteArrayOutputStream bo = new ByteArrayOutputStream() ;
		DataOutputStream dos = new DataOutputStream(bo) ;
		
		byte[] back = null;
		try{
			//我们不考虑b == null的情况，以后要考虑细心点儿。
			dos.writeInt(b.length) ;
			dos.write(b) ;
			dos.writeInt(auth) ;
			
			back = bo.toByteArray() ;
			
		}catch(Exception e){
			System.out.println("mm.smy.bicq.user.Host:toBytes() has thrown an exception:" + e.getMessage()) ;
		}finally{
			if (bo!= null){
				try{
					bo.close() ;
				}catch(Exception e){}
			}
			
			if (dos != null){
				try{
					dos.close() ;
				}catch(Exception e){}
			}
		}
	
		return back ;				
		
	}
	
	public String toString(){
		return super.toString() + "[host part is: auth = " + auth + "]";		
	}
		
}
