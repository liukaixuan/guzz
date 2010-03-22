package mm.smy.bicq ;

/**
* 提供对所有头像的处理。
* @date 2003-11-17
* 
* 
* 
* 
* 
*/

import java.io.* ;

import javax.swing.ImageIcon ;
import javax.swing.Icon ;
import java.awt.image.MemoryImageSource ;
import java.awt.Image ;

import mm.smy.bicq.user.User ;

public class FaceManager{
	
	/**
	* 获得指定头像编号，制定状态的 头像ImageIcon。如果有错误，返回第1号的头像。
	* @param portrait 头像的编号，如果系统没有找到，返回默认的头像。第0号。
	* @param state 目前该头像好友的状态。
	* @return 获得该头像该状态的ImageIcon对象。
	*/
	public static ImageIcon getFaceIcon(int portrait, int state){
    	if(portrait < 1 ) portrait = 1 ; //以前默认的是70
    	
    	String path = "face/" + portrait ;
    	
    	if(state == User.ONLINE)
    		path = path + "-1.bmp" ;
    	else if(state == User.LEAVE)
    		path = path + "-3.bmp" ;
    	else
    		path = path + "-2.bmp" ;
    	
 	//	File file = new File(path) ;
    //	System.out.println(file.getAbsolutePath()) ;
   	
    	Image bmp = null ;
    	try{
    		bmp = loadBitmap(path) ;
    	}catch(Exception e){    		
    		System.out.println("face manager:" + e.getMessage() ) ;
    		try{
    			bmp = loadBitmap("face/1-1.bmp") ;	
    		}catch(Exception ee){
    			return null ;	
    		}
    	}
    	return new ImageIcon(bmp) ;
    	
 /*       java.net.URL imgURL = GuestPanel.class.getResource(path) ;
        if (imgURL != null) {
            return new ImageIcon(imgURL) ;
        }else {
            System.err.println("Couldn't find file: " + path) ;
            return null ;
        }		
*/	}
	
	
   /**
 	*读取未压缩的 24 位和 8 位图像。
 	*如果图像不是 24 位或 8 位图像或是被压缩，抛出IOException
 	@param dir 要加载位图的路径。
	@return Image 返回声称的Image对象
	@exception FileNotFoundException, IOException
    */	
	public static Image loadBitmap(String dir) throws FileNotFoundException, IOException{
		Image image ;
		//System.out.println("loading:"+sdir+sfile) ;

    	FileInputStream fs=new FileInputStream(dir) ;
    	BufferedInputStream bis = new BufferedInputStream(fs) ;
    	int bflen=14 ; // 14 字节 BITMAPFILEHEADER
    	byte bf[]=new byte[bflen] ;
   		bis.read(bf,0,bflen) ;
    	int bilen=40 ; // 40 字节 BITMAPINFOHEADER
    	byte bi[]=new byte[bilen] ;
    	bis.read(bi,0,bilen) ;

     	// 解释数据。
    	int nsize = (((int)bf[5]&0xff)<<24) 
  		| (((int)bf[4]&0xff)<<16)
  		| (((int)bf[3]&0xff)<<8)
  		| (int)bf[2]&0xff ;
 		//System.out.println("File type is :"+(char)bf[0]+(char)bf[1]) ;
 		//System.out.println("Size of file is :"+nsize) ;

   		int nbisize = (((int)bi[3]&0xff)<<24)
 		| (((int)bi[2]&0xff)<<16)
 		| (((int)bi[1]&0xff)<<8)
		| (int)bi[0]&0xff ;
 	   // System.out.println("Size of bitmapinfoheader is :"+nbisize) ;

     	int nwidth = (((int)bi[7]&0xff)<<24)
  		| (((int)bi[6]&0xff)<<16)
  		| (((int)bi[5]&0xff)<<8)
  		| (int)bi[4]&0xff ;
    	//System.out.println("Width is :"+nwidth) ;

     	int nheight = (((int)bi[11]&0xff)<<24)
  		| (((int)bi[10]&0xff)<<16)
  		| (((int)bi[9]&0xff)<<8)
  		| (int)bi[8]&0xff ;
    	//System.out.println("Height is :"+nheight) ;

     	int nplanes = (((int)bi[13]&0xff)<<8) | (int)bi[12]&0xff ;
     	//System.out.println("Planes is :"+nplanes) ;

     	int nbitcount = (((int)bi[15]&0xff)<<8) | (int)bi[14]&0xff ;
     	//System.out.println("BitCount is :"+nbitcount) ;

     	// 查找表明压缩的非零值
     	int ncompression = (((int)bi[19])<<24)
  		| (((int)bi[18])<<16)
 		| (((int)bi[17])<<8)
  		| (int)bi[16] ;
     	//System.out.println("Compression is :"+ncompression) ;

    	int nsizeimage = (((int)bi[23]&0xff)<<24)
  		| (((int)bi[22]&0xff)<<16)
  		| (((int)bi[21]&0xff)<<8)
  		| (int)bi[20]&0xff ;
  		//System.out.println("SizeImage is :"+nsizeimage) ;

     	int nxpm = (((int)bi[27]&0xff)<<24)
  		| (((int)bi[26]&0xff)<<16)
  		| (((int)bi[25]&0xff)<<8)
  		| (int)bi[24]&0xff ;
   		//System.out.println("X-Pixels per meter is :"+nxpm) ;

     	int nypm = (((int)bi[31]&0xff)<<24)
  		| (((int)bi[30]&0xff)<<16)
  		| (((int)bi[29]&0xff)<<8)
  		| (int)bi[28]&0xff ;
     	//System.out.println("Y-Pixels per meter is :"+nypm) ;

  		int nclrused = (((int)bi[35]&0xff)<<24)
 		| (((int)bi[34]&0xff)<<16)
		| (((int)bi[33]&0xff)<<8)
 		| (int)bi[32]&0xff ;
     	//System.out.println("Colors used are :"+nclrused) ;

     	int nclrimp = (((int)bi[39]&0xff)<<24)
  		| (((int)bi[38]&0xff)<<16)
  		| (((int)bi[37]&0xff)<<8)
  		| (int)bi[36]&0xff ;
    	//System.out.println("Colors important are :"+nclrimp) ;

     	if (nbitcount==24) {
  			// 24 位格式不包含调色板数据，但扫描行被补足到
  			// 4 个字节。
  			int npad = (nsizeimage / nheight) - nwidth * 3 ;
  			int ndata[] = new int [nheight * nwidth] ;
  			byte brgb[] = new byte [( nwidth + npad) * 3 * nheight] ;
  			bis.read (brgb, 0, (nwidth + npad) * 3 * nheight) ;
  			int nindex = 0 ;
  			for (int j = 0 ; j < nheight ; j++){
      			for (int i = 0 ; i < nwidth ; i++){
   					ndata [nwidth * (nheight - j - 1) + i] =
       				(255&0xff)<<24
       				| (((int)brgb[nindex+2]&0xff)<<16)
       				| (((int)brgb[nindex+1]&0xff)<<8)
       				| (int)brgb[nindex]&0xff ;
    				//System.out.println("Encoded Color at (" +i+","+j+")is:"+brgb+" (R,G,B)= (" +((int)(brgb[2]) & 0xff)+","+((int)brgb[1]&0xff)+"," +((int)brgb[0]&0xff)+")") ;
   					nindex += 3 ;
   				}
      			nindex += npad ;
      		}
  			image = java.awt.Toolkit.getDefaultToolkit().createImage( new MemoryImageSource (nwidth, nheight, ndata, 0, nwidth)) ;
  		}else if (nbitcount == 8){
 			// 必须确定颜色数。如果 clrsused 参数大于 0，
  			// 则颜色数由它决定。如果它等于 0，则根据
 			// bitsperpixel 计算颜色数。
  			int nNumColors = 0 ;
  			if (nclrused > 0){
     			nNumColors = nclrused ;
      		}else{
      			nNumColors = (1&0xff)<<nbitcount ;
      		}
  			//System.out.println("The number of Colors is"+nNumColors) ;

  			// 某些位图不计算 sizeimage 域，请找出
  			// 这些情况并对它们进行修正。
  			if (nsizeimage == 0){
      			nsizeimage = ((((nwidth*nbitcount)+31) & ~31 ) >> 3) ;
      			nsizeimage *= nheight ;
      			//System.out.println("nsizeimage (backup) is"+nsizeimage) ;
      		}

  			// 读取调色板颜色。
  			int npalette[] = new int [nNumColors] ;
  			byte bpalette[] = new byte [nNumColors*4] ;
  			bis.read (bpalette, 0, nNumColors*4) ;
  			int nindex8 = 0 ;
  			for (int n = 0 ; n < nNumColors ; n++){
      			npalette[n] = (255&0xff)<<24
   				| (((int)bpalette[nindex8+2]&0xff)<<16)
   				| (((int)bpalette[nindex8+1]&0xff)<<8)
   				| (int)bpalette[nindex8]&0xff ;
       			//System.out.println ("Palette Color "+n
				//+" is:"+npalette[n]+" (res,R,G,B)= ("
   				//+((int)(bpalette[nindex8+3]) & 0xff)+","
   				//+((int)(bpalette[nindex8+2]) & 0xff)+","
   				//+((int)bpalette[nindex8+1]&0xff)+","
   				//+((int)bpalette[nindex8]&0xff)+")") ;
      			nindex8 += 4 ;
      		}

  			// 读取图像数据（实际上是调色板的索引）
  			// 扫描行仍被补足到 4 个字节。
  			int npad8 = (nsizeimage / nheight) - nwidth ;
  			//System.out.println("nPad is:"+npad8) ;

  			int ndata8[] = new int [nwidth*nheight] ;
  			byte bdata[] = new byte [(nwidth+npad8)*nheight] ;
  			bis.read (bdata, 0, (nwidth+npad8)*nheight) ;
  			nindex8 = 0 ;
  			for (int j8 = 0 ; j8 < nheight ; j8++){
      			for (int i8 = 0 ; i8 < nwidth ; i8++){
   					ndata8 [nwidth*(nheight-j8-1)+i8] =
       				npalette [((int)bdata[nindex8]&0xff)] ;
   					nindex8++ ;
   				}
      		nindex8 += npad8 ;
     	 }
     	 
  		 image = java.awt.Toolkit.getDefaultToolkit().createImage( new MemoryImageSource (nwidth, nheight, ndata8, 0, nwidth)) ;
  		}else{
  			bis.close() ;
  			fs.close() ;
  			throw new IOException("bmp type not supported.目前只支持8位与24位未压缩位图。") ;
  		}

     	bis.close() ;
     	return image ;
	}
	
	
	
	
	
	
	
	
}
