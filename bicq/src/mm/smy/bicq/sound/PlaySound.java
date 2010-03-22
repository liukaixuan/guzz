
package mm.smy.bicq.sound ;

/**
* 简单的进行一下声音的播放，没有涉及到很多的细节处理。
* @date 2004-4-10
* @author XF
* 
* 
* 
* 
*/

import javax.swing.* ;

import java.applet.Applet ;
import java.applet.AudioClip;
import java.net.URL;
import java.net.MalformedURLException;

public class PlaySound{
	
	private static String baseURL = "file:/" + System.getProperty("user.dir") + "/sound/" ;
	
	public static final int CALL = 944 ;
	public static final int FOLDER = 945 ;
	public static final int GLOBAL = 946 ;
	public static final int MSG = 947 ;
	public static final int RING = 948 ;
	public static final int SYSTEM = 949 ;
	public static final int ONLINE = 940 ;
	
	private static AudioClip call ;
	private static AudioClip folder ;
	private static AudioClip global ;
	private static AudioClip msg ;
	private static AudioClip ring ;
	private static AudioClip system ;
	private static AudioClip online ;
	
	static{
	//	System.out.println("static inti starts: baseURL:" + baseURL) ;
		try{
			call = Applet.newAudioClip(new URL(baseURL + "call.wav") );
			folder = Applet.newAudioClip(new URL(baseURL + "folder.wav")) ;
			global = Applet.newAudioClip(new URL(baseURL + "global.wav")) ;
			msg = Applet.newAudioClip(new URL(baseURL + "msg.wav")) ;
			ring = Applet.newAudioClip(new URL(baseURL + "ring.wav")) ;
			system = Applet.newAudioClip(new URL(baseURL + "system.wav")) ;
			online = Applet.newAudioClip(new URL(baseURL + "online.wav")) ;
		}catch(Exception e){
			System.out.println("init sound faied.") ;
			e.printStackTrace() ;
		}		
	}
	
	
	public static void play(int type){
//		System.out.println("playing:") ;
		
		switch(type){
			case CALL :
				call.play() ;
				break ;
			case FOLDER :
				folder.play() ;
				break ;
			case GLOBAL :
				global.play() ;
				break ;
			case MSG :
				msg.play() ;
				break ;
			case RING :
				ring.play() ;
				break ;
			case SYSTEM :
				system.play() ;
				break ;
			case ONLINE :
				online.play() ;
				break ;
			default:
				//TODO: add to log here
		}
	}
	
	public static void main(String[] args){
	//	PlaySound.play(PlaySound.CALL) ;
		PlaySound.play(PlaySound.ONLINE) ;
	//	JFrame frame = new JFrame() ;
	//	frame.show() ;
	//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;
		//System.out.println(System.getProperty("user.dir")) ;
	}
	
}