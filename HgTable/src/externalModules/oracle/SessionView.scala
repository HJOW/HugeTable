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
/*
 Be careful !
 Oracle software has a license policy called OTN.
 If you want to use this source to use Oracle Database, you should agree OTN.
 Visit http://www.oracle.com to see details. 
 */

package externalModules.oracle

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
import hjow.dbtool.oracle.OTool
import java.sql.SQLException

/**
 * <p>세션 정보를 확인하는 뷰입니다. 오라클 DB 에서만 정상 작동하며, 사용자가 DBA 권한을 가지고 있어야 사용이 가능합니다.</p>
 * 
 * @author HJOW
 */
class SessionView extends ScalaDialogModule 
{
  var moduleName  : String  = trans("Session View");
  var moduleLoc   : Integer = GUIDialogModule.ON_MENU_VIEW;
  var needThreads : Boolean = true;
  
  var dialog : JDialog = null;
  var mainPanel : JPanel = null;
  
  var upPanel : JPanel = null;
  var downPanel : JPanel = null;
  var centerPanel : JPanel = null;
  
  var tableModel : TableModel = null;
  var tables : JTable = null;
  var tableScroll : JScrollPane = null;
  
  var btClose : JButton = null;
  var btRefresh : JButton = null;
  
  var autoRefresh : JCheckBox = null;
  
  var otool : OTool = null;
  var data : TableSet = null;
  
  /**
   * <p>이 메소드에서 컴포넌트들을 초기화합니다. 모듈 내 컴포넌트들을 초기화하기 위해 이 메소드를 재정의합니다.</p>
   * 
   */
  override def initializeComponents() =
  {
    super.initializeComponents();
    
    setModuleId(16188948618153L);
    
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
    
    upPanel = new JPanel();
    downPanel = new JPanel();
    centerPanel = new JPanel();
    
    mainPanel.add(upPanel, BorderLayout.NORTH);
    mainPanel.add(centerPanel, BorderLayout.CENTER);
    mainPanel.add(downPanel, BorderLayout.SOUTH);
    
    centerPanel.setLayout(new BorderLayout());
    
    tableModel = new DefaultTableModel();
    tables = new JTable(tableModel);
    tableScroll = new JScrollPane(tables);
    
    centerPanel.add(tableScroll, BorderLayout.CENTER);
    
    downPanel.setLayout(new FlowLayout());
    
    btClose = new JButton(trans("Close"));
    addListener(btClose);
    
    downPanel.add(btClose);
    
    upPanel.setLayout(new FlowLayout());
    
    btRefresh = new JButton(trans("Refresh"));
    addListener(btRefresh);
    
    autoRefresh = new JCheckBox(trans("Auto Refresh"));
    addListener(autoRefresh);
    
    upPanel.add(btRefresh);
    upPanel.add(autoRefresh);
    
    setListeners();
    setThreadGap(1000);
  };
  
  /**
   * <p>이 메소드는 쓰레드 내에서 실행됩니다. 별도의 설정이 없다면 100 밀리초 (0.1초)에 한 번씩 실행됩니다.</p>
   * 
   */
  @throws(classOf[Exception])
  override def onThread() =
  {
    if(autoRefresh.isSelected() && getDialog().isVisible()) refresh(new java.util.Hashtable[String, Object]());
  };
  
  /**
   * <p>이 메소드는 매니저 객체로부터 호출됩니다. 호출된 사유는 additionalData 에 whyCalled 라는 원소에 텍스트로 삽입됩니다.</p>
   * @param additionalData : 매니저 객체로부터 받은 정보들
   */
  override def refresh(additionalData : java.util.Map[String, Object]) =
  {
    if(additionalData != null && DataUtil.isNotEmpty(additionalData.get("selected_dao")))
    {
      refreshSelectedDao();
    }
    
    search();
  };
  
  /**
   * <p>선택된 DAO를 가져옵니다.</p>
   * 
   */
  def refreshSelectedDao() = 
  {
    if(manager.getDao() == null) otool = null;
    else if(! (manager.getDao().isAlive())) otool = null;
    else if(manager.getDao().getDataSourceTool().isInstanceOf[OTool])
    {
      otool = manager.getDao().getDataSourceTool().asInstanceOf[OTool];
    }
    else otool = null;
  };
  
  /**
   * <p>세션 목록을 조회합니다.</p>
   * 
   */
  def search() = 
  {
    if(otool != null && manager.getDao() != null && manager.getDao().isAlive())
    {
      try
      {
        var sessionInfoquery : String = "SELECT SID, USERNAME, OWNERID, STATUS, SCHEMANAME, OSUSER FROM V$SESSION ORDER BY STATUS, USERNAME";
        var results : TableSet = manager.getDao().queryWithoutLog(sessionInfoquery, this);
        
        if(data != null)
        {
          if(! data.equals(results))
          {
            tableModel = GUIUtil.toTableModel(results);
            tables.setModel(tableModel);
          }
        }
      }
      catch
      {
        case e : SQLException =>
        {
           manager.log(trans("Cannot get session information because") + " " + e.getMessage());
           manager.log(trans("Session View can work if DAO is connected at Oracle Database and there is a DBA privilege."));
           autoRefresh.setSelected(false);
        }
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
    else if(e == btRefresh)
    {
      refreshSelectedDao();
      search();
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
   * <p>이 메소드는 사용자가 이 모듈을 사용하기 위해 대화 상자를 열려고 할 때 호출됩니다.</p>
   * 
   */
  override def open() =
  {
    super.open();
  };
  
  /**
   * <p>이 객체 사용을 중단하기 위해 순환 참조를 끊습니다. 프로그램이 종료될 때 호출됩니다.</p>
   * 
   */
  override def noMoreUse() =
  {
    super.noMoreUse();
  };
  
  override def getLicense() : String =
  {
    return "URL://http://www.oracle.com/technetwork/licenses/standard-license-152015.html";
  };
  
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