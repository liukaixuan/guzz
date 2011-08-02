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
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.guzz.transaction.ReadonlyTranSession;

/**
 * 
 * Serializable Clob with database connection resource.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TranClob implements Clob, Serializable {

	protected transient ReadonlyTranSession tran ;
	protected Clob clob ;
	private int blobBufferSize ;
	
	/**
	 * Marker for non-contextually created {@link java.sql.Blob} instances..
	 */
	private final boolean loadedFromDB ;
	
	/**
	 * construct a TranClob that won't need to release db connections.
	 * 
	 * @param blob Blob loaded from the {@link ResultSet}. If the blob is just created in the application, use {@link #TranBlob(Blob, false)}
	 */
	public TranClob(Clob clob){
		this(clob, true) ;
	}
	
	public TranClob(Clob clob, boolean loadedFromDB){
		this.clob = clob ;
		this.loadedFromDB = loadedFromDB ;
	}
	
	public TranClob(ReadonlyTranSession tran, Clob clob){
		this.tran = tran ;
		this.clob = clob ;
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
	
	public Clob getWrappedClob(){
		if(clob == null){
			throw new IllegalStateException("Clobs may not be accessed after serialization");
		}else{
			return clob;
		}
	}
	
	public void writeOut(Writer w) throws IOException, SQLException{
		Reader r = getCharacterStream() ;
		
		char buff[] = new char[4096];
		int length ;
		
		while ((length = r.read(buff, 0, 4096)) > 0) {
			w.write(buff, 0, length);
		}
	}
	
	/**
	 * write the Reader's data into the clob at the given position.
	 * <p>
	 * aslo see {@link Clob#setCharacterStream(long)}
	 * </p>
	 * 
	 * @param reader the Reader used writing to the CLOB value. the Reader won't be closed automaticly.
	 * @param pos the position in the CLOB value at which to start writing. starting at 1.
	 */
	public void writeIntoClob(Reader reader, long pos) throws IOException, SQLException{
		Writer w = this.setCharacterStream(pos) ;

		char buff[] = new char[blobBufferSize];
		int length ;
		
		while ((length = reader.read(buff, 0, blobBufferSize)) > 0) {
			w.write(buff, 0, length);
		}
		
		//must flush, or some data will lose. (eg: oracle 10g)
		w.flush() ;
	}
	
	/**
	 * write the InputStream data into the blob at the given position.
	 * <p>
	 * aslo see {@link Clob#setAsciiStream(long)}
	 * </p>
	 * 
	 * @param is the InputStream used writing to the BLOB value. the InputStream won't be closed automaticly.
	 * @param pos the position in the BLOB value at which to start writing. starting at 1. 
	 */
	public void writeIntoClob(InputStream is, long pos) throws IOException, SQLException{
		OutputStream w = this.setAsciiStream(pos) ;

		byte buff[] = new byte[blobBufferSize];
		int length ;
		
		while ((length = is.read(buff, 0, blobBufferSize)) > 0) {
			w.write(buff, 0, length);
		}
		
		//must flush, or some data will lose. (eg: oracle 10g)
		w.flush() ;
	}
	
	/*----------------------POJO methods----------------------------------*/
	
	public String getContent() throws SQLException{
		return getSubString(1L, (int) length()) ;
	}
	
	public long getLength() throws SQLException{
		return length() ;
	}
	
	/*----------------------Clob adapter methods----------------------------------*/

	public InputStream getAsciiStream() throws SQLException {
		return getWrappedClob().getAsciiStream() ;
	}

	public Reader getCharacterStream() throws SQLException {
		return getWrappedClob().getCharacterStream() ;
	}

	public String getSubString(long pos, int length) throws SQLException {
		return getWrappedClob().getSubString(pos, length) ;
	}

	public long length() throws SQLException {
		return getWrappedClob().length() ;
	}

	public long position(String searchstr, long start) throws SQLException {
		return getWrappedClob().position(searchstr, start) ;
	}

	public long position(Clob searchstr, long start) throws SQLException {
		return getWrappedClob().position(searchstr, start) ;
	}

	public OutputStream setAsciiStream(long pos) throws SQLException {
		return getWrappedClob().setAsciiStream(pos) ;
	}

	public Writer setCharacterStream(long pos) throws SQLException {
		return getWrappedClob().setCharacterStream(pos) ;
	}

	public int setString(long pos, String str) throws SQLException {
		return getWrappedClob().setString(pos, str) ;
	}

	public int setString(long pos, String str, int offset, int len) throws SQLException {
		return getWrappedClob().setString(pos, str, offset, len) ;
	}

	public void truncate(long len) throws SQLException {
		getWrappedClob().truncate(len) ;
	}

	public int getBlobBufferSize() {
		return blobBufferSize;
	}

	public void setBlobBufferSize(int blobBufferSize) {
		this.blobBufferSize = blobBufferSize;
	}

	/**
	 * @since 1.6
	 */
	public void free() throws SQLException {
		throw new SQLException("In jdk 1.6, try getWrappedClob().free(). You still need to call close() to release the database connection in lazy load mode.") ;
	}

	/**
	 * @since 1.6
	 */
	public Reader getCharacterStream(long pos, long length) throws SQLException {
		throw new SQLException("In jdk 1.6, try getWrappedClob().getCharacterStream(long pos, long length).") ;
	}

	public boolean isLoadedFromDB() {
		return loadedFromDB;
	}

}
