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
package org.guzz.pojo.lob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.guzz.transaction.ReadonlyTranSession;

/**
 * 
 * Serializable Blob with database connection resource.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TranBlob implements Blob, Serializable {

	protected transient ReadonlyTranSession tran ;
	protected Blob blob ;
	
	private int blobBufferSize = 4096 ;
	
	/**
	 * Marker for non-contextually created {@link java.sql.Blob} instances..
	 */
	private final boolean loadedFromDB ;

	/**
	 * construct a TranBlob that won't need to release db connections.
	 * 
	 * @param blob Blob loaded from the {@link ResultSet}. If the blob is just created in the application, use {@link #TranBlob(Blob, false)}
	 */
	public TranBlob(Blob blob){
		this(blob, true) ;
	}
	
	public TranBlob(Blob blob, boolean loadedFromDB){
		this.blob = blob ;
		this.loadedFromDB = loadedFromDB ;
	}
	
	public TranBlob(ReadonlyTranSession tran, Blob blob){
		this.tran = tran ;
		this.blob = blob ;
		this.loadedFromDB = true ;
	}
	
	/**
	 * close related connections.
	 */
	public void close(){
		if(tran != null){
			tran.close() ;
		}
	}
	
	public Blob getWrappedBlob(){
		if(blob == null){
			throw new IllegalStateException("Blobs may not be accessed after serialization");
		}else{
			return blob;
		}
	}
	
	public void writeOut(OutputStream os) throws IOException, SQLException{
		InputStream inputStream = getBinaryStream() ;
		
		byte buff[] = new byte[blobBufferSize];
		int length ;
		
		while ((length = inputStream.read(buff, 0, blobBufferSize)) > 0) {
			os.write(buff, 0, length);
		}
	}
	
	/**
	 * write the InputStream data into the blob at the given position.
	 * <p>
	 * aslo see {@link Blob#setBinaryStream(long)}
	 * </p>
	 * 
	 * @param is the InputStream used writing to the BLOB value. the InputStream won't be closed automaticly.
	 * @param pos the position in the BLOB value at which to start writing. starting at 1.
	 */
	public void writeIntoBlob(InputStream is, long pos) throws IOException, SQLException{
		OutputStream os = this.setBinaryStream(pos) ;

		byte buff[] = new byte[blobBufferSize];
		int length ;
		
		while ((length = is.read(buff, 0, blobBufferSize)) > 0) {
			os.write(buff, 0, length);
		}
		
		//must flush, or some data will lose. (eg: oracle 10g)
		os.flush() ;
	}
	
	/*----------------------POJO methods----------------------------------*/
	
	public byte[] getContent() throws SQLException{
		return getBytes(1L, (int) length()) ;
	}
	
	public long getLength() throws SQLException{
		return length() ;
	}

	/*----------------------Blob Adapter methods----------------------------------*/
	
	public InputStream getBinaryStream() throws SQLException {
		return getWrappedBlob().getBinaryStream() ;
	}

	public byte[] getBytes(long pos, int length) throws SQLException {
		return getWrappedBlob().getBytes(pos, length) ;
	}

	public long length() throws SQLException {
		return getWrappedBlob().length() ;
	}

	public long position(byte[] pattern, long start) throws SQLException {
		return getWrappedBlob().position(pattern, start) ;
	}

	public long position(Blob pattern, long start) throws SQLException {
		return getWrappedBlob().position(pattern, start) ;
	}

	public OutputStream setBinaryStream(long pos) throws SQLException {
		return getWrappedBlob().setBinaryStream(pos) ;
	}

	public int setBytes(long pos, byte[] bytes) throws SQLException {
		return getWrappedBlob().setBytes(pos, bytes) ;
	}

	public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
		return getWrappedBlob().setBytes(pos, bytes, offset, len) ;
	}

	public void truncate(long len) throws SQLException {
		getWrappedBlob().truncate(len) ;
	}

	public int getBlobBufferSize() {
		return blobBufferSize;
	}

	public void setBlobBufferSize(int blobBuffer) {
		this.blobBufferSize = blobBuffer;
	}

	/**
	 * @since 1.6
	 */
	public void free() throws SQLException {
		throw new SQLException("In jdk 1.6, try getWrappedBlob().free(). You still need to call close() to release the database connection in lazy load mode.") ;
	}

	/**
	 * @since 1.6
	 */
	public InputStream getBinaryStream(long pos, long length) throws SQLException {
		throw new SQLException("In jdk 1.6, try getWrappedBlob().getBinaryStream(long pos, long length).") ;
	}

	public boolean isLoadedFromDB() {
		return loadedFromDB;
	}

}
