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

import javax.servlet.http.HttpServletResponse;

/**
 * HTTP响应工具类.
 */
public class ResponseUtil {

    /**
     * 给HTTP响应头添加清空缓存标记.
     */
    public static void clearCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "no-store"); //this is necessary to prevent caching on FireFox.
        response.setHeader("Pragma", "no-cache");   // HTTP 1.0
        response.setDateHeader("Expires", -1);
        response.setDateHeader("max-age", 0);
    }
    
    /**
     * 给HTTP响应头中, 设置缓存时间.
     * @param ms 缓存时间(毫秒).
     */
    public static void setCacheExpire(HttpServletResponse response, long ms) {
        long curTime = System.currentTimeMillis();
        response.setDateHeader("Last-Modified", curTime);
        response.setDateHeader("Expires", curTime + ms);
    }

}
