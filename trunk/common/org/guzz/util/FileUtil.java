/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.guzz.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 封装了文件操作相关的一些方法。
 */
public abstract class FileUtil {
	private static final Log log = LogFactory.getLog(FileUtil.class) ;

	public static final _F_CONST CONST = new _F_CONST() ;
	
	static class _F_CONST{
		public final String LINE_FEED = System.getProperty("line.separator");
		public final String FILE_SEPARATOR = System.getProperty("file.separator") ;
	}
		
	public static String getFileExtention(String fileName){
		if(fileName == null) return null ;
		int pos = fileName.lastIndexOf('.') ;
		
		if(pos >=0 ){
			return fileName.substring(pos + 1) ;
		}
		
		return null ;
	}
	
	public static File createNewFile(String filePath) throws IOException{
		filePath = cleanFilePath(filePath) ;
		
		int pos = filePath.lastIndexOf('/') ;
		
		String folder =  filePath.substring(0, pos) ;
		String filename = filePath.substring(pos + 1) ;
		
		File mf = new File(folder) ;
		if(!mf.exists()){
			mf.mkdirs() ;
		}
		
		File file = new File(mf, filename) ;
		
		file.createNewFile() ;
		
		return file ;
	}
	
	public static String cleanFilePath(String path){
		
		if(path == null) return null ;
		
		StringBuffer sb = new StringBuffer(path.length() * 2) ;
		for(int i = 0 ; i < path.length() ; i++){
			char c = path.charAt(i) ;
			
			if( c == '\\'){
				sb.append('/') ;
			}else{
				sb.append(c) ;
			}
		}
		
		return sb.toString() ;
	}

