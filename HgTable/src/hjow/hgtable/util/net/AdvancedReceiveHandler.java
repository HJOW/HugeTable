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
package hjow.hgtable.util.net;

import java.util.Map;

import hjow.hgtable.jscript.JScriptObject;

/**
 * <p>소켓 통신의 이벤트 처리를 위한 인터페이스입니다. 문자열뿐만 아닌, 객체 자체를 수신받을 때 사용됩니다.</p>
 * 
 * @author HJOW
 *
 */
public interface AdvancedReceiveHandler extends JScriptObject
{
	/**
	 * <p>메시지 수신 시 호출되는 메소드입니다.</p>
	 * 
	 * @param message : 받은 메시지
	 * @param anotherInfo : 기타 정보 (포트 번호, 상대방 IP 주소 등)
	 * @exception Throwable : 수신 후 동작에 발생한 예외
	 */
	public void receive(Object message, Map<String, Object> anotherInfo) throws Throwable;
}
