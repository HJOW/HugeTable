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

package hjow.hgtable.stringtable;

public class InvalidPrefixException extends Exception
{
	private static final long serialVersionUID = -681152711613608573L;
	private int prefixProblemLine = 0;
	private String prefixProblemLineContents = "";
	public InvalidPrefixException()
	{
		super();
	}
	public InvalidPrefixException(String msg, int lineIndex, String lineContents)
	{
		super(msg);
		this.prefixProblemLine = lineIndex;
		this.prefixProblemLineContents = new String(lineContents);
	}
	public int getPrefixProblemLine()
	{
		return prefixProblemLine;
	}
	public void setPrefixProblemLine(int prefixProblemLine)
	{
		this.prefixProblemLine = prefixProblemLine;
	}
	public String getPrefixProblemLineContents()
	{
		return prefixProblemLineContents;
	}
	public void setPrefixProblemLineContents(String prefixProblemLineContents)
	{
		this.prefixProblemLineContents = prefixProblemLineContents;
	}
}
