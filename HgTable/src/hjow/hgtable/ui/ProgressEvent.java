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

/**
 * <p>어떤 작업이 얼마나 진행 중인지 여부를 수집하기 위한 이벤트입니다.</p>
 * 
 * @author HJOW
 *
 */
public interface ProgressEvent
{
	/**
	 * <p>어떤 작업의 진행률이 변경되었을 때(작업이 수행되었거나, 초기화된 경우) 호출됩니다.</p>
	 * 
	 * @param v : 작업률 (100분율, %)
	 */
	public void setValue(int v);
	
	/**
	 * <p>현재 진행중인 작업을 설명할 메시지를 변경합니다.</p>
	 * 
	 * @param message : 메시지
	 */
	public void setText(String message);
}
