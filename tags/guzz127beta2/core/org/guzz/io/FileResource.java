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
package org.guzz.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.guzz.util.CloseUtil;

/**
 * 
 * 基于文件的resource
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class FileResource implements Resource {
	public static final String CLASS_PATH_PREFIX = "classpath:" ;
	
	File file ;
	
	InputStream fis = null ;
	
	public FileResource(File f){
		this.file = f ;
	}
	
	/**支持以classpath:开头的路径和文件的绝对路径2种方式**/
	public FileResource(String fileName){
		this(null, fileName) ;
	}	
	
	public FileResource(File relatedFile, String fileName){
		if (fileName.startsWith(CLASS_PATH_PREFIX)){
			initClassPathFile(fileName) ;			
		}else{
			if(relatedFile == null){
				this.file = new File(fileName) ;
			}else{
				if(relatedFile.isDirectory()){
					this.file = new File(relatedFile, fileName) ;
				}else{
					this.file = new File(relatedFile.getParentFile(), fileName) ;
				}
			}
		}
	}
	
	protected void initClassPathFile(String classpathFile){
		String classPath = FileResource.class.getResource("/").getFile() ;
		String m_fileName = classpathFile.substring(CLASS_PATH_PREFIX.length()) ;	
		this.file = new File(classPath, m_fileName) ;
	}

	public void close() {
		CloseUtil.close(fis) ;
	}

	public InputStream getInputStream() throws IOException {
		if(fis == null){
			fis = new FileInputStream(file) ;
		}
		
		if(fis == null){
			throw new IOException("resource is available. file is:" + (this.file == null ? null : this.file.getAbsolutePath())) ;
		}
		
		return fis;
	}
	
	public String toString(){
		return "file resource. file is:" + (this.file == null ? null : this.file.getAbsolutePath()) ;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
