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

import com.google.gson.JsonObject;

import hjow.hgtable.Manager;
import hjow.hgtable.tableset.Column;
import hjow.hgtable.tableset.DefaultTableSet;
import hjow.hgtable.tableset.Record;
import hjow.hgtable.tableset.TableSet;

/**
 * <p>JSON 을 다루는 데 필요한 정적 메소드들이 정의된 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class JSONUtil
{	
	private static void addColumnFrom(Object obj, Record record)
	{
		if(obj instanceof com.google.gson.JsonObject)
		{
			String name = ((JsonObject) obj).get("name").getAsString();
			String data = ((JsonObject) obj).get("data").getAsString();
			String type = ((JsonObject) obj).get("type").getAsString();
			
			record.getColumnName().add(name);
			record.getDatas().add(data);
			
			try
			{
				record.getTypes().add(new Integer(Integer.parseInt(type)));
			}
			catch(NumberFormatException e)
			{
				if(type.equalsIgnoreCase("BLANK"))
				{
					record.getTypes().add(new Integer(Column.TYPE_BLANK));
				}
				else if(type.equalsIgnoreCase("BOOLEAN"))
				{
					record.getTypes().add(new Integer(Column.TYPE_BOOLEAN));
				}
				else if(type.equalsIgnoreCase("DATE"))
				{
					record.getTypes().add(new Integer(Column.TYPE_DATE));
				}
				else if(type.equalsIgnoreCase("ERROR"))
				{
					record.getTypes().add(new Integer(Column.TYPE_ERROR));
				}
				else if(type.equalsIgnoreCase("FORMULA"))
				{
					record.getTypes().add(new Integer(Column.TYPE_FORMULA));
				}
				else if(type.equalsIgnoreCase("NUMERIC"))
				{
					record.getTypes().add(new Integer(Column.TYPE_NUMERIC));
				}
				else if(type.equalsIgnoreCase("INTEGER"))
				{
					record.getTypes().add(new Integer(Column.TYPE_INTEGER));
				}
				else if(type.equalsIgnoreCase("FLOAT"))
				{
					record.getTypes().add(new Integer(Column.TYPE_FLOAT));
				}
				else if(type.equalsIgnoreCase("STRING"))
				{
					record.getTypes().add(new Integer(Column.TYPE_STRING));
				}
				else
				{
					record.getTypes().add(new Integer(Column.TYPE_STRING));
				}
			}
		}
	}
	/**
	 * <p>JSON 형식의 텍스트로부터 레코드 객체를 얻습니다. 텍스트 Gson 에서 제공하는 JsonArray 타입의 객체도 사용할 수 있습니다.</p>
	 * 
	 * @param obj : JSON 형식 텍스트
	 * @return 레코드 객체
	 */
	public static Record toRecord(Object obj)
	{
		Record record = new Record();
		if(obj instanceof com.google.gson.JsonArray)
		{
			com.google.gson.JsonObject insideObject;
			
			for(int i=0; i<((com.google.gson.JsonArray) obj).size(); i++)
			{
				insideObject = ((com.google.gson.JsonArray) obj).get(i).getAsJsonObject();
				
				addColumnFrom(insideObject, record);
			}
		}
		else
		{
			return toRecordOf(String.valueOf(obj));
		}
		return record;
	}
	private static Record toRecordOf(String json)
	{
		Record record = new Record();
		
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		com.google.gson.JsonElement rootElement = parser.parse(json);
				
		com.google.gson.JsonArray rootArray = rootElement.getAsJsonArray();
		com.google.gson.JsonObject insideObject;
		
		for(int i=0; i<rootArray.size(); i++)
		{
			insideObject = rootArray.get(i).getAsJsonObject();
			
			addColumnFrom(insideObject, record);
		}
		
		return record;
	}
	/**
	 * <p>JSON 형식의 텍스트로부터 테이블 셋 객체를 얻습니다.</p>
	 * 
	 * @param json : JSON 형식 텍스트
	 * @return 테이블 셋 객체
	 * @throws InvalidInputException : 타입 문제
	 */
	public static TableSet toTableSet(String json) throws InvalidInputException
	{
		TableSet tableSet = new DefaultTableSet();
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		com.google.gson.JsonElement rootElement = parser.parse(json);
				
		com.google.gson.JsonObject rootObject = rootElement.getAsJsonObject();
		
		try
		{
			if(! rootObject.get("type").getAsString().equalsIgnoreCase("TABLE"))
			{
				throw new InvalidInputException(Manager.applyStringTable("JSON type is not a table"));
			}
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		
		tableSet.setName(rootObject.get("name").getAsString());
		
		com.google.gson.JsonArray recordData = rootObject.get("records").getAsJsonArray();
		for(int i=0; i<recordData.size(); i++)
		{
			tableSet.addData(toRecord(recordData.get(i).getAsJsonArray()));
		}
		
		return tableSet;
	}
}
