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

/**
 * <p>권한 체계 관리를 위한 인터페이스입니다.</p>
 * 
 * @author HJOW
 *
 */
public interface NeedAuthorize
{
	/**
	 * <p>해당 ID와 비밀번호를 토대로 인증 가능 여부를 반환합니다.</p>
	 * 
	 * @param id : 사용자 ID
	 * @param pw : 사용자 비밀번호
	 * @return 인증 여부
	 */
	public boolean isAuthorize(String id, String pw);
	
	/**
	 * <p>해당 스크립트 엔진이 인증 가능한지 여부를 검사합니다.</p>
	 * 
	 * @param runner : 스크립트 엔진
	 * @return 인증 여부
	 */
	public boolean isAuthorize(JScriptRunner runner);
}
