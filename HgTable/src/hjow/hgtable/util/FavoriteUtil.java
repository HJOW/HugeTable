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

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import hjow.hgtable.Manager;
import hjow.hgtable.favorites.AbstractFavorites;
import hjow.hgtable.favorites.Favorites;
import hjow.hgtable.favorites.MultipleFavorites;

/**
 * <p>자주 사용하는 스크립트 목록을 처리하기 위한 여러 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class FavoriteUtil
{
	/**
	 * <p>자주 사용하는 스크립트 목록을 읽습니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @return 자주 사용하는 스크립트 목록
	 */
	public static List<AbstractFavorites> readFavorites(Manager manager)
	{
		List<AbstractFavorites> list = new Vector<AbstractFavorites>();
		File file = new File(Manager.getOption("config_path") + "favorites" + Manager.getOption("file_separator"));
		if(file.exists())
		{
			File[] favList = file.listFiles(new FileFilter()
			{
				@Override
				public boolean accept(File pathname)
				{
					String name = pathname.getAbsolutePath();
					return name.endsWith(".fav") || name.endsWith(".FAV") || name.endsWith(".Fav");
				}
			});
			for(File f : favList)
			{
				try
				{
					String reads = StreamUtil.readText(f, "UTF-8");
					AbstractFavorites fav = instantiation(reads);
					if(fav == null) continue;
					list.add(fav);
				}
				catch(Throwable t)
				{
					manager.logError(t, Manager.applyStringTable("On loading favorites"));
				}
			}
		}
		
		return list;
	}
	
	/**
	 * <p>읽은 문자열을 번역해 자주 사용하는 스크립트 객체로 만듭니다.</p>
	 * 
	 * @param serialized : 읽은 문자열
	 * @return 자주 사용하는 스크립트 객체
	 * @throws Throwable
	 */
	public static AbstractFavorites instantiation(String serialized) throws Throwable
	{
		String type = "Favorites"; // 기본값
		StringTokenizer lineTokenizer = new StringTokenizer(serialized, "\n");
		while(lineTokenizer.hasMoreTokens())
		{
			String line = DataUtil.remove65279(lineTokenizer.nextToken()).trim();
			if(DataUtil.isEmpty(line)) continue;
			if(line.startsWith("#")) continue;
			if(line.startsWith("@"))
			{
				String eliminated = line.substring(new String("!").length()).trim();
				if(eliminated.startsWith("TYPE") || eliminated.startsWith("Type") || eliminated.startsWith("type"))
				{
					type = eliminated.substring(new String("TYPE").length()).trim();
					break;
				}
			}
		}
		
		List<AbstractFavorites> samples = new Vector<AbstractFavorites>();
		samples.add(new Favorites());
		samples.add(new MultipleFavorites());
		for(AbstractFavorites sample : samples)
		{
			try
			{
				if(sample.getType().equalsIgnoreCase(type))	return sample.getClass().getConstructor(String.class).newInstance(serialized);
			}
			catch(NoSuchMethodException e)
			{
				
			}
			catch(Throwable e)
			{
				throw e;
			}
		}
		
		return null;
	}
}
