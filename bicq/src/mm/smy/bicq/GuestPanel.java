package mm.smy.bicq ;

/**
* 用户定义Guest头像在mf中的按钮。
* 该类直接继承JButton，加入了自己的绘制方法，绘出自己的按钮。
* 
* 注意到状态改变，大小头像等等，所以绘制时将传入User对象；以及是大头像还是小头像！
* 
* 头像库保存在/face目录下面。
* number-1.bmp :在线头像
* number-2.bmp :不在线状态
* number-3.bmp :离开状态
* 
* 
* 
* 我们已经在User里面做了处理，加入了新的变量保存未读的消息。
* 因为照顾到 把自己加为好友或是调试时使程序崩溃，因而没有用Guest对象保存该资料！
* 以后为了防止自己把自己把为好友可以强制性考虑。
*/

import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;
import java.io.* ;

import mm.smy.bicq.user.User ;

public class GuestPanel extends JPanel{
	
	private User u = null ;
	boolean isSmallPortrait = false ;
	private JButton b = null ;
	private int number = -1 ;

	//public GuestPanel(){}
	
	public GuestPanel(User u, boolean isSmallPortrait){
		this.u = u ;
		this.isSmallPortrait = isSmallPortrait ;
		fresh() ;
	}
	
	
    public ImageIcon createImageIcon(int portrait) {
		return FaceManager.getFaceIcon(portrait, u.getState()) ;
    }
    
    //we can paint out panel specially here.
    
    public void paintComponent(Graphics g){
    	super.paintComponents(g) ;
    	//g.drawLine(b.getX(),b.getY(),b.getX() + 40,b.getY() + 40) ;
	   //	System.out.println("paintComponent has been invoked....") ;   	
    }
	
	/**
	* 绘制该面板。
	*
	*
	*
	*/
	private void fresh(){
		if(u == null) return ;
		
		number = u.getNumber() ;
		
		this.setSize(150,60) ;
		
		//b = new JButton(this.getNickname(),this.getImage()) ;
		b = new JButton(this.getNickname(),this.createImageIcon(u.getPortrait())) ;
		
		if(u.getUnreadMessages() > 0 ) b.setText(this.getNickname() + "[有消息]") ;
		
		b.setActionCommand(u.getNumber() + "") ;
		b.setSize(150,50) ;
		
//		JLabel label = new JLabel(this.getImage()) ;
//		label.setLabelFor(b) ;
		
//		this.add(label) ;
		this.add(this.getNickname(),b) ;
	}
	
	public void addMouseListener(MouseListener listener){
		b.addMouseListener(listener) ;
	}
	
	public void addActionListener(ActionListener listener){
		b.addActionListener(listener) ;	
	}
	
	public String getNickname(){
		if(u == null) return "" ;
		
		if(u.getNickname() == null || u.getNickname().length() == 0 )
			return u.getNumber() + "" ;
		return u.getNickname() ;		
	}
	
	private Icon getImage(){
		if( u == null) return null ;
		
//		String nickname = u.getNickname() ;
//		if(nickname == null || nickname.length() == 0 )
//			nickname = u.getNumber() + "" ;
		
		ImageIcon icon = null ;
		icon = new ImageIcon("face" + File.separator + u.getPortrait() + "-1.jpg" ) ;
		
		if(icon == null){
			System.out.println("no such image") ;
			icon = new ImageIcon("face" + File.separator + "1-1.jpg")	;
		}
		
		return icon ;
//		b = new JButton(nickname,icon) ;
//		if(isSmallPortrait){
		//	b.setSize(50,40) ;
			//icon.paintIcon(b,b.getGraphics(),0,0) ;
//			b.setHorizontalTextPosition(AbstractButton.RIGHT) ;
			
//			b.setActionCommand((number > 0 ?number:u.getNumber()) + "") ;
			
//		}else{
		//	b.setSize(100,100) ;
//			b.setVerticalTextPosition(AbstractButton.BOTTOM) ;
			
//			b.setActionCommand((number>0?number:u.getNumber()) + "") ;	
//		}
		
//		this.add(b) ;
		
	}
	
	//返回好友的BICQ号。
	public int getNumber(){
		return number ;
	}
	
	public String getText(){
		if(u==null) return "N/A" ;	
		
		if(u.getNickname() == null || u.getNickname().length() == 0 )
			return u.getNumber() + "" ;
		return u.getNickname() ;
	}
	
	protected void setNumber(int m_number){
		number = m_number ;		
	}
	
}