	/**
	 * 把指定的输入流写入目标文件。
	 *
	 * @param inputStream 文件输入流
	 * @param targetFile  目标文件
	 * @throws IOException
	 */
	public static void writeFile(File targetFile, InputStream inputStream) throws IOException {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(targetFile);
			
			targetFile.createNewFile() ;
			
			byte buff[] = new byte[4096];
			int length;
			while ((length = inputStream.read(buff, 0, 4096)) != -1) {
				if(length > 0){
					fos.write(buff, 0, length);
				}
			}
		} finally {
			CloseUtil.close(fos) ;
			CloseUtil.close(inputStream) ;
		}
	}
	
	/**
	 * 读取inputStream，将读取的内容按照byte[]返回。自动关闭inputStream。
	 */
	public static byte[] readBytes(InputStream inputStream) throws IOException {
		ByteArrayOutputStream bos = null;

		try {
			bos = new ByteArrayOutputStream() ;			

			byte buff[] = new byte[4096];
			int length;
			while ((length = inputStream.read(buff, 0, 4096)) != -1) {
				if(length > 0){
					bos.write(buff, 0, length);
				}
			}
			
			return bos.toByteArray() ;
			
		} finally {
			CloseUtil.close(bos) ;
			CloseUtil.close(inputStream) ;
		}
	}
	
	/**
	 * 从一个输入流中按照指定编码读取文本。
	 * 
	 * @param is 输入流，读取完毕以后将自动关闭。
	 * @param encoding 编码
	 */
	public static String readText(InputStream is, String encoding) throws IOException {
		StringBuffer sb = new StringBuffer(8192);
		InputStreamReader isr = null ;
		
        try {
            isr = new InputStreamReader(is, encoding);
            
            char buff[] = new char[4096];
    		int length;
    		
    		while ((length = isr.read(buff, 0, 4096)) != -1) {
    			if(length > 0){ 
    				sb.append(buff, 0, length) ;
    			}
    		}
    		
            return sb.toString();
        } catch (Exception e) {
           return null ;
        }finally{
        	CloseUtil.close(isr) ;
        	CloseUtil.close(is) ;
        }
	}

	/**
	 * 覆盖写文件
	 * @param bytes 写入的内容
	 * @exception IOException
	 */
	public static void writeFile(File file, byte[] bytes) throws IOException{
		FileOutputStream fos = null ;
		BufferedOutputStream bos = null ;

		try{
			file.createNewFile() ;

			fos = new FileOutputStream(file) ;
			bos = new BufferedOutputStream(fos) ;

			bos.write(bytes) ;
		}finally{
			CloseUtil.close(bos) ;
			CloseUtil.close(fos) ;
		}
	}

	/**
	 * 覆盖写文件
	 * @param content 写入的内容
	 * @param encoding 文件的编码名称，如：UTF-8
	 * @exception IOException
	 */
	public static void writeFile(File file, String content, String encoding) throws IOException{

		if(content == null){
			content = "" ;
		}
		
		FileOutputStream fos = null ;
		OutputStreamWriter osw = null ;
		BufferedWriter bw = null ;

		try{
			file.createNewFile() ;

			fos = new FileOutputStream(file) ;
			osw = new OutputStreamWriter(fos, encoding);
	        bw = new BufferedWriter(osw);

	        bw.write(content) ;

		}finally{
			CloseUtil.close(bw) ;
			CloseUtil.close(osw) ;
			CloseUtil.close(fos) ;
		}
	}

	/**
	 * 按照一个给定的编码读取文本文件。
	 */
	public static String readTextFile(File file, String encoding) throws IOException{
        try {
        	FileInputStream fis = new FileInputStream(file) ;
            
        	return readText(fis, encoding) ;
        } catch(FileNotFoundException e){
        	log.error(e) ;
        }
        
        return null ;
	}
	
	public static boolean prepareParentFolder(File fileToWrite){
		String path = fileToWrite.getAbsolutePath() ;
		int pos = path.lastIndexOf(CONST.FILE_SEPARATOR) ;
		
		String parentFolder = path.substring(0, pos) ;
		File parentFile = new File(parentFolder) ;
		
		if(!parentFile.exists()){
			return parentFile.mkdirs() ;
		}
		
		return true ;
	}

	/**
	 * 强制删除一个目录，包含所有子目录。<br>
	 * 如果目录不存在，直接返回。
	 */
	public static void forceDeleteDirectory(File directory) throws IOException {
		if (!directory.exists())
			return;

		File[] files = directory.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					files[i].delete();
				} else {
					forceDeleteDirectory(files[i]);
				}
			}
		}
		// 最后删除当前的目录。
		directory.delete();
	}

	/**
	 * 移动一个文件，尽量忽略其中可能的错误。
	 *
	 * @param sourceDir 原目录
	 * @param destDir 要移动到的目录。
	 *
	 * @return true 如果全部移动成功
	 */
	public static boolean moveDirectory(File sourceDir, File destDir){
		if(!sourceDir.exists()){
			return true ;
		}

		boolean success =  sourceDir.renameTo(destDir) ;
		boolean success2 = true ;

		if(!success){ //递归的移动所有子文件夹
			if(!destDir.exists()) destDir.mkdirs() ; //创建目录

			File[] files = sourceDir.listFiles() ;
			for(int i = 0 ; i < files.length ; i++){
				File child = files[i] ;
				if(child.isDirectory()){
					success2 = success2 && moveDirectory(child, new File(destDir, child.getName())) ;
				}else{
					success2 = success2 && moveFile(child, new File(destDir, child.getName())) ;
				}
			}
		}

		success = success || success2 ;

		if(success){ //删除原来的文件夹
			if(sourceDir.exists()){
				success = sourceDir.delete() ; //不使用forceDeleteDirectory可以进一步的确定子文件夹的确已经成功地移动了。
			}
		}

		if(log.isDebugEnabled()){
			if(success){
				log.debug("move folder from :[" + sourceDir.getAbsolutePath() + "] to [" + destDir.getAbsolutePath() + "] successful") ;
			}else{
				log.debug("move folder from :[" + sourceDir.getAbsolutePath() + "] to [" + destDir.getAbsolutePath() + "] failed") ;
			}
		}

		return success ;
	}

	/**
	 * 移动一个文件，无论移动与否，目标文件如果存在则被删除。
	 * @param sourceFile 要移动的文件
	 * @param destFile 要移动到的目标文件
	 *
	 * @return true 如果移动成功。
	 */
	public static boolean moveFile(File sourceFile, File destFile){
		if(destFile.exists()) destFile.delete() ; //删除以后rename成功的概率会提高。

		boolean success = false ;
		success = sourceFile.renameTo(destFile) ;

		if(!success){ // rename失败，我们直接覆盖写过去。
			try {
				if(!destFile.exists()){
					destFile.createNewFile() ;
				}

				writeFile(destFile, new FileInputStream(sourceFile)) ;
			}catch (IOException e) {
				log.error("error while moving file:[" + sourceFile.getAbsolutePath() + "] to [" + destFile.getAbsolutePath() + "]", e) ;
				success = false ;
			}
		}

		if(success){
			if(sourceFile.exists()){
				success = sourceFile.delete() ; //删除原文件
			}
		}

		if(log.isDebugEnabled()){
			if(success){
				log.debug("move file from :[" + sourceFile.getAbsolutePath() + "] to [" + destFile.getAbsolutePath() + "] successful") ;
			}else{
				log.debug("move file from :[" + sourceFile.getAbsolutePath() + "] to [" + destFile.getAbsolutePath() + "] failed") ;
			}
		}

		return success ;
	}

	/**
	 * 复制一个文件
	 * @param sourceFile
	 * @param destFile
	 *
	 * @return true 如果复制成功。
	 */
	public static boolean copyFile(File sourceFile, File destFile){
		try {
			
			writeFile(destFile, new FileInputStream(sourceFile)) ;
			
			return true ;
		}catch (IOException e) {
			log.error("error while copying file:[" + sourceFile.getAbsolutePath() + "] to [" + destFile.getAbsolutePath() + "]", e) ;
			return false ;
		}
	}
}
