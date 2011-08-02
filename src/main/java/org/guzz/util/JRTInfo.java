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

/**
 * Java运行时环境信息. <BR>
 * 
 */
public class JRTInfo {

    private static final boolean isIBMJDK;
    private static final boolean isJDK14;
    private static final boolean isJDK50 ;
    private static final String jdkVer;

    static {
        isIBMJDK = vendorIsIBM();
        isJDK14 = isJDKGreaterThan14();
        jdkVer = getJDKVersion0();
        isJDK50 = "5.0".equals(jdkVer) ;
    }

    /**
     * 是否是IBM JDK.
     * @return 是否是IBM JDK
     */
    public static boolean isIBMJDK() {
        return isIBMJDK;
    }

    /**
     * 是否是JDK1.4以上版本.
     * @return 是JDK1.4以上版本返回true, 否则返回false.
     */
    public static boolean isJDK14OrHigher() {
        return isJDK14;
    }
    
    /**
     * 是否是JDK5.0以上版本.
     * @return 是JDK5.0以上版本返回true, 否则返回false.
     */
    public static boolean isJDK50OrHigher() {
        return isJDK50;
    }

    /**
     * 返回JDK的版本字符串. 该字符串包括主版本和次版本, 不包括维护版本.
     * @return JDK的版本, 所有可能的取值有:
     *          <li>"1.0"
     *          <li>"1.1"
     *          <li>"1.2"
     *          <li>"1.3"
     *          <li>"1.4"
     *          <li>"5.0"
     */
    public static String getJDKVersion() {
        return jdkVer;
    }

    /**
     * 获取该JVM的基本信息, 包括版本, 厂商, 虚拟机最大内存, 操作系统等信息.
     */
    public static String getJavaEnvInfo() {
        try {
            Runtime runtime = Runtime.getRuntime();
            StringBuffer sb = new StringBuffer(256);
            sb.append("[JavaEnv]:").append(System.getProperty("java.version")).append(',').append(System.getProperty("java.vendor")).append(';');
            sb.append("[Memory]:").append(runtime.maxMemory() / 1048576).append("MB").append(';');
            sb.append("[userdir]:").append(System.getProperty("user.dir")).append(';');
            sb.append("[OS]:").append(System.getProperty("os.name")).append(',').append(System.getProperty("os.version")).append(',').append(System.getProperty("os.arch"));
            return sb.toString();
        } catch (Throwable t) {
            //such as: java.lang.NoClassDefFoundError: java/lang/Runtime    (WebappClassLoader: Lifecycle error : CL stopped)
            return "getEnv fail! err=" + t;
        }
    }

    private static final boolean vendorIsIBM() {
        return System.getProperty("java.vendor").toUpperCase().indexOf("IBM") >= 0;
    }

    private static final boolean isJDKGreaterThan14() {
        try {
            Class.forName("java.lang.StackTraceElement");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static final String getJDKVersion0() {
        String jdkVer = "1.0";
        try {
            Class.forName("java.lang.Void");
            jdkVer = "1.1";
    
            Class.forName("java.lang.ThreadLocal");
            jdkVer = "1.2";
    
            Class.forName("java.lang.StrictMath");
            jdkVer = "1.3";
    
            Class.forName("java.lang.StackTraceElement");
            jdkVer = "1.4";
            
            Class.forName("java.util.concurrent.locks.ReentrantLock") ;
            jdkVer = "5.0";
        } catch (ClassNotFoundException e) {
        } catch (Throwable t) {
        }
        return jdkVer;
    }

    /**
     * 获取该JVM的Classpath信息.
     */
    public static String getClasspaths() {
        StringBuffer sb = new StringBuffer(256);
        sb.append("javahome=");
        sb.append(System.getProperty("java.home"));
        sb.append(";workdir=");
        sb.append(System.getProperty("user.dir"));
        sb.append(";classpath=");
        sb.append(System.getProperty("java.class.path"));
        return sb.toString();
    }

    /**
     * 获取该JVM的内存使用信息.
     */
    public static String getVMMemeoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        StringBuffer sb = new StringBuffer(64);
        sb.append("user.dir=").append(System.getProperty("user.dir")).append(';');
        sb.append("java.home=").append(System.getProperty("java.home")).append(';');
        sb.append("java.version=").append(System.getProperty("java.version")).append(';');
        sb.append("java.vendor=").append(System.getProperty("java.vendor")).append(';');
        sb.append("java.vm.name=").append(System.getProperty("java.vm.name")).append(';');
        sb.append("[Memory]: free=");
        sb.append(runtime.freeMemory() / 1048576).append("MB, total=");
        sb.append(runtime.totalMemory() / 1048576).append("MB, max=");
        sb.append(runtime.maxMemory() / 1048576).append("MB");
        return sb.toString();
    }

    private JRTInfo() {
    }

}
