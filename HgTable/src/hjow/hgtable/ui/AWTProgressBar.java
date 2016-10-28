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
package hjow.hgtable.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.TextField;

/**
 * <p>AWT 기반 상태바 컴포넌트입니다. AWT와 호환됩니다.</p>
 * 
 * @author HJOW
 *
 */
public class AWTProgressBar extends TextField
{
	private static final long serialVersionUID = -5734354961048376337L;
	protected int nowState = 0;
	protected int maxValue = 100;
	protected int minValue = 0;
	protected String message = "";
	protected boolean useGradient = true;
	protected boolean showMsg = true;

	/**
	 * <p>상태바 객체를 만듭니다. 최대값은 100, 최소값은 0으로 기본값이 설정됩니다.</p>
	 * 
	 */
	public AWTProgressBar()
	{
		super(20);
		setEditable(false);
		setForeground(Color.GREEN);
	}
	
	@Override
	public void setText(String text)
	{
		this.message = text;
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		
		Color c;
		
		g.setColor(getBackground());
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if(useGradient)
		{
			c = new Color((int)(getForeground().getRed() / 1.3), (int)(getForeground().getGreen() / 1.3), (int)(getForeground().getBlue() / 1.3));
			g.setColor(c);
			g.fillRect(getX(), getY(), ((int) Math.round((((double) (nowState - minValue)) / ((double) (maxValue - minValue))) * ((double) getWidth()))), 1);
//			Main.println("1:" + c);
			
			c = new Color((int)(getForeground().getRed() / 1.1), (int)(getForeground().getGreen() / 1.1), (int)(getForeground().getBlue() / 1.1));
			g.setColor(c);
			g.fillRect(getX(), getY() + 1, ((int) Math.round((((double) (nowState - minValue)) / ((double) (maxValue - minValue))) * ((double) getWidth()))), 2);
//			Main.println("2:" + c);
			
			c = getForeground();
			g.setColor(c);
			g.fillRect(getX(), getY() + 3, ((int) Math.round((((double) (nowState - minValue)) / ((double) (maxValue - minValue))) * ((double) getWidth()))), getHeight() - 6);
//			Main.println("3:" + c);
			
			c = new Color((int)(getForeground().getRed() / 1.1), (int)(getForeground().getGreen() / 1.1), (int)(getForeground().getBlue() / 1.1));
			g.setColor(c);
			g.fillRect(getX(), getY() + getHeight() - 3, ((int) Math.round((((double) (nowState - minValue)) / ((double) (maxValue - minValue))) * ((double) getWidth()))), 2);
//			Main.println("4:" + c);
			
			c = new Color((int)(getForeground().getRed() / 1.3), (int)(getForeground().getGreen() / 1.3), (int)(getForeground().getBlue() / 1.3));
			g.setColor(c);
			g.fillRect(getX(), getY() + getHeight() - 1, ((int) Math.round((((double) (nowState - minValue)) / ((double) (maxValue - minValue))) * ((double) getWidth()))), 1);
//			Main.println("5:" + c);
		}
		else
		{
			g.setColor(getForeground());
			g.fillRect(getX(), getY(), ((int) Math.round((((double) (nowState - minValue)) / ((double) (maxValue - minValue))) * ((double) getWidth()))), getHeight());
		}
		
		if((! (message == null || message.trim().equals(""))) && showMsg)
		{
			int red, green, blue;
			Color fore = getForeground();
			Color back = getBackground();
			
			if(fore.getRed() > back.getRed()) red = 255 - fore.getRed();
			else red = 255 - back.getRed();
			
			if(fore.getGreen() > back.getGreen()) green = 255 - fore.getGreen();
			else green = 255 - back.getGreen();
			
			if(fore.getBlue() > back.getBlue()) blue = 255 - fore.getBlue();
			else blue = 255 - back.getBlue();
			
			g.setColor(new Color(red, green, blue));
			g.setFont(new Font(getFont().getFamily(), getFont().getStyle(), 10));
			g.drawString(message, 0, this.getHeight() / 2 + 5);
		}
	}
	
	
	public void setValue(int n)
	{
		nowState = n;
		repaint();
	}

	public int getNowState()
	{
		return nowState;
	}

	public void setNowState(int nowState)
	{
		setValue(nowState);
	}

	public int getMaxValue()
	{
		return maxValue;
	}

	public void setMaxValue(int maxValue)
	{
		this.maxValue = maxValue;
	}

	public int getMinValue()
	{
		return minValue;
	}

	public void setMinValue(int minValue)
	{
		this.minValue = minValue;
	}

	public boolean isUseGradient()
	{
		return useGradient;
	}

	public void setUseGradient(boolean useGradient)
	{
		this.useGradient = useGradient;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public boolean isShowMsg()
	{
		return showMsg;
	}

	public void setShowMsg(boolean showMsg)
	{
		this.showMsg = showMsg;
	}
}
