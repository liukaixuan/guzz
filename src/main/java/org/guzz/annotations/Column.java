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
package org.guzz.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.guzz.lang.NullValue;
import org.guzz.pojo.lob.TranBlob;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */

@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Column {
	
	 /**
     * (Optional) The value returned when the stored value in the database is null.  
     * 
     * The value "null" is reserved for java keyword null.
     */
    String nullValue() default "null" ;
    
    /**
     * (Optional) The user-defined loader to fetch the property's value.
     */
    Class loader() default NullValue.class ;
    
    /**
     * (Optional) The data type of the column. <br/>eg:string, int, varchar, {@link TranBlob}
     */
    String type() default "" ;

}
