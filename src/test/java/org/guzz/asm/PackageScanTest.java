/**
 * 
 */
package org.guzz.asm;

import java.io.IOException;

import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import junit.framework.TestCase;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class PackageScanTest extends TestCase{
	
	public void testClassesInFileSystem() throws IOException{
		PathMatchingResourcePatternResolver pr = new PathMatchingResourcePatternResolver() ;
	    MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(pr);
	    
		Resource[] rs = pr.getResources("classpath*:nu/xom/*.class") ;
		System.out.println(rs.length) ;
		for(Resource r : rs){
			if(r.isReadable()){
				try{
					MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(r);
				    String className = metadataReader.getClassMetadata().getClassName() ;
				    
					System.out.println(className) ;
				}catch(Exception e){
					//Not a class

					System.out.println("file:" + r.getURL().toString()) ;
				}
			}else{
				System.out.println("no read:" + r.getFile().getAbsolutePath()) ;
			}
		}
	}
	
}
