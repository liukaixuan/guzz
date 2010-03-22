package mm.smy.bicq.user.manager ;

import java.awt.* ;
import java.awt.event.* ;

import javax.swing.* ;

import java.util.Vector ;
import java.util.Hashtable ;
import java.util.Enumeration ;

import mm.smy.bicq.user.GuestGroup ;
import mm.smy.bicq.user.Guest ;

import mm.smy.util.* ;

/**
* 实现对GuestGroup的管理.
* 
* @author XF
* @author e-mail:myreligion@163.com
* @date 2003-11-29
* @copyright Copyright 2003 XF All Rights Reserved
*/

public class GuestGroupManager extends JFrame implements ActionListener,MouseListener{
	
	private JList list = null ;
	private JScrollPane pane = null ;
	
	private JButton select = new JButton("选定") ;
	private JButton add    = new JButton("添加新组") ;
	private JButton delete = new JButton("删除") ;
	
	
	private	Vector data = new Vector() ;	
	
	private Hashtable guestgroups = null ;
	
	private boolean isSelected = false ;  //当要返回一个用户组时，用来标志用户是否选择了用户。
	private GuestGroup gg_return = null ; //当要返回一个用户组时，该类用来表示选择的用户组。
	
	private GuestGroupManager outer = this ;

	
	public static void main(String[] args){
		GuestGroup gg = new GuestGroup("我的好友") ;
		gg.setIsSystemic(true) ;
		GuestGroup gg2 = new GuestGroup("gg2") ;
		GuestGroup gg3 = new GuestGroup("gg3") ;
		Hashtable h = new Hashtable() ;
		h.put(gg.getGroupname(), gg) ;
		h.put(gg2.getGroupname(), gg2) ;
		h.put(gg3.getGroupname(), gg3) ;
		
		GuestGroupManager t = new GuestGroupManager(h) ;
		t.show() ;
		System.out.println("chose:" + t.getChoseGuestGroup().getGroupname()) ;
		System.out.println("h:     " + h) ;
	}


	public GuestGroupManager(Hashtable m_guestgroups){
		this.guestgroups = m_guestgroups ;
		
		this.setSize(300,400) ;
		this.setLocation(200,200) ;
		this.setTitle("用户组管理") ;
		
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					gg_return = null ;	
					isSelected = true ;
					outer.dispose() ;
				}
			}
		) ;
		
		init() ;
	}
	
	private void init(){
		
		Enumeration e = guestgroups.keys() ;
		while(e.hasMoreElements()){
			data.add(e.nextElement()) ;
		}
		
		//Vector
		list = new JList(data) ;
		pane = new JScrollPane() ;
		pane.getViewport().setView(list) ;
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION) ;
		list.addMouseListener(this) ;
		
		select.setActionCommand("select") ;
		select.addActionListener(this) ;
		
		add.setActionCommand("add") ;
		add.addActionListener(this) ;
		
		delete.setActionCommand("delete") ;
		delete.addActionListener(this) ;
		
		Container cp = this.getContentPane() ;
		cp.add(pane,BorderLayout.CENTER) ;
		
		JPanel buttons = new JPanel() ;
		buttons.add(select) ;
		buttons.add(add) ;
		buttons.add(delete) ;
		cp.add(buttons, BorderLayout.SOUTH) ;
			
	}
	
	/**
	* 该方法为阻止方法，直到用户作出了选择或是关闭了窗口为止。
	* 如果用户关闭了窗口，将会返回 "我的好友" 组。
	* 用户可能在调用这个方法以后修改了guestgroups的信息。
	* 可是该类没有提供刷新mf的方法。所以在调用该方法后，应该刷新mf。
	*/
	public synchronized GuestGroup getChoseGuestGroup(){
		
		while(!isSelected){		
					try{
						wait(50) ;	
					//	System.out.println("ggm waiting......") ;
					}catch(Exception error){
						System.out.println("error:" + error.getMessage() ) ;
					}
			
		}

		//如果用户不选择的话，我们返回 我的好友 
		if(gg_return == null){
			return (GuestGroup) guestgroups.get("我的好友") ;
		}
		
		
			
		return gg_return ;
		/*
		this.dispose() ;
		return (GuestGroup) guestgroups.get("我的好友") ;
		*/
	}
	
	public void mouseClicked(MouseEvent e){
		if(e.getClickCount() >= 2){
			gg_return = (GuestGroup) guestgroups.get(list.getSelectedValue()) ;
			isSelected = true ;
			this.dispose() ;
			return ;
		}
	}
	
	public void mouseReleased(MouseEvent e){
		
	}
	
	public void mousePressed(MouseEvent e){
		
	}
	
	public void mouseEntered(MouseEvent e){
		
	}
	
	public void mouseExited(MouseEvent e){
		
	}
	
	
	public void actionPerformed(ActionEvent e){
		
		String command = e.getActionCommand() ;
		if(command.equals("select")){
			if(list.isSelectionEmpty()) return ;
			
			gg_return = (GuestGroup) guestgroups.get(list.getSelectedValue()) ;
			isSelected = true ;
			this.dispose() ;
			return ;
		}else if(command.equals("add")){
			String newname = JOptionPane.showInputDialog(this,"请输入新组名：", "建立新组", JOptionPane.PLAIN_MESSAGE) ;
			if(newname == null) return ;
			newname = newname.trim() ;
			if(newname.length() == 0 ) return ;
			
			if(!guestgroups.containsKey(newname)){
				GuestGroup gg = new GuestGroup(newname) ;
				gg.setIsSystemic(false) ;
				guestgroups.put(newname, gg) ;
				data.add(newname) ;	
			}else{
				return ;
			}			
			
		}else if(command.equals("delete")){
			if(list.isSelectionEmpty()) return ;
			
			Object name = list.getSelectedValue() ;
			GuestGroup gg2 = (GuestGroup) guestgroups.get(name) ;
			if(gg2 == null) return ;
			
			//系统组，不能删除。
			if(gg2.isSystemic()){
				JOptionPane.showMessageDialog(this, gg2.getGroupname() + " 是系统组，不能删除", "删除组错误", JOptionPane.ERROR_MESSAGE) ;
				return ;
			}
			//在删除之前我们把 该组中的用户移到 "我的好友" 中。
			Vector friends = gg2.getAllGuests() ;
			if(friends != null){
				Enumeration fe = friends.elements() ;
				GuestGroup myfriend = (GuestGroup) guestgroups.get("我的好友") ;
				
				while(fe.hasMoreElements()){
					((Guest) fe.nextElement()).joinGuestGroup(myfriend) ;	
				}
			}
			
			guestgroups.remove(name) ;
			data.remove(name) ;
			//System.out.println("guestgroups:" + guestgroups) ;
		}
		
		list.setListData(data) ;
		list.repaint() ;
		pane.repaint() ;
		pane.invalidate() ;
	}
	
	
}