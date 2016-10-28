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

package externalModules

import hjow.hgtable._
import hjow.hgtable.tableset._
import hjow.hgtable.util._
import hjow.hgtable.ui._
import hjow.hgtable.ui.swing._
import hjow.hgtable.ui.module._
import hjow.dbtool.common._
import hjow.datasource.common._
import java.util._
import java.awt._
import javax.swing._
import javax.swing.table._
import java.lang.management._
import java.net._
import java.io.File;
import hjow.hgtable.dao.JdbcDao

/**
 * <p>다수의 쿼리, 혹은 테이블에 조건을 걸어 파일 여러 개로 만듭니다.</p>
 * 
 * @author HJOW
 */
class MassiveSave extends ScalaDialogModule
{
  /*
   모듈에 대한 기본 설정들을 지정하는 곳입니다.
   주의 : 모듈 ID는 initializeComponents() 메소드 내에서 지정합니다.
   */
  var moduleName  : String  = trans("Massive Downloader"); // 모듈 이름을 지정합니다.
  var moduleLoc   : Integer = GUIDialogModule.ON_MENU_TOOL;             // 모듈이 어느 메뉴에 위치해야 할 지를 지정합니다.
  var needThreads : Boolean = true;                                     // 쓰레드를 사용하는 모듈인 경우 true 로 지정해야 합니다.
  
  /*
   필요한 컴포넌트들과 데이터에 해당하는 필드를 선언하는 곳입니다. 
   */
  var dialog    : JDialog = null;
  var mainPanel : JPanel = null;
  
  var objList : UIList = null;
  var objListData : java.util.Vector[String] = new Vector[String]();
  
  var msgArea : JTextArea = null;
  
  var progress : JProgressBar = null;
  
  var fileField : JTextField = null;
  var fileChooser : JFileChooser = new JFileChooser();
  
  var cbType : UIComboBox = null;
  
  var btRefresh : JButton = null;
  var btFile : JButton = null;
  var btClose : JButton = null;
  var btSave : JButton = null;
  
  var needWork : Boolean = false;
  
  /**
   * <p>이 메소드에서 컴포넌트들을 초기화합니다. 모듈 내 컴포넌트들을 초기화하기 위해 이 메소드를 재정의합니다.</p>
   * 
   */
  override def initializeComponents() =
  {
    super.initializeComponents();
    
    // 모듈 ID는 고유하게 지정해야 합니다. long 형 정수 타입 (64 bits 길이)으로 지정하시면 됩니다.
    setModuleId(95499119194987224L);
    
    // 기본 대화 상자와 컴포넌트들을 생성합니다.
    dialog = manager.asInstanceOf[GUIManager].newDialog(false).asInstanceOf[JDialog];
    
    var scSize : Dimension = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setSize(600, 420);
    dialog.setLocation((scSize.getWidth()/2 - dialog.getWidth()/2).toInt, (scSize.getHeight()/2 - dialog.getHeight()/2).toInt);
    
    dialog.setLayout(new BorderLayout());
    dialog.setTitle(getName());
    
    dialog.addWindowListener(this);
    
    mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    
    dialog.add(mainPanel, BorderLayout.CENTER);
    
    // 이 곳에 추가 컴포넌트들과 각종 변수들을 생성합니다.
    var upPanel : JPanel = new JPanel();
    var centerPanel : JPanel = new JPanel();
    var downPanel : JPanel = new JPanel();
    
    mainPanel.add(upPanel, BorderLayout.NORTH);
    mainPanel.add(centerPanel, BorderLayout.CENTER);
    mainPanel.add(downPanel, BorderLayout.SOUTH);
    
    centerPanel.setLayout(new BorderLayout());
    
    var splitPane : JSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    centerPanel.add(splitPane, BorderLayout.CENTER);
    
    objList = new UIList();
    objList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    splitPane.setTopComponent(new JScrollPane(objList));
    
    msgArea = new JTextArea();
    msgArea.setEditable(false);
    splitPane.setBottomComponent(new JScrollPane(msgArea));
    
    upPanel.setLayout(new BorderLayout());
    
    progress = new JProgressBar();
    upPanel.add(progress);
    
    downPanel.setLayout(new FlowLayout());
    
    fileField = new JTextField(15);
    
    cbType = new UIComboBox();
    cbType.addItem("XLSX");
    cbType.addItem("JSON");
    cbType.addItem("HGF");
    cbType.addItem("INSERT");
    
    btRefresh = new JButton(trans("Refresh"));
    btFile = new JButton("...");
    btClose = new JButton(trans("Close"));
    btSave = new JButton(trans("Save"));
    
    downPanel.add(btRefresh);
    downPanel.add(fileField);
    downPanel.add(btFile);
    downPanel.add(cbType);
    downPanel.add(btSave);
    downPanel.add(btClose);
    
    addListener(btRefresh);
    addListener(btFile);
    addListener(btClose);
    addListener(btSave);
    
    // 이벤트 리스너를 정리합니다.
    setListeners();
  };
  
