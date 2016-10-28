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

/**
 * <p>현재 실행중인 Huge Table 의 모든 쓰레드의 스택 추적 내역을 보여 줍니다.</p>
 * 
 * @author HJOW
 */
class StackTraceView extends ScalaDialogModule 
{
  /*
   모듈에 대한 기본 설정들을 지정하는 곳입니다.
   주의 : 모듈 ID는 initializeComponents() 메소드 내에서 지정합니다.
   */
  var moduleName  : String  = trans("Thread View"); // 모듈 이름을 지정합니다.
  var moduleLoc   : Integer = GUIDialogModule.ON_MENU_VIEW;             // 모듈이 어느 메뉴에 위치해야 할 지를 지정합니다.
  var needThreads : Boolean = false;                                    // 쓰레드를 사용하는 모듈인 경우 true 로 지정해야 합니다.
  
  /*
   필요한 컴포넌트들과 데이터에 해당하는 필드를 선언하는 곳입니다. 
   */
  var dialog : JDialog = null;
  var mainPanel : JPanel = null;
  var splitPane : JSplitPane = null;
  
  var area : JTextArea = null;
  var threadList : UIList = new UIList();
  var threadInfoArea : JTextArea = null;
  
  var btRefresh : JButton = null;
  var btShutdown : JButton = null;
  var btSuspend : JButton = null;
  var btClose : JButton = null;
  
  var threads : Vector[Thread] = new Vector[Thread]();
  var threadNames : Vector[String] = new Vector[String]();
  var firstTime : Boolean = true;
  
  /**
   * <p>이 메소드에서 컴포넌트들을 초기화합니다. 모듈 내 컴포넌트들을 초기화하기 위해 이 메소드를 재정의합니다.</p>
   * 
   */
  override def initializeComponents() =
  {
    super.initializeComponents();
    
    // 모듈 ID는 고유하게 지정해야 합니다. long 형 정수 타입 (64 bits 길이)으로 지정하시면 됩니다.
    setModuleId(275854764621413L);
    
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
    
    mainPanel.add(upPanel, BorderLayout.NORTH);
    mainPanel.add(centerPanel, BorderLayout.CENTER);
    
    centerPanel.setLayout(new BorderLayout());
    upPanel.setLayout(new FlowLayout());
    
    var centerTab : JTabbedPane = new JTabbedPane();
    
    area = new JTextArea();
    area.setLineWrap(true);
    area.setEditable(false);
    
    centerTab.add(trans("Information"), new JScrollPane(area));
    centerPanel.add(centerTab, BorderLayout.CENTER);
    
    var treatPanel : JPanel = new JPanel();
    treatPanel.setLayout(new BorderLayout());
    centerTab.add(trans("Treat"), treatPanel);
    
    var splitPane : JSplitPane = new JSplitPane();
    splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    treatPanel.add(splitPane, BorderLayout.CENTER);
    
    var controlPanel : JPanel = new JPanel();
    controlPanel.setLayout(new FlowLayout());
    btShutdown = new JButton(trans("Stop thread"));
    btSuspend = new JButton(trans("Suspend Thread"));
    controlPanel.add(btShutdown);
    controlPanel.add(btSuspend);
    treatPanel.add(controlPanel, BorderLayout.SOUTH);
    
    threadList = new UIList();
    threadInfoArea = new JTextArea();
    splitPane.setTopComponent(new JScrollPane(threadList));
    splitPane.setBottomComponent(new JScrollPane(threadInfoArea));
    
    
    btRefresh = new JButton(trans("Refresh"));
    btClose = new JButton(trans("Close"));
    upPanel.add(btRefresh);
    upPanel.add(btClose);
    
    addListener(threadList);
    addListener(btRefresh);
    addListener(btShutdown);
    addListener(btSuspend);
    addListener(btClose);
    
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
    if(additionalData != null && DataUtil.parseBoolean(additionalData.get("stacktrace")))
    {
      var stackTraceMap : java.util.Map[Thread, Array[StackTraceElement]] = Thread.getAllStackTraces();
      threads.clear();
      threadNames.clear();
      threads.addAll(stackTraceMap.keySet());
      var text : StringBuffer = new StringBuffer("");
      for(i <- 0 to threads.size() - 1)
      {
        var thread : Thread = threads.get(i);
        var threadName : String = thread.getName() + "(" + thread.getId() + ")";
        threadNames.add(threadName);
        if(i >= 1) text = text.append("\n\n");
        text = text.append(makeThreadInfoText(thread));
      }
      area.setText(text.toString());
      threadList.setListData(threadNames);
    }
    else if(additionalData != null && DataUtil.parseBoolean(additionalData.get("list")))
    {
      var selectedIndex : Int = threadList.getSelectedIndex();
      if(selectedIndex >= 0)
      {
        var thread : Thread = threads.get(selectedIndex);
        threadInfoArea.setText(makeThreadInfoText(thread));
        threadInfoArea.setCaretPosition(0);
      }
      else
      {
        threadInfoArea.setText("");
      }
    }
  };
  
