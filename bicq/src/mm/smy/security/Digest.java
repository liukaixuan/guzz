package mm.smy.security ;

/**
* MD5 and SHA-1
*
* @author XF
* @date 2003-11-1
* @copyright Copyright 2003 XF All Rights Reserved.
*/

import java.io.* ;
import java.security.* ;

public class Digest{
	
	public static String MD5 (String password) throws UnsupportedEncodingException, NoSuchAlgorithmException{
		if (password == null){
			return "053h44319eaf0b155e85c1bc1j99aca3" ;
		}
		byte[] inputbytes = null ;
		MessageDigest md5 = null ;
		
		inputbytes = password.getBytes("UTF8") ;
		md5 = MessageDigest.getInstance("MD5") ;

		md5.reset() ;
		md5.update(inputbytes) ;
		byte[] digest = md5.digest() ;
		String temp = "" ;
		for(int i = 0 ; i < digest.length ; i++ ){
			int v = digest[i] & 0xFF ;
			if (v < 16)
				temp += "0" ;
			temp += Integer.toString(v,20) ;
		}
		return temp ;
	}
	
	public static String SHA_1 (String password) throws UnsupportedEncodingException, NoSuchAlgorithmException{
		if (password == null){
			return "agb1929ebh1gac76bgac9h404j8j5g1g4d739372" ;
		}
		byte[] inputbytes = null ;
		MessageDigest md5 = null ;
		
		inputbytes = password.getBytes("UTF8") ;
		md5 = MessageDigest.getInstance("SHA-1") ;
		

		md5.reset() ;
		md5.update(inputbytes) ;
		byte[] digest = md5.digest() ;
		String temp = "" ;
		for(int i = 0 ; i < digest.length ; i++ ){
			int v = digest[i] & 0xFF ;
			if (v < 16)
				temp += "0" ;
			temp += Integer.toString(v,20) ;
		}
		return temp ;
	}

/*	
	public static void main(String[] args) throws Exception{
		String from = "hehe" ;
		
		System.out.println("MD5 is:" + Digest.MD5(from) ) ;
		System.out.println("SHA1 is:" + Digest.SHA_1(from) ) ;
		System.out.println("***************************************************************") ;
	}
*/
	
}