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
package org.guzz;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.guzz.pojo.lob.BlobImpl;
import org.guzz.pojo.lob.ClobImpl;
import org.guzz.pojo.lob.TranBlob;
import org.guzz.pojo.lob.TranClob;

/**
 * 
 * 
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class Guzz {

	/**
	 * Create a new <tt>Blob</tt>. The returned object will be initially
	 * immutable.
	 * 
	 * @param bytes a byte array
	 * @return the Blob
	 */
	public static TranBlob createBlob(byte[] bytes) {
		return new TranBlob(new BlobImpl(bytes)) ;
	}

	/**
	 * Create a new <tt>Blob</tt>. The returned object will be initially
	 * immutable.
	 * 
	 * @param stream a binary stream
	 * @param length the number of bytes in the stream
	 * @return the Blob
	 */
	public static TranBlob createBlob(InputStream stream, int length) {
		return new TranBlob(new BlobImpl(stream, length)) ;
	}

	/**
	 * Create a new <tt>Blob</tt>. The returned object will be initially
	 * immutable.
	 * 
	 * @param stream a binary stream
	 * @return the Blob
	 * @throws IOException
	 */
	public static TranBlob createBlob(InputStream stream) throws IOException {
		return new TranBlob(new BlobImpl(stream, stream.available())) ;
	}

	/**
	 * Create a new <tt>Clob</tt>. The returned object will be initially
	 * immutable.
	 * 
	 * @param string a <tt>String</tt>
	 */
	public static TranClob createClob(String string) {
		return new TranClob(new ClobImpl(string)) ;
	}

	/**
	 * Create a new <tt>Clob</tt>. The returned object will be initially
	 * immutable.
	 * 
	 * @param reader a character stream
	 * @param length the number of characters in the stream
	 */
	public static TranClob createClob(Reader reader, int length) {
		return new TranClob(new ClobImpl(reader, length)) ;
	}

}
