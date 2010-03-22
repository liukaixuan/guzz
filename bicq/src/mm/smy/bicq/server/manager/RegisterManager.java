package mm.smy.bicq.server.manager ;

/**
* 实现对RegisterMessage的分流处理。
* 
* @author XF
* @date 2003-11-23
* 
* 
*/

import mm.smy.bicq.server.StartServer ;

import mm.smy.bicq.message.ReceivedMessage ;
import mm.smy.bicq.message.ICMPMessage ;
import mm.smy.bicq.message.RegisterMessage ;
import mm.smy.bicq.message.MessageType ;

import mm.smy.bicq.user.User ;


import mm.smy.bicq.server.user.InsertUserDB ;
import mm.smy.bicq.server.user.ServerGuest ;

import mm.smy.bicq.server.db.BugWriter ;

import java.util.Date ;
import java.sql.SQLException ;


public class RegisterManager{
	
	private StartServer ss = null ;
	
	public RegisterManager(StartServer m_ss){
		ss = m_ss ;
	}
	
	public void messageAction(ReceivedMessage rm){
		if(rm == null) return ;
		if(rm.getType() != MessageType.REGISTER_MESSAGE) return ;
		
		RegisterMessage message = new RegisterMessage() ;
		message.setByteContent(rm.getContent()) ;
		//这儿我们应该察看mintype，不过因为它只有一种mintype，所以现在就省了。
		
		User u = message.getUser() ;
		
		ICMPMessage icmp = new ICMPMessage() ;
		if(u == null){
			icmp.setMinType(ICMPMessage.REGISTER_RESULT_FAIL) ;
			icmp.setContent("不知道怎么回事，您传过来 资料 为空~~~~~；\n请和我们联系。") ;
			ss.sendMessage(icmp.getByteContent(), icmp.getType(), 1000, rm.getFrom(),rm.getIP(), message.getPort()) ;
			return ;
		}
		
		int number = ss.getNewNumber() ;
		
		ServerGuest guest = new ServerGuest() ;
		guest.setNumber(number) ;
		guest.setAddress(u.getAddress()) ;
		guest.setBirthday(u.getYear(),u.getMonth(),u.getDay()) ;
		guest.setCounty(u.getCountry()) ;
		guest.setExplain(u.getExplain()) ;
		guest.setGender(u.getGender()) ;
		guest.setHomepage(u.getHomepage()) ;
		guest.setLastLoginIP(rm.getIP()) ;
		guest.setLastLoginTime(new Date()) ;
		guest.setLeaveWord(u.getLeaveWord()) ;
		guest.setMail(u.getMail()) ;
		guest.setNickname(u.getNickname()) ;
		guest.setPassword(message.getPassword()) ;
		guest.setPortrait(u.getPortrait()) ;
		guest.setProvince(u.getProvince()) ;
		guest.setRealname(u.getRealname()) ;
		guest.setRegisterIP(rm.getIP()) ;
		guest.setRegisterTime(new Date()) ;
		guest.setState(u.getState()) ;
		guest.setTelephone(u.getTelephone()) ;
		guest.setZip(u.getZip()) ;
		
		
		InsertUserDB insert = new InsertUserDB(guest) ;
		boolean success = false ;
		try{
			insert.update() ;
			success = true ;
		}catch(SQLException e){
			success = false ;
			BugWriter.log(this, e, "无法插入新用户：" + guest.getNumber() ) ;	
		}finally{
			insert.close() ;	
		}
		
		if(success){
			icmp.setMinType(ICMPMessage.REGISTER_RESULT_SUCCESS) ;
			icmp.setContent(new Integer(number).toString()) ;	
		}else{
			icmp.setMinType(ICMPMessage.REGISTER_RESULT_FAIL) ;
			icmp.setContent("向数据库中插入新用户时出现错误，我们为您申请的号码是：" + number + " ，您也可以试试能不能用。\n您可以重新申请，如果该问题依旧，请和我们联系。") ;	
		}
		
		ss.sendMessage(icmp.getByteContent(),icmp.getType(), 1000, number, rm.getIP(), message.getPort()) ;
		
		return ;
	}
	
	
	
	
	
}



