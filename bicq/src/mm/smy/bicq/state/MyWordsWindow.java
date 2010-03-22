package mm.smy.bicq.state ;

/**
* 用户修改自己的 留言 的地方。
* TempLeaveWord.
* 我们把修改后的Vector发送给StateChangedManager.
* 注意，这个Vector是新建的，这主要是为了方便。
* 因为如果用户修改了tempLeaveWord，而不想保存，我们就不用保存了
* 所以，StateChangedManager就麻烦了一点。
*
*
*
*
*
*/

import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;

import java.util.Vector ;
import mm.smy.bicq.user.Host ;

public class MyWordsWindow extends JFrame implements ActionListener,ItemListener{
	private StateChangedManager scm = null ;
//test part:	
	public MyWordsWindow(Host h){
		mywords = h.getAllMyWords() ;
		init() ;
	}
//*/
	public MyWordsWindow(StateChangedManager m_scm, Host h){
		mywords = h.getAllMyWords() ;
		System.out.println("host:" + h) ;
		System.out.println("mywords:" + mywords ) ;
		scm = m_scm ;
		init() ;		
	}
	
	private JComboBox counts = null ; //留言编号
	private JTextArea word = new JTextArea(8,8) ;
	private JScrollPane pane = new JScrollPane(word) ;
	
	private JButton add = new JButton("添加") ;
	private JButton delete = new JButton("删除") ;
	private JButton ok = new JButton("确定") ;
	private JButton cancel = new JButton("取消") ;
	
	private Vector mywords = null ;
	private Vector tempwords = null ; //临时存放mywords的地方，如果用户要求"确定" 的话，就把它set给scm.
	
	private int current_number = 0 ; //当前counts选择的留言编号。start at the index of zero
	
	private void init(){
		add.setActionCommand("add") ;
		add.addActionListener(this) ;
		
		delete.setActionCommand("delete") ;
		delete.addActionListener(this) ;
		
		ok.setActionCommand("ok") ;
		ok.addActionListener(this) ;
		
		cancel.setActionCommand("cancel") ;
		cancel.addActionListener(this) ;
		
		//初始化counts.		
		counts = new JComboBox() ;
		counts.addItemListener(this) ;
		counts.setEditable(false) ;
		
		
		//初始化tempwords
		if(mywords == null || mywords.size() == 0 ) tempwords = new Vector() ;
		else{
			tempwords = new Vector(mywords) ;
			System.out.println("tempwords:" + tempwords ) ;
			if(tempwords.elementAt(0) != null){
				word.setText((String) tempwords.elementAt(0) ) ;
				System.out.println("word at init() is inited to:" + word.getText() ) ;
			}
		}
		
		//初始化显示窗口
		this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE) ;		
		this.setTitle("回复设置") ;
		this.setSize(400,400) ;
		
		initWindow() ;
		refresh() ;		
	}
	
	private void initWindow(){
		JPanel top = new JPanel() ; //
		JPanel center = new JPanel() ;
		JPanel bottom = new JPanel() ;
		
		top.add(new JLabel("留言设置：")) ;
		top.add(counts) ;
		
		JPanel center_right = new JPanel() ;
			center_right.setLayout(new GridLayout(2,1)) ;
			center_right.add(add) ;
			center_right.add(delete) ;
		center.setLayout(new BorderLayout()) ;
			center.add(word,BorderLayout.CENTER) ;
			center.add(center_right,BorderLayout.EAST) ;
		
		bottom.add(ok) ;
		bottom.add(cancel) ;
		
		Container cp = this.getContentPane() ;
		cp.add(top,BorderLayout.NORTH) ;
		cp.add(center, BorderLayout.CENTER) ;
		cp.add(bottom, BorderLayout.SOUTH) ;
		
	}
	
	private void refresh(){
		counts.removeAllItems() ;
		for( int i = 0 ; i < tempwords.size() ; i++){
			counts.addItem(new Integer( i + 1 )) ;	
		}
		//if(current_number > tempwords.size())
		if(current_number > 0 )
			counts.setSelectedIndex(current_number) ;
		//word.setText((String) tempwords.get(current_number) ) ;
		this.invalidate() ;
		return ;
	}
	
	private boolean isButtonPressed = false ; //如果是点击的按钮的话，那么在itemStateChanged(.. e)中就不能保存当前的word到current_number了。
	
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand().trim().toLowerCase() ;
		if(command.equals("add")){ //添加
			if(tempwords.size() == 0 ){
				tempwords.add(" ") ;	
			}else{
				tempwords.setElementAt(word.getText(),current_number) ; //保存现在的word内容。
				tempwords.add(" ") ;
			}
			current_number = tempwords.size() - 1 ; //将目前的指针指向最有/新建的项。
			
			System.out.println("add: current_number is:" + current_number) ;
			isButtonPressed = true ;
			refresh() ;
		}else if(command.equals("delete")){ //删除留言，同时修改，移动counts,tempwords中的内容，让他们按顺序没有空格的放着。
			if(tempwords.size() == 0 ) return ;
			
			tempwords.remove(current_number) ;
			current_number = 0 ;
			isButtonPressed = true ;
			refresh() ;
		}else if(command.equals("ok")){
			//把当前的word内容写入。
			if(tempwords.size() != 0){
				tempwords.setElementAt(word.getText(),current_number) ;
			}			
			System.out.println("tempwords:" + tempwords) ;
			scm.setMyWords(tempwords) ;
			this.dispose() ;
			return ;
		}else if(command.equals("cancel")){
			this.dispose() ;
			return ;	
		}
		
	}
	
	public void itemStateChanged(ItemEvent e){
		//没有留言。
		//如果不加入这一句的话，那么当counts为空时点击counts选择的空选项，e.getItem().toString还是1。这就会造成错误。
		if(tempwords.size() == 0 ) return ; 
		
		if(!isButtonPressed){
			String s = e.getItem().toString() ;
			if( s == null || s.trim().length() == 0 ) return ;
			System.out.println("\n\ne.getItem().toString():" + s) ;
			
			System.out.println("itemChanged:") ;
			System.out.println("current_number:" + current_number) ;
			System.out.println("word.getText():" + word.getText() ) ;
			
			tempwords.setElementAt(word.getText(), current_number) ; //保存现在的word内容。
			
			try{
				current_number = (new Integer(s).intValue()) - 1 ;
			}catch(Exception er){
				System.out.println("error:" + er) ;
			}
		}else{
			isButtonPressed = false ;
		}		
		
		word.setText((String) tempwords.get(current_number)) ;
		
		
				//word.setText( (String) tempwords.get(current_number)) ;
		System.out.println("+++++++++++++++++++ ItemEvent +++++++++++++++++++") ;
		System.out.println("e.getStateChange():" + e.getStateChange()) ;
		System.out.println("e.getItem():" + e.getItem()) ;
		//System.out.println("e.getItemSelectable():" + e.getItemSelectable()) ;
	}
	
	public static void main(String[] args){
		Host h = new Host(3000) ;
		Vector words = new Vector() ;
		words.add("Hello Word 1") ;
		words.add("2 item") ;
		h.setMyWords(null) ;
		
		MyWordsWindow window = new MyWordsWindow(h) ;
		window.show() ;	
		
	}
	
	
	
}