  /**
   * <p>이 메소드는 매니저 객체로부터 호출됩니다. 호출된 사유는 additionalData 에 whyCalled 라는 원소에 텍스트로 삽입됩니다.</p>
   * <p>이 메소드를 통해, 사용자가 모듈 바깥에서 어떠한 동작을 했는지의 여부를 알 수 있습니다.</p>
   * @param additionalData : 매니저 객체로부터 받은 정보들
   */
  override def refresh(additionalData : java.util.Map[String, Object]) =
  {
    if(additionalData != null)
    {
      if(DataUtil.parseBoolean(additionalData.get("object")))
      {
        objListData.clear();
        
        var dao = manager.getDao();
        if(dao == null || (! dao.isAlive())) 
        {
          alert(trans("Connection is needed."));
          close();
        }
        if(dao.isInstanceOf[JdbcDao])
        {
          var jdbcDao : JdbcDao = dao.asInstanceOf[JdbcDao];
          var tableList : TableSet = jdbcDao.getDBTool().tableList(false, null);
          var viewList : TableSet = jdbcDao.getDBTool().viewList(false, false, null);
          
          for(i <- 0 to tableList.getRecordCount() - 1)
          {
            objListData.add(String.valueOf(tableList.get(i).getDataOf(0)));
          }
          for(i <- 0 to viewList.getRecordCount() - 1)
          {
            objListData.add(String.valueOf(viewList.get(i).getDataOf(0)));
          }
        }
        else
        {
          alert(trans("RDBMS connection is needed."));
          close();
        }
        
        objList.setListData(objListData);
      }
    }
  };
  
  /**
   * <p>이 메소드는 버튼이 클릭되었을 때 호출됩니다.</p>
   * 
   * @param e : 클릭된 버튼 객체
   */
  override def actionPerformed(e : AbstractButton) =
  {
    if(e == btClose)
    {
      close();
    }
    else if(e == btSave)
    {
      progress.setValue(0);
      
      btSave.setEnabled(false);
      btFile.setEnabled(false);
      btRefresh.setEnabled(false);
      fileField.setEditable(false);
      
      needWork = true;
    }
    else if(e == btRefresh)
    {
      var map : Hashtable[String, Object] = new Hashtable[String, Object];
      map.put("object", "true");
      refresh(map);
    }
    else if(e == btFile)
    {
      fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      var selection = fileChooser.showSaveDialog(getDialog());
      if(selection == JFileChooser.APPROVE_OPTION)
      {
        var selectedFile : File = fileChooser.getSelectedFile();
        fileField.setText(selectedFile.getAbsolutePath());
      }
    }
  };
  
  /**
   * <p>이 메소드는 텍스트 필드에서 엔터 키가 눌렸을 때 호출됩니다.</p>
   * 
   * @param e : 해당 텍스트 필드
   */
  override def actionPerformed(e : JTextField) =
  {
    
  };
  
