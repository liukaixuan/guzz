package mm.smy.bicq.user ;

/**
* 包含头像信息的一个小类。
* 
* @date 2004-4-19
* @author XF
* @copyright Copyright 2003-2004 XF All Rights Reserved.
*/

import javax.swing.Icon ;
import mm.smy.bicq.FaceManager ;
import mm.smy.bicq.user.User ;

import javax.swing.* ;
import javax.swing.event.* ;

import java.util.Vector ;
import java.util.Enumeration ;

public class PortraitListModel extends DefaultComboBoxModel implements ComboBoxModel{
	
	private Vector v = new Vector() ;
//	private Object selected ;
	
	public PortraitListModel(){
		init() ;
	}
	
	//我们知道默认的头像是1-1.bmp
	//我们的头像也是从1开始编号的。
	//我们知道目前有85个头像，当然更好的做法是自动的获取头像数据，只是不想麻烦了
	//现在凑着这用一下，等以后对FaceManager进行缓冲处理时再进行优化设计。
	private void init(){
		for(int i = 1 ; i < 86 ; i++){
			portraits[i - 1 ] = new PortraitObject(FaceManager.getFaceIcon(i, User.ONLINE ), i ) ;
			addElement(portraits[i - 1]) ;
		}
	}
	
	public int getSize(){
		return 85 ;
	}
	
	public Object getElementAt(int index){
		if(index < 0 ) index = 0 ;
		if(index > 84) index = 84 ;
		return portraits[index] ;
	}

/*	
	public void addListDataListener(ListDataListener listener){
//		ListDataEvent evt = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED,0,85) ;
//		listener.intervalAdded(evt) ;
		v.add(listener) ;
	}
	
	public void removeListDataListener(ListDataListener listener){
		v.remove(listener) ;
	}
	
	public void setSelectedItem(Object anItem){
		selected = anItem ;
	}
	
	public Object getSelectedItem(){
		return selected ;
	}
*/	
	public PortraitObject[] portraits = new PortraitObject[86] ; // 0 -> 85
	
	public static void main(String[] args){
		final JComboBox box = new JComboBox() ;
//		box.setSelectionMode(ListSelectionModel.SINGLE_SELECTION) ;
		box.setEditable(false) ;
		box.setModel(new PortraitListModel()) ;
		box.setRenderer(new PortraitCellRender()) ;
		box.getModel().addListDataListener(
			new ListDataListener(){
				public void contentsChanged(ListDataEvent e){
					System.out.println("list changed, is portraitobject:" + ((PortraitObject)box.getModel().getSelectedItem()).getPortrait()) ;
				}
				public void intervalAdded(ListDataEvent e){
					
				}
				
				public void intervalRemoved(ListDataEvent e){
					
				}
			}
		) ;
		
		box.setSelectedIndex(84) ;
		
		JFrame frame = new JFrame() ;
		frame.setSize(400,400) ;
		frame.getContentPane().add(box) ;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;
		frame.show() ;		
	}
	
}