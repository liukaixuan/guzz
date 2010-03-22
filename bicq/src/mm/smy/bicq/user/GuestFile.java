package mm.smy.bicq.user ;

/**
* deal with the guest.bicq ; store/read guests information on the 
* local disk.
* @author XF
* @e-mail myreligion@163.com
* @date   2003-8-14   
* @copyright Copyright 2003 XF All Rights Reserved.
*/
import java.io.* ;
import java.util.Enumeration;
import java.util.Vector;
import mm.smy.bicq.* ;

public class GuestFile{
	private File guestfile = null ;
	private String number = "" ;
	public GuestFile(String m_number){
		number = new Integer(m_number).toString() ; //Host number.
	}
	
	private void init() throws IOException{
		if (guestfile == null)
			guestfile = new File(number + File.separator + "guest.bicq") ;
		if (!guestfile.exists()){
			guestfile.createNewFile() ;
		}		
	}
	
	public boolean isDataExsits(){
		guestfile = new File(number + File.separator + "guest.bicq") ; 
		return (guestfile.exists() && guestfile.length() != 0) ;
	}
	
	/**
	*保存好友资料，覆盖原有文件。
	*/
	public void save(Guest[] g) throws FileNotFoundException,IOException{
		init();
		ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream(guestfile)) ;
		for ( int i = 0 ; i < g.length ; i++ ){
			out.writeObject(g[i]) ;
		}
		out.close();							 
		
	}
	public void save(GuestGroup[] gg) throws FileNotFoundException,IOException{
		init();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(guestfile)) ;
		Guest[] g = null ;
		for ( int i = 0 ; i < gg.length ; i++ ){
			
			Enumeration e = gg[i].getAllGuests().elements() ;
			while(e.hasMoreElements()){
				Guest temp_guest = (Guest) e.nextElement() ;
				
				out.writeObject(temp_guest) ;
			}
		}
		out.close();
	}
	/**
	*在原好友文件后面添加新的好
	*/
	public void append(Guest[] g) throws FileNotFoundException,IOException{
		init();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(guestfile,true)) ;
		for ( int i = 0 ; i < g.length ; i++)
			out.writeObject(g[i]) ;
		out.close();		
	}
	
	public void append(Guest g) throws FileNotFoundException,IOException{
		init();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(guestfile,true)) ;
		out.writeObject(g) ;
		out.close();		
		
	}
	//读取好友资料
	public Guest read(Guest g) throws FileNotFoundException,NoSuchUserException,IOException,ClassNotFoundException{
		init();
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(guestfile)) ;	
		while(in.available() > 0 ){
			Guest mg = (Guest) in.readObject();
			if (mg.equals(g)){
				in.close() ;
				return mg ;
			}
		}
		in.close();
		throw new NoSuchUserException(g.getNumber() + " Cannot found!") ;
	}
	public Guest[] readAll() throws FileNotFoundException,IOException,ClassNotFoundException{
		init();
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(guestfile)) ;
		Vector v = new Vector() ;
		while(in.available() > 0 ){
			v.add(in.readObject()) ;
		}
		in.close();
		v.trimToSize();
		return (Guest[]) v.toArray() ;
	}
	
	
} 


