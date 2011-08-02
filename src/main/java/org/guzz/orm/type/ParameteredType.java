/*
 * Copyright 2008-2010 the original author or authors.
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
package org.guzz.orm.type;

/**
 * 
 * Allow the {@link SQLDataType} to accept a parameter.
 * <p>
 * The parameter is passed with the type name after "|" seperator. <br>eg:<br>
 * enum.ordinal|org.guzz.MyEnumType sets the mapped dataType to enum(ordinal) and pass the parameter "org.guzz.MyEnumType" telling the enum's class name.
 * </p>
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface ParameteredType {
	
	/**
	 * Inject the parameter.
	 */
	public void setParameter(String param) ;

}