  /**
   * <p>이 메소드는 쓰레드 내에서 실행됩니다. 이 안에 있는 내용은 별도의 쓰레드에서 반복 실행됩니다. needThreads 가 false 이면 이 메소드는 실행되지 않습니다.</p>
   * <p>needThreads 가 true 라면, 별도의 설정이 없는 경우 100 밀리초 (0.1초)에 한 번씩 실행됩니다.</p>
   * 
   */
  @throws(classOf[Exception])
  override def onThread() =
  {
    if(needWork)
    {
      needWork = false;
      var selecteds : Array[Int] = objList.getSelectedIndices();
      var errCnt : Int = 0;
      var successCnt : Int = 0;
      
      var selectedTypes : String = String.valueOf(cbType.getSelectedItem());
      
      for(i <- selecteds)
      {
        var objName : String = objListData.get(i);
        try
        {
          var query : String = "SELECT * FROM " + objName;
          var path : File = new File(fileField.getText());
          if(! path.exists()) path.mkdirs();
          
          if(! (fileField.getText().endsWith("/") || fileField.getText().endsWith("\\")))
          {
            fileField.setText(fileField.getText() + File.separatorChar);
          }
          var file : File = null;
          
          msgArea.append("\n" + trans("Select") + " : " + query);
          msgArea.setCaretPosition(msgArea.getDocument().getLength());
          
          var tableSet : TableSet = manager.getDao().queryWithoutLog(query, this);
          
          if(selectedTypes.equalsIgnoreCase("XLSX"))
          {
            file = new File(fileField.getText() + objName + ".xlsx");
          }
          else if(selectedTypes.equalsIgnoreCase("JSON"))
          {
            file = new File(fileField.getText() + objName + ".json"); 
          }
          else if(selectedTypes.equalsIgnoreCase("HGF"))
          {
            file = new File(fileField.getText() + objName + ".hgf");
          }
          else if(selectedTypes.equalsIgnoreCase("INSERT"))
          {
            file = new File(fileField.getText() + objName + ".sql");
          }
          
          msgArea.append("\n" + trans("Saving") + " : " + file.getAbsolutePath());
          msgArea.setCaretPosition(msgArea.getDocument().getLength());
          
          if(selectedTypes.equalsIgnoreCase("XLSX"))
          {
            XLSXUtil.save(tableSet, file);
          }
          else if(selectedTypes.equalsIgnoreCase("JSON"))
          {
            StreamUtil.saveFile(file, tableSet.toJSON(true));
          }
          else if(selectedTypes.equalsIgnoreCase("HGF"))
          {
            StreamUtil.saveFile(file, tableSet.toHGF());
          }
          else if(selectedTypes.equalsIgnoreCase("INSERT"))
          {
            StreamUtil.saveFile(file, tableSet.toInsertSQL());
          }
          
          msgArea.append("\n" + trans("Success") + " : " + objName);
          msgArea.setCaretPosition(msgArea.getDocument().getLength());
          successCnt = successCnt + 1;
        }
        catch
        {
          case e : Throwable =>
            {
              msgArea.append("\n" + trans("Fail") + " : " + objName + "\n  <--" + e.getMessage());
              errCnt = errCnt + 1;
            }
        }
        progress.setValue(((i.asInstanceOf[Double] / selecteds.length.asInstanceOf[Double]) * 100.0).asInstanceOf[Int]);
      }
      
      msgArea.append("\n" + trans("Complete"));
      msgArea.append("\n" + trans("Success") + " : " + successCnt);
      msgArea.append("\n" + trans("Fail") + " : " + errCnt);
      msgArea.setCaretPosition(msgArea.getDocument().getLength());
      
      btSave.setEnabled(true);
      btFile.setEnabled(true);
      btRefresh.setEnabled(true);
      fileField.setEditable(true);
      
      progress.setValue(100);
    }
  };
  
  /**
   * <p>이 메소드는 사용자가 이 모듈을 사용하기 위해 대화 상자를 열려고 할 때 호출됩니다.</p>
   * 
   */
  override def open() =
  {
    super.open();
    
    // 사용자가 이 모듈을 사용하려고 대화 상자를 열었을 때 수행해야 할 작업을 이 곳에 작성합니다.
    var map : Hashtable[String, Object] = new Hashtable[String, Object];
    map.put("object", "true");
    refresh(map);
  };
  
  /**
   * <p>이 객체 사용을 중단하기 위해 순환 참조를 끊습니다. 프로그램이 종료될 때 호출됩니다.</p>
   * 
   */
  override def noMoreUse() =
  {
    super.noMoreUse();
    
    // 프로그램이 종료될 때 수행해야 할 작업을 이 곳에 작성합니다.
    
  };
  
  // 아래의 메소드들은 가급적이면 변경하지 않도록 합니다.
  
  /**
   * <p>쓰레드 사용이 필요한 모듈인지 여부를 반환합니다. true 반환 시 쓰레드가 자동 시작됩니다.</p>
   * 
   * @return 쓰레드 사용 필요 여부
   */
  override def needThread() : Boolean =
  {
    return needThreads;
  };
  
  /**
   * <p>모듈 이름을 반환합니다.</p>
   * 
   * @return 모듈 이름
   */
  override def getName() : String =
  {
    return moduleName;
  };
  
  /**
   * <p>이 대화 상자형 모듈이 속할 메뉴 코드를 반환합니다.</p>
   * 
   * @return 소속 메뉴 코드
   */
  override def getMenuLocation() : Int =
  {
    return moduleLoc;
  };
  
  /**
   * <p>대화 상자 객체를 반환합니다.</p>
   * 
   * @return 대화 상자 객체
   */
  override def getDialog() : Dialog =
  {
    return dialog;
  };
}