  /**
   * <p>쓰레드 정보를 텍스트로 출력해 반환합니다.</p>
   * 
   */
  def makeThreadInfoText(t : Thread) : String =
  {
    var text : StringBuffer = new StringBuffer("");
    text = text.append(trans("Thread"));
    text = text.append("\n").append(trans("Name") + " : ").append(t.getName()).append("(" + t.getId() + ")");
    text = text.append("\n").append(trans("Priority") + " : ").append(t.getPriority());
    text = text.append("\n").append(trans("State") + " : ").append(t.getState());
    text = text.append("\n");
    
    if(t.isInstanceOf[HThread])
    {
      text = text.append(t.asInstanceOf[HThread].getDescription());
      text = text.append("\n");
    }
    
    var stackTraces : Array[StackTraceElement] = t.getStackTrace();
        
    for(i <- 0 to stackTraces.length - 1)
    {
      var stackTrace : StackTraceElement = stackTraces(i);
      if(i <= 0) text = text.append("\n").append(stackTrace);
      else text = text.append("\n" + trans(" <- ")).append(stackTrace);
    }
    return text.toString();
  };
  
  /**
   * <p>이 메소드는 리스트가 선택되었을 때 호출됩니다.</p>
   * 
   * @param e : 클릭된 리스트 객체
   */
  override def valueChanged(e : UIList) =
  {
    if(e == threadList)
    {
      var param : java.util.Map[String, Object] = new Hashtable[String, Object]();
      param.put("list", "true");
      refresh(param);
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
      var param : java.util.Map[String, Object] = new Hashtable[String, Object]();
      param.put("stacktrace", "true");
      param.put("list", "true");
      refresh(param);
    }
    else if(e == btSuspend)
    {
      suspendThread();
    }
    else if(e == btShutdown)
    {
      shutdownThread();
    }
  };
  
  def suspendThread() =
  {
    var selectedIndex : Int = threadList.getSelectedIndex();
    if(selectedIndex >= 0)
    {
      var accepted : Boolean = true;
      if(firstTime)
      {
        accepted = confirm(trans("This operation can harm system. Do you want to do it?"));
        if(accepted) firstTime = false;
      }
      if(accepted)
      {
        var thread : Thread = null;
        try
        {
          thread = threads.get(selectedIndex);
          thread.suspend();
        }
        catch
        {
          case e1 : Throwable =>
            {
              manager.logError(e1, trans("On suspend thread") + " " + thread);
            }
        }
        
        var param : java.util.Map[String, Object] = new Hashtable[String, Object]();
        param.put("stacktrace", "true");
        param.put("list", "true");
        refresh(param);
      }
    }
    else
    {
      alert(trans("Select a thread what you want to suspend."));
    }
  };
  
  def shutdownThread() =
  {
    var selectedIndex : Int = threadList.getSelectedIndex();
    if(selectedIndex >= 0)
    {
      var accepted : Boolean = true;
      if(firstTime)
      {
        accepted = confirm(trans("This operation can harm system. Do you want to do it?"));
        if(accepted) firstTime = false;
      }
      if(accepted)
      {
        var thread : Thread = null;
        try
        {
          thread = threads.get(selectedIndex);
          thread.stop();
        }
        catch
        {
          case e1 : Throwable =>
            {
              manager.logError(e1, trans("On shutdown thread") + " " + thread);
            }
        }
        
        var param : java.util.Map[String, Object] = new Hashtable[String, Object]();
        param.put("stacktrace", "true");
        param.put("list", "true");
        refresh(param);
      }
    }
    else
    {
      alert(trans("Select a thread what you want to shutdown."));
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

  };
  
  /**
   * <p>이 메소드는 사용자가 이 모듈을 사용하기 위해 대화 상자를 열려고 할 때 호출됩니다.</p>
   * 
   */
  override def open() =
  {
    super.open();
    
    // 사용자가 이 모듈을 사용하려고 대화 상자를 열었을 때 수행해야 할 작업을 이 곳에 작성합니다.
    var param : java.util.Map[String, Object] = new Hashtable[String, Object]();
    param.put("stacktrace", "true");
    refresh(param);
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