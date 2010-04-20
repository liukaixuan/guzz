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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.guzz.lang.NullValue;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Table {

	/**
	 * (Optional) The name of the table. Defaults to the {@link javax.persistence.Table}'s name.
	 * <p/>
	 * For simpifying annotation when you don't want to write {@link javax.persistence.Table}.
	 */
	String name() default "";
	
	/**
	 * (Optional) The database group to store the table.
	 */
	String dbGroup() default "" ;
	
	/**
	 * (Optional) The policy to split the table.
	 */
	Class shadow() default NullValue.class ;

	/**
	 * (Optional) dynamic update? Default false.
	 */
	boolean dynamicUpdate() default false ;
}
