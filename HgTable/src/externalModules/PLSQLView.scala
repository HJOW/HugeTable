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

import javax.swing.border.EtchedBorder

/**
 * <p>함수와 스크립트 관리를 위한 도구입니다.</p>
 * 
 * @author HJOW
 */
class PLSQLView extends ScalaDialogModule
{
  var dialog : JDialog = null;
  var mainPanel : JPanel = null;
  var upPanel : JPanel = null;
  var centerPanel : JPanel = null;
  var downPanel : JPanel = null;
  var leftPanel : JPanel = null;
  var objList : UIList = null;
  var objScroll : JScrollPane = null;
  var searchPanel : JPanel = null;
  var searchFieldPanel : JPanel = null;
  var searchTypePanel : JPanel = null;
  var searchTypeCombo : UIComboBox = null;
  var searchField : JTextField = null;
  var btSearch : JButton = null;
  var scriptArea : LineNumberTextArea = null;
  var scriptControlPanel : JPanel = null;
  var btClose : JButton = null;
  var btRun : JButton = null;
  
  var dbtool : DBTool = null;
  
  /**
   * <p>이 메소드에서 컴포넌트들을 초기화합니다. 모듈 내 컴포넌트들을 초기화하기 위해 이 메소드를 재정의합니다.</p>
   * 
   */
  override def initializeComponents() =
  {
    super.initializeComponents();
    
    setModuleId(1651849876153L);
    
    dialog = manager.asInstanceOf[GUIManager].newDialog(false).asInstanceOf[JDialog];
    
    var scSize : Dimension = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setSize(610, 450);
    dialog.setLocation((scSize.getWidth()/2 - dialog.getWidth()/2).toInt, (scSize.getHeight()/2 - dialog.getHeight()/2).toInt);
    
    dialog.setLayout(new BorderLayout());
    dialog.setTitle(getName());
    
    dialog.addWindowListener(this);
    
    mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    
    dialog.add(mainPanel, BorderLayout.CENTER);
    
    leftPanel = new JPanel();
    upPanel = new JPanel();
    centerPanel = new JPanel();
    downPanel = new JPanel();
    
    mainPanel.add(leftPanel, BorderLayout.WEST);
    mainPanel.add(upPanel, BorderLayout.NORTH);
    mainPanel.add(centerPanel, BorderLayout.CENTER);
    mainPanel.add(downPanel, BorderLayout.SOUTH);
    
    leftPanel.setLayout(new BorderLayout());
    
    objList = new UIList();
    objList.setBorder(new EtchedBorder());
    objList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    objScroll = new JScrollPane(objList);
    leftPanel.add(objScroll, BorderLayout.CENTER);
    
    addListener(objList);
    
    searchPanel = new JPanel();
    leftPanel.add(searchPanel, BorderLayout.NORTH);
    
    searchPanel.setLayout(new BorderLayout());
    
    searchFieldPanel = new JPanel();
    searchTypePanel = new JPanel();
    
    searchPanel.add(searchTypePanel, BorderLayout.NORTH);
    searchPanel.add(searchFieldPanel, BorderLayout.CENTER);
    
    searchFieldPanel.setLayout(new BorderLayout());
    
    searchField = new JTextField(16);
    btSearch = new JButton(trans("Search"));
    searchFieldPanel.add(searchField, BorderLayout.CENTER);
    searchFieldPanel.add(btSearch, BorderLayout.EAST);
    
    addListener(searchField);
    addListener(btSearch);
    
    searchTypePanel.setLayout(new FlowLayout());
    
    var types : Vector[String] = new Vector[String]();
    types.add(trans("All"));
    types.add(trans("Function"));
    types.add(trans("Procedure"));
    
    searchTypeCombo = new UIComboBox(types);
    searchTypePanel.add(searchTypeCombo);
    
    centerPanel.setLayout(new BorderLayout());
    
    scriptArea = new LineNumberTextArea(dialog);
    centerPanel.add(scriptArea, BorderLayout.CENTER);
    
    scriptControlPanel = new JPanel();
    scriptControlPanel.setLayout(new FlowLayout());
    
    btClose = new JButton(trans("Close"));
    btRun = new JButton(trans("Run"));
    
    addListener(btClose);
    addListener(btRun);
    
    scriptControlPanel.add(btClose);
    scriptControlPanel.add(btRun);
    
    centerPanel.add(scriptControlPanel, BorderLayout.SOUTH);
    
    setListeners();
    
    threadGap = 250;
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
  
  /**
   * <p>모듈 이름을 반환합니다.</p>
   * 
   * @return 모듈 이름
   */
  override def getName() : String =
  {
    return trans("PL/SQL");
  };
  
  /**
   * <p>이 대화 상자형 모듈이 속할 메뉴 코드를 반환합니다.</p>
   * 
   * @return 소속 메뉴 코드
   */
  override def getMenuLocation() : Int =
  {
    return GUIDialogModule.ON_MENU_VIEW;
  };
  
  /**
   * <p>이 메소드는 쓰레드 내에서 실행됩니다.</p>
   * 
   */
  @throws(classOf[Exception])
  override def onThread() =
  {
    refresh(new java.util.Hashtable[String, Object]());
  };
  
  /**
   * <p>쓰레드 사용이 필요한 모듈인지 여부를 반환합니다. true 반환 시 쓰레드가 자동 시작됩니다.</p>
   * 
   * @return 쓰레드 사용 필요 여부
   */
  override def needThread() : Boolean =
  {
    return true;
  };
  
  /**
   * <p>이 객체 사용을 중단하기 위해 순환 참조를 끊습니다.</p>
   * 
   */
  override def noMoreUse() =
  {
    super.noMoreUse();
    if(dbtool != null) dbtool.close();
    scriptArea.noMoreUse();
  };
  
  /**
   * <p>이 메소드는 매니저 객체로부터 호출됩니다. 호출된 사유는 additionalData 에 whyCalled 라는 원소에 텍스트로 삽입됩니다.</p>
   * @param additionalData : 매니저 객체로부터 받은 정보들
   */
  override def refresh(additionalData : java.util.Map[String, Object]) =
  {
    if(additionalData != null && DataUtil.isNotEmpty(additionalData.get("selected_dao")))
    {
      if(dbtool != null)
      {
        dbtool.close();
        dbtool = null;
      }
      if(manager.getDao.isAlive())
      {
        var tool : DataSourceTool = manager.getDao().getDataSourceTool();
        if(tool.isInstanceOf[DBTool]) dbtool = tool.asInstanceOf[DBTool];
      }
    }
  };
  
  /**
   * <p>이 메소드는 사용자가 이 모듈을 사용하기 위해 대화 상자를 열려고 할 때 호출됩니다.</p>
   * 
   */
  override def open() =
  {
    super.open();
    search();
  };
  
  /**
   * <p>이 메소드는 버튼이 클릭되었을 때 호출됩니다.</p>
   * 
   * @param e : 클릭된 버튼 객체
   */
  override def actionPerformed(e : AbstractButton) =
  {
    if(e == btSearch)
    {
      search();
    }
    else if(e == btClose)
    {
      close();
    }
    else if(e == btRun)
    {
      executeScript();
    }
  };
  
  /**
   * <p>이 메소드는 텍스트 필드에서 엔터 키가 눌렸을 때 호출됩니다.</p>
   * 
   * @param e : 해당 텍스트 필드
   */
  override def actionPerformed(e : JTextField) =
  {
    if(e == searchField)
    {
      search();
    }
  }
  
  /**
   * <p>함수 및 프로시저 목록을 검색합니다.</p>
   */
  def search() =
  {
    var objListData : Vector[String] = new Vector[String]();
    
    var daoRefreshMap = new Hashtable[String, Object]();
    daoRefreshMap.put("selected_dao", "true");
    if(dbtool == null) refresh(daoRefreshMap);
    else if(dbtool.getDao() == null) refresh(daoRefreshMap);
    else if(! dbtool.getDao().isAlive()) refresh(daoRefreshMap);
    else if(dbtool != null)
    {
      var selectedType : String = String.valueOf(searchTypeCombo.getSelectedItem());
      if(selectedType.equals("All") || selectedType.equals(trans("All"))
          || selectedType.equals("Function") || selectedType.equals(trans("Function")))
      {
        var functionList : TableSet = dbtool.getFunctionWithStatus(searchField.getText(), "");
      
        for(i <- 0 to (functionList.getRecordCount() - 1))
        {
          objListData.add("(" + String.valueOf(functionList.getColumn("STATUS").getData(i)) + ") " 
              + String.valueOf(functionList.getColumn("NAME").getData(i)));
        }
      }
      
      if(selectedType.equals("All") || selectedType.equals(trans("All"))
          || selectedType.equals("Procedure") || selectedType.equals(trans("Procedure")))
      {
        var procedureList : TableSet = dbtool.getProcedureWithStatus(searchField.getText(), "");
      
        for(i <- 0 to (procedureList.getRecordCount() - 1))
        {
          objListData.add("(" + String.valueOf(procedureList.getColumn("STATUS").getData(i)) + ") " + String.valueOf(procedureList.getColumn("NAME").getData(i)));
        }
      }
    }
    
    objList.setListData(objListData);
  };
  
  /**
   * <p>이 메소드는 리스트가 선택되었을 때 호출됩니다.</p>
   * 
   * @param button : 클릭된 리스트 객체
   */
  override def valueChanged(e : UIList) =
  {
    if(e == objList)
    {
      var objName : String = String.valueOf(objList.getSelectedValue()).trim().substring(new String("( ) ").length());
      
      var daoRefreshMap = new Hashtable[String, Object]();
      daoRefreshMap.put("selected_dao", "true");
      if(dbtool == null || (! dbtool.getDao.isAlive())) refresh(daoRefreshMap);
      
      scriptArea.setText(dbtool.getScriptOf(objName));
    } 
  };
  
  /**
   * <p>스크립트 영역에 있는 텍스트를 실행합니다.</p>
   */
  def executeScript() =
  {
    manager.log(manager.getDao().query(scriptArea.getText()));
  };
}