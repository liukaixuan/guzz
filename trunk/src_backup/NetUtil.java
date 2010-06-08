package org.guzz.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 网络处理相关的工具类. <BR>
 * 
 */
public class NetUtil {
	private static final Log LOG = LogFactory.getLog(NetUtil.class) ;
    
    private static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager() ;
  	private static HttpClient client = new HttpClient(connectionManager) ;   
  	
  	static{
  		connectionManager.getParams().setMaxTotalConnections(50) ;
  		connectionManager.getParams().setMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION, 15) ;
  	}
    
  	/**通过HttpClient读取一个URL地址的内容。如果出现错误，返回null。 本方法不会对外抛出异常。*/
    public static String readContentOfURL(String url, String responseEncoding){
    	InputStream is = null ;
		
		if(LOG.isDebugEnabled()){
			LOG.debug("reading from url:" + url) ;
		}
		
	    GetMethod get = new GetMethod(url);
	    try {
	        try {
				client.executeMethod(get);
				 is = get.getResponseBodyAsStream() ;
				 
			     return FileUtil.readText(is, responseEncoding == null ? get.getResponseCharSet() : responseEncoding) ;
			     
			} catch (HttpException e) {
				LOG.error("HttpException.reading:" + url, e) ;
				e.printStackTrace();
			} catch (IOException e) {
				LOG.error("IOException.reading:" + url, e) ;
			}
	    } finally {
	        // return the connection back to the connection manager
	        get.releaseConnection();
	    }
	    
	    return null ;
    }

    /**
     * 测试是否能与指定主机名或IP, 指定端口建立TCP连接.
     * @param host 给定的主机名或IP
     * @param port
     * @return 如果能成功建立TCP连接, 返回true; 否则返回false
     */
    public static boolean canConnect(String host, int port) {
        if (host == null) {
            return false;
        }
        if (port < 0 || port > 65535) {
            return false;
        }
        host = host.trim();
        if (host.length() == 0) {
            return false;
        }

        Socket socket = null;
        try {
            socket = new Socket(host, port);
            return true;
        } catch (UnknownHostException e) {
            LOG.error("UnknownHost! host=" + host, e);
        } catch (IOException e) {
            LOG.error("IOException! host=" + host + ", port=" + port, e);
        } finally {
            CloseUtil.close(socket);
        }

        return false;
    }

    /**
     * 获取本机的一个IP地址(非本地回环IP地址).
     * @return 本机的一个IP地址(排除本地回环IP地址)
     * @since JDK1.4
     */
    public static String getIPAddress() {
        Enumeration nets = null;
        try {
            nets = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (nets == null) {
            return "";
        }
        NetworkInterface netInterface;
        for (; nets.hasMoreElements();) {
            netInterface = (NetworkInterface) nets.nextElement();
            /*            if (logger.isDebugEnabled()) {
             logger.debug("name:" + netInterface.getName() + ", displayName:" + netInterface.getDisplayName());
             }*/
            Enumeration ips = netInterface.getInetAddresses();
            if (ips == null) {
                return "";
            }

            for (; ips.hasMoreElements();) {
                InetAddress address = (InetAddress) ips.nextElement();
                final String ipAddress = address.getHostAddress();
                if (ipAddress.equals("127.0.0.1")) {
                    continue;
                }
                return ipAddress;
            }
        }
        return "";
    }

    /**
     * 获取本机的所有IP地址.
     * @return 本机的所有IP地址(包括本地回环IP地址)
     * @since JDK1.4
     */
    public static InetAddress[] getAllInetAdresses() {
        Enumeration nets = null;
        try {
            nets = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            LOG.error("fail to getNetworkInterfaces!", e);
        }
        if (nets == null) {
            return null;
        }
        NetworkInterface ni;
        Enumeration ips;
        List results = new ArrayList();
        for (; nets.hasMoreElements();) {
            ni = (NetworkInterface) nets.nextElement();
            if (LOG.isDebugEnabled()) {
                LOG.debug("name:" + ni.getName() + ", displayName:" + ni.getDisplayName());
            }
            ips = ni.getInetAddresses();
            if (ips == null) {
                continue;
            }
    
            for (; ips.hasMoreElements();) {
                results.add(ips.nextElement());
            }
        }
        return (InetAddress[]) results.toArray(new InetAddress[0]);
    }
    
    public static String[] getHostNameIps() {
        InetAddress[] ias = getAllInetAdresses();
        if (ias == null) {
            return null;
        }
        List results = new ArrayList();
        for (int i = 0; i < ias.length; i++) {
            if ("127.0.0.1".equals(ias[i].getHostAddress()) && ias.length > 1) {
                continue;
            }
            results.add(ias[i].toString());
        }
        return (String[]) results.toArray(new String[0]);
    }

}
