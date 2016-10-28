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

package externalModules;

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

/**
 * <p>HTTP 도구 대화 상자입니다.</p>
 * 
 * @author HJOW
 */
class HttpTool extends ScalaDialogModule
{
  var dialog : JDialog = null;
  var mainPanel : JPanel = null;
  var upPanel : JPanel = null;
  var centerPanel : JPanel = null;
  var downPanel : JPanel = null;
  var pns : Vector[JPanel] = null;
  var labels : Vector[JLabel] = null;
  var urlField : JTextField = null;
  var encodingField : UIComboBox = null;
  var tryTransTableSet : JCheckBox = null;
  var tableModel : DefaultTableModel = null;
  var tables : JTable = null;
  var tableScroll : JScrollPane = null;
  var tableControlPanel : JPanel = null;
  var btNew : JButton = null;
  var btClear : JButton = null;
  var btSend : JButton = null;
  var btClose : JButton = null;
  var btMap   : JButton = null;
  var needWork : Boolean = false;
  var requestMap : Map[String, String] = null;
  var methodField : UIComboBox = null;
  
  /**
   * <p>이 메소드에서 컴포넌트들을 초기화합니다. 모듈 내 컴포넌트들을 초기화하기 위해 이 메소드를 재정의합니다.</p>
   * 
   */
  override def initializeComponents() =
  {
    super.initializeComponents();
    
    setModuleId(46217894516L);
    
    dialog = manager.asInstanceOf[GUIManager].newDialog(false).asInstanceOf[JDialog];
    
    var scSize : Dimension = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setSize(340, 350);
    dialog.setLocation((scSize.getWidth()/2 - dialog.getWidth()/2).toInt, (scSize.getHeight()/2 - dialog.getHeight()/2).toInt);
    
    dialog.setLayout(new BorderLayout());
    dialog.setTitle(getName());
    
    mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    
    dialog.add(mainPanel, BorderLayout.CENTER);
    
    upPanel = new JPanel();
    centerPanel = new JPanel();
    downPanel = new JPanel();
    
    mainPanel.add(upPanel, BorderLayout.NORTH);
    mainPanel.add(centerPanel, BorderLayout.CENTER);
    mainPanel.add(downPanel, BorderLayout.SOUTH);
    
    centerPanel.setLayout(new BorderLayout());
    downPanel.setLayout(new FlowLayout());
    
    var pnsSize : Int = 2;
    pns = new Vector[JPanel]();
    labels = new Vector[JLabel]();
    upPanel.setLayout(new GridLayout(pnsSize, 1));
    
    var i : Int = 1;
    for(i <- 1 to pnsSize)
    {
      var newPanel : JPanel = new JPanel();
      var newLabel : JLabel = new JLabel();
      if(i == 1)
      {
        newPanel.setLayout(new BorderLayout());
        newPanel.add(newLabel, BorderLayout.WEST);
      }
      else
      {
        newPanel.setLayout(new FlowLayout());
        newPanel.add(newLabel);
      }
      pns.add(newPanel);
      labels.add(newLabel);
      upPanel.add(newPanel);
    }
    
    labels.get(0).setText(trans("URL"));
    urlField = new JTextField();
    pns.get(0).add(urlField, BorderLayout.CENTER);
    
    var methods : Vector[String] = new Vector[String]();
    methods.add("POST");
    methods.add("GET");
    methodField = new UIComboBox(methods);
    pns.get(0).add(methodField, BorderLayout.EAST);
    
    labels.get(1).setText(trans("Options"));
    
    var encodings : Vector[String] = new Vector[String]();
    encodings.add("UTF-8");
    encodings.add("EUC-KR");
    
    encodingField = new UIComboBox(encodings);
    encodingField.setEditable(true);
    pns.get(1).add(encodingField);
    
    tryTransTableSet = new JCheckBox(trans("Try to convert table set"));
    pns.get(1).add(tryTransTableSet);
    
    tableModel = new DefaultTableModel();
    tableModel.addColumn("KEY");
    tableModel.addColumn("VALUE");
    
    tables = new JTable(tableModel);
    tableScroll = new JScrollPane(tables);
    
    centerPanel.add(tableScroll, BorderLayout.CENTER);
    
    tableControlPanel = new JPanel();
    centerPanel.add(tableControlPanel, BorderLayout.SOUTH);
    
    btNew = new JButton(trans("New"));
    buttons.add(btNew);
    
    btClear = new JButton(trans("Clean"));
    buttons.add(btClear);
    
    tableControlPanel.setLayout(new FlowLayout());
    tableControlPanel.add(btNew);
    tableControlPanel.add(btClear);
    
    btClose = new JButton(trans("Close"));
    addListener(btClose);
    
    btMap = new JButton(trans("Request Property"));
    addListener(btMap);
    
    btSend = new JButton(trans("Send"));
    addListener(btSend);
    
    downPanel.add(btSend);
    downPanel.add(btMap);
    downPanel.add(btClose);
    
    setListeners();
    
    threadGap = 250;
  };
  
