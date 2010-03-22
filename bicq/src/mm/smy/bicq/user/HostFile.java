package mm.smy.bicq.user ;

/**
* deal with the host.bicq ; store/read host information on the 
* local disk.
* @author XF
* @e-mail myreligion@163.com
* @date   2003-8-14   
* @copyright Copyright 2003 XF All Rights Reserved.
*/
import java.io.* ;
import java.util.Vector;
import mm.smy.bicq.* ;

public class HostFile{
	private File hostfile = null ;
	private String number = "" ;
	public HostFile(int m_number){
		number = new Integer(m_number).toString() ; //Host number.
	}
	
	private void init() throws IOException{
		if (hostfile == null)
			hostfile = new File(number + File.separator + "host.bicq") ;
		if(!hostfile.getParentFile().exists())
			hostfile.getParentFile().mkdirs() ;
		if (!hostfile.exists()){
			hostfile.createNewFile() ;
		}		
	}
	
	public boolean isDataExsits(){
		hostfile = new File(number + File.separator + "host.bicq") ;
		return (hostfile.exists() && hostfile.length() != 0 ) ;
	}
	
	/**
	*保存资料，覆盖原有文件。
	*/
	public void save(Host h) throws FileNotFoundException,IOException{
		init();	
		ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream(hostfile)) ;
		out.writeObject(h) ;
		out.close();
	}

	//读取资料
	public Host read() throws FileNotFoundException,IOException,ClassNotFoundException{
		init();
		Host mg = null ;
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(hostfile)) ;	
		if(hostfile != null && hostfile.length() != 0 )
			mg = (Host) in.readObject();
		in.close();
		return mg ;
	}	
	
} 


