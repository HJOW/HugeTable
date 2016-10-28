/*
 
 Copyright 2015 HJOW

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
 */

package hjow.hgtable.jscript;

import hjow.hgtable.Manager;
import hjow.hgtable.tableset.Column;

/**
 * <p>타입 코드 도움말 객체입니다.</p>
 * 
 * @author HJOW
 *
 */
public class TypeCodeHelp implements JScriptObject
{
	private static final long serialVersionUID = -7709637760162137147L;
	
	/**
	 * <p>기본 생성자입니다.</p>
	 */
	public TypeCodeHelp()
	{
		
	}

	@Override
	public String help()
	{
		StringBuffer results = new StringBuffer("");
		results = results.append(Column.TYPE_BLANK + " : " + Manager.applyStringTable("BLANK"));
		results = results.append(Column.TYPE_BOOLEAN + " : " + Manager.applyStringTable("BOOLEAN"));
		results = results.append(Column.TYPE_DATE + " : " + Manager.applyStringTable("DATE"));
		results = results.append(Column.TYPE_ERROR + " : " + Manager.applyStringTable("ERROR"));
		results = results.append(Column.TYPE_FLOAT + " : " + Manager.applyStringTable("FLOAT"));
		results = results.append(Column.TYPE_FORMULA + " : " + Manager.applyStringTable("FORMULA"));
		results = results.append(Column.TYPE_INTEGER + " : " + Manager.applyStringTable("INTEGER"));
		results = results.append(Column.TYPE_NUMERIC + " : " + Manager.applyStringTable("NUMERIC"));
		results = results.append(Column.TYPE_STRING + " : " + Manager.applyStringTable("STRING"));
		
		return results.toString();
	}
	@Override
	public void noMoreUse()
	{
		
	}
	@Override
	public boolean isAlive()
	{
		return true;
	}
}
