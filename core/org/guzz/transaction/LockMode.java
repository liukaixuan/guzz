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
package org.guzz.transaction;

/**
 *
 * row lock mode. see hibernate's LockMode for detail.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class LockMode {

	private final int level ;

	private final String name ;

	protected LockMode(String name, int level){
		this.name = name ;
		this.level = level ;
	}

	public static final LockMode NONE = new LockMode("NONE", 0) ;

	public static final LockMode READ = new LockMode("READ", 5) ;

	public static final LockMode UPGRADE = new LockMode("UPGRADE", 10) ;

	public static final LockMode UPGRADE_NOWAIT = new LockMode("UPGRADE_NOWAIT", 10) ;

	public int getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

}
