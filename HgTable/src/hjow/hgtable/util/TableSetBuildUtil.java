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
import java.util.Vector;

import hjow.hgtable.dao.Dao;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.tableset.TableSetBuilder;
import hjow.hgtable.tableset.TableSetDownloader;
import hjow.hgtable.tableset.TableSetTreator;
import hjow.hgtable.tableset.TableSetUploader;
import hjow.hgtable.tableset.TableSetWriter;
import hjow.hgtable.ui.ProgressEvent;

/**
 * <p>테이블 셋을 불러오거나 저장하는 데 사용되는 여러 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class TableSetBuildUtil
{
	protected static List<TableSetBuilder> tableSetBuilder = new Vector<TableSetBuilder>();
	protected static List<TableSetWriter> tableSetWriter = new Vector<TableSetWriter>();
	protected static List<TableSetUploader> tableSetUploader = new Vector<TableSetUploader>();
	protected static List<TableSetDownloader> tableSetDownloader = new Vector<TableSetDownloader>();
	
	static
	{
		init();
	}
	
	/**
	 * <p>테이블 셋 불러오기 및 저장 도구들을 불러옵니다.</p>
	 */
	public static void init()
	{
		tableSetBuilder.clear();
		tableSetWriter.clear();
		tableSetUploader.clear();
		tableSetDownloader.clear();
		tableSetBuilder.addAll(LicenseUtil.builders());
		tableSetWriter.addAll(LicenseUtil.writers());
		tableSetUploader.addAll(LicenseUtil.uploader());
		tableSetDownloader.addAll(LicenseUtil.downloader());
		
		// XLSXUtil
		tableSetBuilder.add(new TableSetBuilder()
		{
			@Override
			public List<TableSet> toTableSet(File file, ProgressEvent event)
			{
				return XLSXUtil.toTableSets(file);
			}
			
			@Override
			public String getName()
			{
				return "Spreadsheet Basic";
			}
			
			@Override
			public FileFilter availables()
			{
				return new FileFilter()
				{
					@Override
					public boolean accept(File pathname)
					{
						String name = pathname.getAbsolutePath();
						return name.endsWith(".xls") || name.endsWith(".xlsx") || name.endsWith(".XLS") || name.endsWith(".XLSX");
					}
				};
			}
		});
		
		// JSONUtil
		tableSetBuilder.add(new TableSetBuilder()
		{
			@Override
			public List<TableSet> toTableSet(File file, ProgressEvent event)
			{
				List<TableSet> list = new Vector<TableSet>();
				list.add(JSONUtil.toTableSet(StreamUtil.readText(file, "UTF-8")));
				return list;
			}
			
			@Override
			public String getName()
			{
				return "JSON";
			}
			
			@Override
			public FileFilter availables()
			{
				return new FileFilter()
				{
					@Override
					public boolean accept(File pathname)
					{
						String name = pathname.getAbsolutePath();
						return name.endsWith(".json") || name.endsWith(".JSON") || name.endsWith(".txt") || name.endsWith(".TXT");
					}
				};
			}
		});
		
		// HGFUtil
		tableSetBuilder.add(new TableSetBuilder()
		{
			@Override
			public List<TableSet> toTableSet(File file, ProgressEvent event)
			{
				List<TableSet> list = new Vector<TableSet>();
				list.add(DataUtil.toTableSet(StreamUtil.readText(file, "UTF-8")));
				return list;
			}
			
			@Override
			public String getName()
			{
				return "HGF";
			}
			
			@Override
			public FileFilter availables()
			{
				return new FileFilter()
				{
					@Override
					public boolean accept(File pathname)
					{
						String name = pathname.getAbsolutePath();
						return name.endsWith(".hgf") || name.endsWith(".HGF") || name.endsWith(".txt") || name.endsWith(".TXT");
					}
				};
			}
		});
		
		List<String> uploaderClassNames = new Vector<String>();
		uploaderClassNames.add("hjow.hgtable.streaming.XLSXStreamingUploader");
		for(String cn : uploaderClassNames)
		{
			try
			{
				@SuppressWarnings("unchecked")
				Class<? extends TableSetUploader> xlsxUploader = (Class<? extends TableSetUploader>) Class.forName(cn);
				tableSetUploader.add(xlsxUploader.newInstance());
			}
			catch(Throwable t)
			{
				
			}
		}
	}
	
	/**
	 * <p>테이블 셋 도구를 등록합니다.</p>
	 * 
	 * @param tool : 테이블 셋 도구
	 */
	public static void register(TableSetTreator tool)
	{
		if(tool instanceof TableSetBuilder)
		{
			tableSetBuilder.add((TableSetBuilder) tool);
		}
		else if(tool instanceof TableSetWriter)
		{
			tableSetWriter.add((TableSetWriter) tool);
		}
		else if(tool instanceof TableSetDownloader)
		{
			tableSetDownloader.add((TableSetDownloader) tool);
		}
		else if(tool instanceof TableSetUploader)
		{
			tableSetUploader.add((TableSetUploader) tool);
		}
	}
	
	/**
	 * <p>사용 준비가 된 테이블 셋 불러오기 도구 이름들을 리스트로 반환합니다.</p>
	 * 
	 * @return 테이블 셋 불러오기 도구 이름 리스트
	 */
	public static List<String> getBuilderNames()
	{
		List<String> list = new Vector<String>();
		for(TableSetBuilder builders : tableSetBuilder)
		{
			list.add(builders.getName());
		}
		return list;
	}
	
	/**
	 * <p>사용 준비가 된 테이블 셋 저장 도구 이름들을 리스트로 반환합니다.</p>
	 * 
	 * @return 테이블 셋 저장 도구 이름 리스트
	 */
	public static List<String> getWriterNames()
	{
		List<String> list = new Vector<String>();
		for(TableSetWriter writers : tableSetWriter)
		{
			list.add(writers.getName());
		}
		return list;
	}
	
	/**
	 * <p>사용 준비가 된 테이블 셋 다운로드 도구 이름들을 리스트로 반환합니다.</p>
	 * 
	 * @return 테이블 셋 다운로드 도구 이름 리스트
	 */
	public static List<String> getDownloaderNames()
	{
		List<String> list = new Vector<String>();
		for(TableSetDownloader downloader : tableSetDownloader)
		{
			list.add(downloader.getName());
		}
		return list;
	}
	
	/**
	 * <p>사용 준비가 된 테이블 셋 업로드 도구 이름들을 리스트로 반환합니다.</p>
	 * 
	 * @return 테이블 셋 업로드 도구 이름 리스트
	 */
	public static List<String> getUploaderNames()
	{
		List<String> list = new Vector<String>();
		for(TableSetUploader uploader : tableSetUploader)
		{
			list.add(uploader.getName());
		}
		return list;
	}
	
	/**
	 * <p>파일로부터 테이블 셋들을 불러옵니다. 준비된 테이블 셋 불러오기 도구(빌더)들 중 사용 가능한 것 하나를 찾아 사용합니다.</p>
	 * 
	 * @param file : 불러올 파일
	 * @return 불러온 테이블 셋 리스트
	 */
	public static List<TableSet> load(File file, ProgressEvent event)
	{
		for(TableSetBuilder builders : tableSetBuilder)
		{
			if(builders.availables().accept(file))
			{
				return builders.toTableSet(file, event);
			}
		}
		throw new NullPointerException("Cannot find available builder for " + file.getAbsolutePath());
	}
	
	/**
	 * <p>특정 이름의 불러오기 도구(빌더)를 선택해 파일을 불러옵니다.</p>
	 * 
	 * @param file : 불러올 파일
	 * @param toolName : 빌더 이름
	 * @param event : 진행 상황 이벤트 (null 가능)
	 * @return 불러온 테이블 셋 리스트
	 */
	public static List<TableSet> load(File file, String toolName, ProgressEvent event)
	{
		for(TableSetBuilder builders : tableSetBuilder)
		{
			if(builders.getName().trim().equals(toolName.trim()))
			{
				return builders.toTableSet(file, event);
			}
		}
		throw new NullPointerException("Cannot find those builder : " + toolName);
	}
	
	/**
	 * <p>특정 이름의 저장 도구를 선택해 테이블 셋을 파일로 저장합니다.</p>
	 * 
	 * @param file : 저장할 파일
	 * @param tableSet : 저장할 테이블 셋
	 * @param toolName : 저장 도구 이름
	 * @param event : 진행 상황 이벤트 (null 가능)
	 */
	public static void save(File file, TableSet tableSet, String toolName, ProgressEvent event)
	{
		for(TableSetWriter writers : tableSetWriter)
		{
			if(writers.getName().trim().equals(toolName.trim()))
			{
				writers.save(file, tableSet, event);
				return;
			}
		}
		throw new NullPointerException("Cannot find those writer : " + toolName);
	}
	
	/**
	 * <p>특정 이름의 다운로드 도구를 선택해 테이블 셋을 데이터 소스로부터 파일로 저장합니다.</p>
	 * 
	 * @param file : 저장할 파일
	 * @param dao : 데이터 소스 접속 DAO
	 * @param query : 조회 스크립트
	 * @param toolName : 다운로드 도구 이름
	 * @param event : 진행 상황 이벤트 (null 가능)
	 */
	public static void download(File file, Dao dao, String query, String toolName, ProgressEvent event)
	{
		for(TableSetDownloader downloader : tableSetDownloader)
		{
			if(downloader.getName().trim().equals(toolName.trim()))
			{
				downloader.download(file, dao, query, event);
				return;
			}
		}
		throw new NullPointerException("Cannot find those downloader : " + toolName);
	}
	
	/**
	 * <p>특정 이름의 업로드 도구를 선택해 테이블 셋을 파일로부터 데이터 소스로 전송합니다.</p>
	 * 
	 * @param file : 저장할 파일
	 * @param dao : 데이터 소스 접속 DAO
	 * @param tableName : 조회 스크립트
	 * @param toolName : 다운로드 도구 이름
	 * @param event : 진행 상황 이벤트 (null 가능)
	 */
	public static void upload(File file, Dao dao, String tableName, String toolName, ProgressEvent event)
	{
		for(TableSetUploader uploader : tableSetUploader)
		{
			if(uploader.getName().trim().equals(toolName.trim()))
			{
				uploader.upload(file, dao, tableName, event);
				return;
			}
		}
		throw new NullPointerException("Cannot find those uploader : " + toolName);
	}
	
	/**
	 * <p>파일로부터 테이블 셋들을 불러와 데이터 소스로 전송합니다. 준비된 테이블 셋 업로드 도구들 중 사용 가능한 것 하나를 찾아 사용합니다.</p>
	 * 
	 * @param file : 불러올 파일
	 * @param dao : 데이터 소스 접속 DAO
	 * @param tableName : 삽입할 테이블 이름
	 * @param event : 진행 상황 이벤트 (null 가능)
	 */
	public static void load(File file, Dao dao, String tableName, ProgressEvent event)
	{
		for(TableSetUploader uploader : tableSetUploader)
		{
			if(uploader.availables().accept(file))
			{
				uploader.upload(file, dao, tableName, event);
				return;
			}
		}
		throw new NullPointerException("Cannot find available uploader for " + file.getAbsolutePath());
	}
}
