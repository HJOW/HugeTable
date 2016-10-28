package hjow.hgtable;

import java.util.Map;

/**
 * 매개 변수가 있는 "동작" 전달용 인터페이스입니다.
 * 
 * @author HJOW
 *
 */
public interface ParameterRunnable
{
	public void run(Map<String, Object> parameters);
}
