package mm.smy.bicq.message ;

import java.io.* ;

import java.util.Vector ;
import java.util.Stack ;
import java.util.Enumeration ;

import mm.smy.bicq.user.* ;
import mm.smy.bicq.message.* ;

/**
*用户保存，读取身份验证消息。
*写入类，恢复时可以恢复User对象。
*
*写入到文件：number/permit.bicq
*
*
*/


public class PermitMessageFile{
	private File file = null ;
	private String number = "0" ;
	
	
	public PermitMessageFile(int m_number){
		number = new Integer(m_number).toString() ; //Host number.
	}
	
	private void init() throws IOException{
		if (file == null)
			file = new File(number + File.separator + "permit.bicq") ;
		if (!file.exists()){
			file.getParentFile().mkdirs() ;
			file.createNewFile() ;
		}
	}
	
	public void save(Stack s) throws IOException{
		//save it; 覆盖原来的
		if(s == null || s.size() == 0 ) return ;
		
		if(file == null) init() ;
		
		FileOutputStream fos = new FileOutputStream(file) ;
		ObjectOutputStream out = new ObjectOutputStream(fos) ;
		
		Enumeration e = s.elements() ;
		while(e.hasMoreElements()){
			PermitMessage pm = (PermitMessage) e.nextElement() ;	
			out.writeObject(pm) ;
		}

		out.close() ;
		file = null ;
		return ;		
	}



	/**
	* 返回所有的验证消息，发出的+收到的。
	*/
	public Stack read() throws IOException,ClassNotFoundException{
		if(file == null) init() ;
		
		if(file.length() == 0) return null ;
		
		Stack v = new Stack() ;
		PermitMessage pm = null ;
		
		FileInputStream fis = new FileInputStream(file) ;
		ObjectInputStream in = new ObjectInputStream(fis) ;
		
		while(fis.available() > 0){
			//System.out.println("available is:" + fis.available() ) ;
			pm = new PermitMessage() ; 
			pm = (PermitMessage) in.readObject() ; //该处没有进行安全的处理，用户可以读取他人的消息。
			v.push(pm) ;
		}
		in.close() ;
		file = null ;
		return v ;
	}
	
	
	public void close(){
		if(file != null){
			file = null ; 	
		}
	}
	
/*	
	public static void main(String args[]) throws IOException,ClassNotFoundException{

		PermitMessageFile file = new PermitMessageFile(50282717) ;

//		file.save(s) ;
		System.out.println("Write completed to file") ;
		
		Stack s2 = file.read() ;
		System.out.println("read completed from file") ;
		
		if(s2 == null) s2 = new Stack() ;
		
		for(int i = 0 ; i < 10 ; i++){
			PermitMessage pm = new PermitMessage() ;
			pm.setContent("permit message " + i) ;			
			s2.push(pm) ;
			//file.append(pm) ;
			System.out.println("append " + i) ;
		}
				
	//	while(!s2.empty()){
	//		PermitMessage message = (PermitMessage) s2.pop() ;
	//		System.out.println(message.getContent()) ;			
	//	}
		
		file.save(s2) ;	
		
		file.close() ;
		
	}
*/	
	
}
