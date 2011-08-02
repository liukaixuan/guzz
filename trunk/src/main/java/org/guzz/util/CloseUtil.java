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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.Service;
import org.guzz.io.Resource;


public class CloseUtil {
    private static final Log log = LogFactory.getLog(CloseUtil.class) ;

    /**
     * 关闭给定的输入流. <BR>
     * @param inStream
     */
    public static void close(InputStream inStream) {
        if (inStream != null) {
            try {
                inStream.close();
            } catch (IOException e) {
                log.error("error on close the inputstream.", e);
            }
        }
    }

    /**
     * 关闭给定的输出流. <BR>
     * @param outStream
     */
    public static void close(OutputStream outStream) {
        if (outStream != null) {
            try {
                outStream.close();
            } catch (IOException e) {
                log.error("error on close the outputstream.", e);
            }
        }
    }
    
    /**
     * 关闭给定的输出流. <BR>
     * @param writer
     */
    public static void close(Writer writer) {
        if (writer != null) {
            try {
            	writer.close();
            } catch (IOException e) {
                log.error("error on close the outputstream.", e);
            }
        }
    }

    /**
     * 关闭给定的Socket.
     * @param socket 给定的Socket
     */
    public static void close(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                log.error("fail on close socket: " + socket, e);
            }
        }
    }

	public static void close(Reader reader) {
		if (reader != null) {
            try {
            	reader.close();
            } catch (IOException e) {
                log.error("error on close the Reader.", e);
            }
        }
	}
	
	public static void close(Connection conn) {
		if (conn != null) {
            try {
            	conn.close();
            } catch (Exception e) {
                log.error("error on close java.sql.Connection.", e);
            }
        }
	}
	
	public static void close(PreparedStatement ps) {
		if (ps != null) {
            try {
            	ps.close();
            } catch (Exception e) {
                log.error("error on close java.sql.PreparedStatement.", e);
            }
        }
	}
	
	public static void close(ResultSet rs) {
		if (rs != null) {
            try {
            	rs.close();
            } catch (Exception e) {
                log.error("error on close java.sql.ResultSet.", e);
            }
        }
	}

	public static void close(Statement st) {
		if(st != null){
			try {
				st.close() ;
			} catch (SQLException e) {
				log.error("error on close java.sql.Statement.", e);
			}
		}
	}

	public static void close(Resource r) {
		if(r != null){
			try {
				r.close() ;
			} catch (Exception e) {
				log.error("error on close org.guzz.io.Resource.", e);
			}
		}
	}
	
	public static void close(Service service) {
		if(service != null){
			try {
				service.shutdown() ;
			} catch (Exception e) {
				if(service.getServiceInfo() != null){
					log.error("error on shutting down service:[" + service.getServiceInfo().getServiceName() + "].", e) ;
				}else{
					log.error("error on shutting down service:[" + service + "].", e) ;
				}
			}
		}
	}

}