  /**
   * <p>이 메소드는 매니저 객체로부터 호출됩니다. 호출된 사유는 additionalData 에 whyCalled 라는 원소에 텍스트로 삽입됩니다.</p>
   * @param additionalData : 매니저 객체로부터 받은 정보들
   */
  override def refresh(additionalData : java.util.Map[String, Object]) =
  {
    if(needWork || DataUtil.parseBoolean(additionalData.get("send")))
    {
      needWork = false;
      sendHttp();
    }
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
    return trans("HTTP Tool");
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
    pns.clear();
    super.noMoreUse();
  };
  
  /**
   * <p>HTTP로 내용을 전송합니다.</p>
   */
  def sendHttp() =
  {
    var params : Hashtable[String, Object] = new Hashtable[String, Object]();
    
    for(i <- 0 to (tableModel.getRowCount() - 1))
    {
      params.put(String.valueOf(tableModel.getValueAt(i, 0)), tableModel.getValueAt(i, 1));
    }
    
    var results : Object  = null;
    var succeed : Boolean = false;
    var errMsg  : String  = "";
    
    try
    {
      var methods : Boolean = false;
      if(String.valueOf(methodField.getSelectedItem()).equalsIgnoreCase("GET"))
      {
        methods = false;
      }
      else if(String.valueOf(methodField.getSelectedItem()).equalsIgnoreCase("POST"))
      {
        methods = true;
      }
      else
      {
        methods = true;
      }
      
      results = NetUtil.send(new URL(urlField.getText()), params, requestMap, null, String.valueOf(encodingField.getSelectedItem()), methods);
      
      if(DataUtil.isNotEmpty(results))
      {
        if(results.isInstanceOf[String] && tryTransTableSet.isSelected())
        {
          results = results.asInstanceOf[String].trim();
          if(results.asInstanceOf[String].startsWith("#") || results.asInstanceOf[String].startsWith("@")
              || results.asInstanceOf[String].startsWith("%") || results.asInstanceOf[String].startsWith("$"))
          {
            try
            {
              results = DataUtil.toTableSet(results.asInstanceOf[String]);
              succeed = true;
            }
            catch
            {
              case e : Exception =>
              {
                  succeed = false;
                  errMsg  = e.getMessage();
              }
            }
          }
          
          if(results.asInstanceOf[String].startsWith("//") || results.asInstanceOf[String].startsWith("{")
              || results.asInstanceOf[String].startsWith("["))
          {
            if(! succeed)
            {
              try
              {
                results = JSONUtil.toTableSet(results.asInstanceOf[String]);
                succeed = true;
              }
              catch
              {
                case e : Exception =>
                {
                    succeed = false;
                    errMsg  = e.getMessage();
                }
              }
            }
          }
        }
      }
    }
    catch
    {
      case e : Exception =>
      {
          succeed = false;
          errMsg  = e.getMessage();
      }
    }
    
    manager.log(results);
    
    if(succeed)
    {
      manager.log(trans("HTTP Communication is successfully finished."));
    }
    else
    {
      manager.log(trans("HTTP Communication is failed because") + " : " + errMsg);
    }
    
    close();
  };
  
  /**
   * <p>매개 변수 테이블에 새 행을 추가합니다.</p>
   * 
   */
  def newRecord() =
  {
    var newRecord : Vector[String] = new Vector[String]();
    newRecord.add("");
    newRecord.add("");
    
    tableModel.addRow(newRecord);
  };
  
  /**
   * <p>매개 변수 테이블에서 모두 비어 있는 행들을 제거합니다.</p>
   * 
   */
  def clearEmpties() =
  {
    var i : Int = 0;
    while(i<tableModel.getRowCount())
    {
      var isAllEmpty : Boolean = true;
      for(j <- 0 to 1)
      {
        if(DataUtil.isNotEmpty(tableModel.getValueAt(i, j)))
        {
          isAllEmpty = false;
        }
      }
      if(isAllEmpty)
      {
        tableModel.removeRow(i);
        i = 0;
      }
      else
      {
        i = i + 1;
      }
    }
  };
  
  /**
   * <p>이 메소드는 버튼이 클릭되었을 때 호출됩니다.</p>
   * 
   * @param button : 클릭된 버튼 객체
   */
  override def actionPerformed(e : AbstractButton) =
  {
    if(e == btClose)
    {
      close();
    }
    else if(e == btSend)
    {
      needWork = true;
    }
    else if(e == btNew)
    {
      newRecord();
    }
    else if(e == btClear)
    {
      clearEmpties();
    }
    else if(e == btMap)
    {
      requestMap = manager.askMap(trans("Input the request properties."), requestMap);
    }
  };
};