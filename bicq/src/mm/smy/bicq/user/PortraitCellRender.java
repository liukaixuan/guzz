package mm.smy.bicq.user ;

/**
* 包含头像信息的一个小类。
* 
* @date 2004-4-19
* @author XF
* @copyright Copyright 2003-2004 XF All Rights Reserved.
*/

import javax.swing.Icon ;

import javax.swing.* ;
import java.awt.Component ;

public class PortraitCellRender implements ListCellRenderer{
	
     public Component getListCellRendererComponent(
     	JList list, 
     	Object value,            // value to display, this type should be PortraitObject
       	int index,               // cell index
       	boolean isSelected,      // is the cell selected
       	boolean cellHasFocus)    // the list and the cell have the focus
     {
     	
     	PortraitObject obj = (PortraitObject) value ;
       	
       	JLabel label = new JLabel() ;
       	label.setIcon(obj.getIcon()) ;

   	   if (isSelected) {
           label.setBackground(list.getSelectionBackground());
	       label.setForeground(list.getSelectionForeground());
	   }
         else {
	       label.setBackground(list.getBackground());
	       label.setForeground(list.getForeground());
	   }
	   
	 	label.setEnabled(list.isEnabled());
//	   setFont(list.getFont());
        label.setOpaque(true);
        return label ;
     }
}