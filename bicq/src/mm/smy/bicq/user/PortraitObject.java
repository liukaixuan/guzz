package mm.smy.bicq.user ;

/**
* 包含头像信息的一个小类。
* 
* @date 2004-4-19
* @author XF
* @copyright Copyright 2003-2004 XF All Rights Reserved.
*/

import javax.swing.Icon ;

public class PortraitObject{
	
	private Icon icon ;
	private int portrait ;
	
	public PortraitObject(Icon icon, int portrait){
		this.icon = icon ;
		this.portrait = portrait ;
	}
	
	public Icon getIcon(){
		return icon ;
	}
	
	public int getPortrait(){
		return portrait ;
	}	
}