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
 * <p>이 객체 사용을 중단하기 위하여 별도의 작업이 필요한 경우 구현하는 인터페이스입니다.</p>
 * 
 * @author HJOW
 *
 */
public interface NeedtoEnd
{
	/**
	 * <p>이 객체 사용을 중단하기 위해 순환 참조를 끊습니다.</p>
	 * 
	 */
	public void noMoreUse();
	
	/**
	 * <p>아직 사용 가능한지 여부를 반환합니다.</p>
	 * 
	 * @return 사용 가능 여부
	 */
	public boolean isAlive();
}
