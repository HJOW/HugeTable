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

package hjow.hgtable.util;

import hjow.hgtable.ui.module.defaults.ArgumentData;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * <p>이 클래스에는 매개 변수를 다루는 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class ArgumentUtil
{
	/**
	 * <p>SQL문에 매개 변수를 삽입한 결과를 반환합니다.</p>
	 * 
	 * @param targetScript : 대상 스크립트
	 * @param args : 매개 변수 리스트
	 * @param forms : 형식, ?, :args, #args# 가능
	 * @return 매개 변수가 삽입된 스크립트
	 */
	public static String applyArgs(String targetScript, List<ArgumentData> args, String forms)
	{
		StringBuffer results = new StringBuffer("");
		char[] targetChar = targetScript.toCharArray();
		int argIndex = 0;
		
		if(forms.equals("?"))
		{
			char quotes = ' ';
			for(int i=0; i<targetChar.length; i++)
			{
				if(quotes == ' ')
				{
					if(targetChar[i] == '"')
					{
						quotes = '"';
						results = results.append(String.valueOf(targetChar[i]));
					}
					else if(targetChar[i] == '?')
					{
						if(i == targetChar.length - 1 || targetChar[i+1] == ' ')
						{
							results = results.append(args.get(argIndex));
							argIndex++;
						}
						else
						{
							results = results.append(String.valueOf(targetChar[i]));
						}
					}
					else
					{
						results = results.append(String.valueOf(targetChar[i]));
					}
				}
				else if(quotes == '"')
				{
					if(targetChar[i] == '"')
					{
						quotes = ' ';
						results = results.append(String.valueOf(targetChar[i]));
					}
					else
					{
						results = results.append(String.valueOf(targetChar[i]));
					}
				}
				else if(quotes == '\'')
				{
					if(targetChar[i] == '\'')
					{
						quotes = ' ';
						results = results.append(String.valueOf(targetChar[i]));
					}
					else
					{
						results = results.append(String.valueOf(targetChar[i]));
					}
				}
			}
		}
		else if(forms.equals(":args"))
		{
			String resultScript = targetScript;
			String argData;
			for(int i=0; i<args.size(); i++)
			{
				argData = "'" + DataUtil.castQuote(false, args.get(i).getData()) + "'";
				resultScript = resultScript.replaceAll(":" + args.get(i).getName(), argData);
			}
			return resultScript;
		}
		else if(forms.equals("#args#"))
		{
			String resultScript = targetScript;
			String argData;
			for(int i=0; i<args.size(); i++)
			{
				argData = "\"" + DataUtil.castQuote(true, args.get(i).getData()) + "\"";
				resultScript = resultScript.replaceAll("#" + args.get(i).getName() + "#", argData);
				resultScript = resultScript.replaceAll("$" + args.get(i).getName() + "$", args.get(i).getData());
			}
			return resultScript;
		}
		else return null;
		
		return results.toString();
	}
	
	/**
	 * <p>텍스트로부터 매개 변수들을 받습니다.</p>
	 * 
	 * @param parameter : 텍스트
	 * @return 매개 변수 리스트
	 */
	public static Map<String, String> getArguments(String parameter)
	{
		return getArguments(parameter, new Vector<String>());
	}
	
	/**
	 * <p>텍스트로부터 매개 변수들을 받습니다.</p>
	 * 
	 * @param ignores : 무시할 매개 변수 목록
	 * @param parameters : 매개 변수들
	 * @return 매개 변수 리스트
	 */
	public static Map<String, String> getArguments(List<String> parameters, List<String> ignores)
	{
		Map<String, String> argMap = new Hashtable<String, String>();
		
		String savedKey = null;
		for(int i=0; i<parameters.size(); i++)
		{
			if(parameters.get(i).startsWith("--"))
			{
				if(savedKey != null)
				{
					argMap.put(savedKey, "true");
				}
				
				savedKey = parameters.get(i).substring(2);
				
				if(ignores != null && ignores.contains(savedKey))
				{
					savedKey = null;
				}
			}
			else
			{
				if(savedKey != null)
				{
					argMap.put(savedKey, parameters.get(i));
					savedKey = null;
				}
			}
		}
		if(savedKey != null)
		{
			if(ignores != null && ignores.contains(savedKey))
			{
				savedKey = null;
			}
			else
			{
				argMap.put(savedKey, "true");
			}
		}
		
		return argMap;
	}
	
	/**
	 * <p>텍스트로부터 매개 변수들을 받습니다.</p>
	 * <p>[검증 필요]</p>
	 * 
	 * @param ignores : 무시할 매개 변수 목록
	 * @param parameter : 텍스트
	 * @return 매개 변수 리스트
	 */
	public static Map<String, String> getArguments(String parameter, List<String> ignores)
	{	
		Map<String, String> argMap = new Hashtable<String, String>();
		
		// TODO : 검증 필요
		
		List<TextBlock> blocks = DataUtil.getBlocks(' ', parameter);
		
		String savedKey = null;
		for(int i=0; i<blocks.size(); i++)
		{
			if(blocks.get(i).getContents().startsWith("--"))
			{
				if(savedKey != null)
				{
					argMap.put(savedKey, "true");
				}
				
				savedKey = blocks.get(i).getContents().substring(2);
				
				if(ignores != null && ignores.contains(savedKey))
				{
					savedKey = null;
				}
			}
			else
			{
				if(savedKey != null)
				{
					argMap.put(savedKey, blocks.get(i).getContents());
					savedKey = null;
				}
			}
		}
		if(savedKey != null)
		{
			if(ignores != null && ignores.contains(savedKey))
			{
				savedKey = null;
			}
			else
			{
				argMap.put(savedKey, "true");
			}
		}
		
		return argMap;
	}
}
