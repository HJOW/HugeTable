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
package hjow.hgtable.ui.jscript;

import hjow.hgtable.jscript.JScriptObject;
import hjow.hgtable.util.DataUtil;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Window;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JPanel;


/**
 * <p>JSON 형식 텍스트를 해석해 Swing 이용해 대화 상자를 구성하는 데 쓰이는 클래스입니다.</p>
 * 미완성
 * 
 * @author HJOW
 *
 */
public class SwingDialog extends JDialog implements JScriptObject
{
	private static final long serialVersionUID = -7852865673362174916L;
	protected Map<String, Component> addedComponents = new Hashtable<String, Component>();

	/**
	 * <p>이 생성자 사용을 권장하지 않습니다.</p>
	 * 
	 */
	protected SwingDialog()
	{
		
	}
	
	/**
	 * <p>JSON 형식 텍스트로부터 대화 상자 객체를 만듭니다.</p>
	 * 
	 * @param jsonText : JSON 형식 텍스트
	 * @param window : 상위 대화 상자 혹은 창 객체
	 */
	public SwingDialog(String jsonText, Window window) throws Exception
	{
		super(window);
		initDialog(jsonText);
	}
	
	/**
	 * <p>JSON 형식 텍스트로부터 대화 상자 객체를 만듭니다.</p>
	 * 
	 * @param jsonText : JSON 형식 텍스트
	 */
	public SwingDialog(String jsonText) throws Exception
	{
		super();
		initDialog(jsonText);
	}
	
	/**
	 * <p>컴포넌트들을 초기화합니다.</p>
	 * 
	 * @param jsonText : JSON 형식 텍스트
	 */
	protected void initDialog(String jsonText) throws Exception
	{
		this.setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel();
		addedComponents.put("pn_main", mainPanel);
		this.add(mainPanel, BorderLayout.CENTER);
		
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		com.google.gson.JsonElement rootElement = parser.parse(jsonText);
		
		com.google.gson.JsonObject rootObject = rootElement.getAsJsonObject();
		this.setTitle(rootObject.get("name").getAsString());
		int w, h;
		
		w = rootObject.get("width").getAsInt();
		h = rootObject.get("height").getAsInt();
		
		this.setSize(w, h);
		
		com.google.gson.JsonElement mainElement = rootObject.get("components").getAsJsonObject();
		com.google.gson.JsonArray mainArray = mainElement.getAsJsonArray();
		
		for(int i=0; i<mainArray.size(); i++)
		{
			mainPanel.add((Component) process(mainArray.get(i).getAsJsonObject()));
		}
	}
	
	protected Object process(com.google.gson.JsonObject obj) throws Exception
	{
		com.google.gson.JsonElement typeElement = obj.get("type");
		com.google.gson.JsonElement nameElement = obj.get("name");
		
		// TODO 미완성
		
		String type = typeElement.getAsString();
		
		if(type.startsWith("pn") || type.startsWith("PN"))
		{
			JPanel panel = new JPanel();
			String layoutText = null;
			
			if(obj.get("layout").isJsonObject())
			{
				layoutText = obj.get("layout").getAsJsonObject().get("name").getAsString();
				panel.setLayout((LayoutManager) process(obj.get("layout").getAsJsonObject()));
			}
			else
			{
				layoutText = obj.get("layout").getAsString();
				if(layoutText.equalsIgnoreCase("borderlayout"))
				{
					panel.setLayout(new BorderLayout());
				}
				else if(layoutText.equalsIgnoreCase("flowlayout"))
				{
					panel.setLayout(new FlowLayout());
				}
				else if(layoutText.equalsIgnoreCase("gridlayout"))
				{
					int rowCount, colCount;
					rowCount = obj.get("row").getAsInt();
					colCount = obj.get("col").getAsInt();
					panel.setLayout(new GridLayout(rowCount, colCount));
				}
				else if(layoutText.equalsIgnoreCase("null"))
				{
					panel.setLayout(null);
				}
				else throw new Exception("Invalid layout type of : " + layoutText);
			}
			
			com.google.gson.JsonArray targetArray = obj.get("components").getAsJsonArray();
			for(int i=0; i<targetArray.size(); i++)
			{
				if(DataUtil.isNotEmpty(layoutText) && layoutText.equalsIgnoreCase("borderlayout")) panel.add((Component) process(targetArray.get(i).getAsJsonObject())
						, targetArray.get(i).getAsJsonObject().get("borderlayout_location").getAsInt());
				else panel.add((Component) process(targetArray.get(i).getAsJsonObject()));
			}
			
			addedComponents.put(nameElement.getAsString(), panel);
			return panel;
		}
		else if(type.equalsIgnoreCase("layout"))
		{
			
		}
		
		return null;
	}
	
	public Component getComponent(String name)
	{
		return addedComponents.get(name);
	}

	public Map<String, Component> getAddedComponents()
	{
		return addedComponents;
	}

	public void setAddedComponents(Map<String, Component> addedComponents)
	{
		this.addedComponents = addedComponents;
	}

	@Override
	public void noMoreUse()
	{
		addedComponents.clear();
	}

	@Override
	public boolean isAlive()
	{
		return true;
	}

	@Override
	public String help()
	{
		return null;
	}
}
