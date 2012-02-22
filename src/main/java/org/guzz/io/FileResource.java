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
import java.net.URLDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.util.CloseUtil;

/**
 * 
 * 基于文件的resource
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class FileResource implements Resource {
	private static final Log log = LogFactory.getLog(FileResource.class) ;
	
	public static final String CLASS_PATH_PREFIX = "classpath:" ;
	
	protected File file ;
	
	protected String classPath ;
	
	InputStream fis = null ;
	
	protected final boolean streamResource ;
	
	public FileResource(File f){
		this.file = f ;
		this.streamResource = false ;
	}
	
	/**支持以classpath:开头的路径和文件的绝对路径2种方式**/
	public FileResource(String fileName){
		this(null, fileName) ;
	}	
	
	public FileResource(Resource relativedResource, String fileName){
		if (fileName.startsWith(CLASS_PATH_PREFIX)){
			initClassPathFile(fileName) ;
			this.streamResource = true ;
		}else{
			if(relativedResource == null){
				this.file = new File(fileName) ;
			}else if(relativedResource instanceof FileResource){
				File relativedFile = ((FileResource) relativedResource).getFile() ;
				
				File f = new File(fileName) ;
				if(f.isAbsolute()){
					this.file = f ;
				}else{
					this.file = new File(relativedFile.getParentFile(), fileName) ;
				}
			}else{
				log.warn("unknown relatived Resource:" + relativedResource) ;
			}
			
			this.streamResource = false ;
		}
	}
	
	protected void initClassPathFile(String classpathFile){
		this.classPath = classpathFile.substring(CLASS_PATH_PREFIX.length()) ;
		
		String classRootPath = FileResource.class.getResource("/").getFile() ;
		String m_fileName = classpathFile.substring(CLASS_PATH_PREFIX.length()) ;
		
		//The warning is not reasonable. The file encoding should be the native one in this situation.
		this.file = new File(URLDecoder.decode(classRootPath), m_fileName) ;
	}

	public void close() {
		CloseUtil.close(fis) ;
	}

	public InputStream getInputStream() throws IOException {
		if(fis == null){
			if(isStreamResource()){
				fis = FileResource.class.getClassLoader().getResourceAsStream(this.classPath) ;
			}else{
				fis = new FileInputStream(file) ;
			}
		}
		
		if(fis == null){
			//must be failed stream resource, OR FileInputStream will raise a exception above.
			throw new IOException("resource is not available. file is:" + this.classPath) ;
		}
		
		return fis;
	}
	
	public String toString(){
		return "file resource. file is:" + (this.file == null ? this.classPath : this.file.getAbsolutePath()) ;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isStreamResource() {
		return streamResource;
	}

}
