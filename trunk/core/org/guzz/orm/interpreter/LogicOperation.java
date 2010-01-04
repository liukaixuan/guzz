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
package org.guzz.orm.interpreter;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class LogicOperation{
	
	private String symbol ;
	
	public LogicOperation(String symbol){
		this.symbol = symbol ;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public static final LogicOperation EQUAL = new LogicOperation("==") ;
	public static final LogicOperation EQUAL_IGNORE_CASE = new LogicOperation("=~=") ;
	public static final LogicOperation BIGGER = new LogicOperation(">") ;
	public static final LogicOperation BIGGER_OR_EQUAL = new LogicOperation(">=") ;
	public static final LogicOperation SMALLER = new LogicOperation("<") ;
	public static final LogicOperation SMALLER_OR_EQUAL = new LogicOperation("<=") ;
	public static final LogicOperation LIKE_CASE_SENSTIVE = new LogicOperation("~=") ;
	public static final LogicOperation LIKE_IGNORE_CASE = new LogicOperation("~~") ;
	public static final LogicOperation NOT_EQUAL = new LogicOperation("!=") ;
		
}


