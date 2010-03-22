package mm.smy.bicq.user ;

import java.io.* ;
import java.util.* ;
/**
* Also See GuestFile
* 该内容需要重新考虑
* GuestFile/GuestGroupFile只能保留一个，多了容易引起。
* 考虑到一个组里可能有0各用户，所以考虑 GuestGroup还是比较合理的
*
* 我们的做法是：只用GuestGroupFile, ObjectWriter/Reader; Guest对象从中恢复。
* 当添加新组时，append
* 当移动成员，添加成员时；重写整个文件。理论上不是最完美的，不过用户应该感觉不到速度上的差异
* 文件名为：number/guestgroup.bicq
*/

public class GuestGroupFile{
	private int number = -1 ;
	private String m_place = null ;
	File file = null ;
	public GuestGroupFile(int m_number){
		number = m_number ;
		m_place = m_number + File.separator + "guestgroup.bicq" ;
	}
	
	private void init() throws IOException{
		file = new File(m_place) ;
		if(!file.exists()){
			file.getParentFile().mkdirs() ;
		}
		file.createNewFile() ;
	}
	
	public Hashtable getAll() throws ClassNotFoundException,FileNotFoundException,IOException {
		if(file == null) init() ;
		
		if(file.length() == 0) return null ; //如果是空文件，返回null
		
		Hashtable h = new Hashtable(5) ;
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file)) ;
		while(true){
			GuestGroup gg = new GuestGroup() ;
			try{
				gg = (GuestGroup) in.readObject() ;
			}catch(FileNotFoundException e){
				throw e ;
			}catch(EOFException e){
				break; 	
			}catch(IOException e){
				e.printStackTrace() ;
				throw e ;
			}catch(ClassNotFoundException e){
				throw e ;
			}catch(Exception e){
				System.out.println("reaching end.") ;
				break ;	
			}
			
			//我们要保证从硬盘获得所有的人资料都是离线的
			Enumeration e = gg.getAllGuests().elements() ;
			while(e.hasMoreElements()){
				User _user = (User) e.nextElement() ;
				_user.setState(User.OFFLINE) ;
			}
			
			h.put(gg.getGroupname(),gg) ;			
		}
		in.close() ;
		
//		System.out.println("In the ggf.class after invoking getAll().") ;
//		GuestGroup gg = (GuestGroup) h.get("我的好友") ;
//		System.out.println("gg:" + gg) ;
		
//		System.out.println("g:" + (Guest)gg.getAllGuests().elementAt(0)) ;
		
		
		return h ;
	}
	
	public void append(GuestGroup gg) throws FileNotFoundException,IOException{
		if (gg == null) return ;
		if (file == null){
			init() ;
		}
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file,true)) ;
		oos.writeObject(gg) ;
		oos.close() ;
	}
	
	public void save(Hashtable hgg) throws FileNotFoundException,IOException{
		if (hgg == null) return ;
		if (file == null) init() ;
		
		System.out.println("delete file:" + file.delete()) ;
		
		file.createNewFile() ;
		
	//	System.out.println("----------------------------ggf,Saving to file:" + hgg) ;
	//	System.out.println("g:" + (Guest)((GuestGroup)hgg.get("我的好友")).getAllGuests().elementAt(0)) ;
	//	System.out.println("--------------------------------------------------------------") ;
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file)) ;
		Enumeration e = hgg.elements() ;
		while(e.hasMoreElements()){
			out.writeObject((GuestGroup) e.nextElement()) ;
		}
		out.close() ;
	}
	
	public void close(){
		return ;
	}
	
}
