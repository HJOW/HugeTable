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

package externalModules.h2

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
import org.h2.tools.Server
import hjow.dbtool.h2.HTool


/**
 * <p>스칼라 언어를 이용한 대화 상자형 모듈 예제이자 기본 형식입니다.</p>
 * <p>이 형식을 가져다 새 모듈을 만드려는 경우, 파일 이름과 클래스 이름, 그리고 모듈 ID를 변경해 주셔야 합니다.</p>
 * 
 * @author HJOW
 */
class H2Runner extends ScalaDialogModule 
{
  /*
   모듈에 대한 기본 설정들을 지정하는 곳입니다.
   주의 : 모듈 ID는 initializeComponents() 메소드 내에서 지정합니다.
   */
  var moduleName  : String  = trans("H2 Database Configurator");        // 모듈 이름을 지정합니다.
  var moduleLoc   : Integer = GUIDialogModule.ON_MENU_TOOL;             // 모듈이 어느 메뉴에 위치해야 할 지를 지정합니다.
  var needThreads : Boolean = false;                                    // 쓰레드를 사용하는 모듈인 경우 true 로 지정해야 합니다.
  
  /*
   필요한 컴포넌트들과 데이터에 해당하는 필드를 선언하는 곳입니다. 
   */
  var dialog      : JDialog = null;
  var mainPanel   : JPanel = null;
  
  var upPanel     : JPanel = null;
  var centerPanel : JPanel = null;
  var downPanel   : JPanel = null;
  
  var argPanel    : JTextField = null;
  
  var btPg        : JButton = null;
  var btTcp       : JButton = null;
  var btWeb       : JButton = null;
  var btClose     : JButton = null;
  
  var pgServer    : Server = null;
  var tcpServer   : Server = null;
  var webServer   : Server = null;
  
  /**
   * <p>이 메소드에서 컴포넌트들을 초기화합니다. 모듈 내 컴포넌트들을 초기화하기 위해 이 메소드를 재정의합니다.</p>
   * 
   */
  override def initializeComponents() =
  {
    super.initializeComponents();
    
    // 모듈 ID는 고유하게 지정해야 합니다. long 형 정수 타입 (64 bits 길이)으로 지정하시면 됩니다.
    setModuleId(1564861692654846562L);
    
    // 기본 대화 상자와 컴포넌트들을 생성합니다.
    dialog = manager.asInstanceOf[GUIManager].newDialog(false).asInstanceOf[JDialog];
    
    var scSize : Dimension = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setSize(600, 150);
    dialog.setLocation((scSize.getWidth()/2 - dialog.getWidth()/2).toInt, (scSize.getHeight()/2 - dialog.getHeight()/2).toInt);
    
    dialog.setLayout(new BorderLayout());
    dialog.setTitle(getName());
    
    dialog.addWindowListener(this);
    
    mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    
    dialog.add(mainPanel, BorderLayout.CENTER);
    
    // 이 곳에 추가 컴포넌트들과 각종 변수들을 생성합니다.
    upPanel     = new JPanel();
    centerPanel = new JPanel();
    downPanel   = new JPanel();
    
    mainPanel.add(upPanel    , BorderLayout.NORTH);
    mainPanel.add(centerPanel, BorderLayout.CENTER);
    mainPanel.add(downPanel  , BorderLayout.SOUTH);
    
    centerPanel.setLayout(new BorderLayout());
    
    argPanel = new JTextField();
    centerPanel.add(argPanel, BorderLayout.CENTER);
    
    upPanel.setLayout(new FlowLayout());
    downPanel.setLayout(new FlowLayout());
    
    btPg  = new JButton(trans("Turn on PG server"));
    btTcp = new JButton(trans("Turn on TCP server"));
    btWeb = new JButton(trans("Turn on WEB server"));
    
    upPanel.add(btPg);
    upPanel.add(btTcp);
    upPanel.add(btWeb);
    
    btClose = new JButton(trans("Close"));
    downPanel.add(btClose);
    
    addListener(btPg, btTcp, btWeb, btClose);
    
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
    
  };
  
  /**
   * <p>이 메소드는 버튼이 클릭되었을 때 호출됩니다.</p>
   * 
   * @param e : 클릭된 버튼 객체
   */
  override def actionPerformed(e : AbstractButton) =
  {
    if(e == btPg)
    {
      if(pgServer != null)
      {
        try
        {
          pgServer.shutdown();
        }
        catch
        {
          case e1 : Throwable =>
            {
              
            }
        }
        pgServer = null;
        manager.log(trans("PG Server is closed."));
        btPg.setText(trans("Turn on PG server"));
      }
      else
      {
        try
        {
          pgServer = HTool.createPGServer(getArgs());
          manager.log(trans("PG Server is started."));
          btPg.setText(trans("Turn off PG server"));
        }
        catch
        {
          case e1 : Throwable =>
            {
              manager.logError(e1, trans("On starting H2 Database PG Server"));
            }
        }
      }
    }
    else if(e == btTcp)
    {
      if(tcpServer != null)
      {
        try
        {
          tcpServer.shutdown();
        }
        catch
        {
          case e1 : Throwable =>
            {
              
            }
        }
        tcpServer = null;
        manager.log(trans("TCP Server is closed."));
        btTcp.setText(trans("Turn on TCP server"));
      }
      else
      {
        try
        {
          tcpServer = HTool.createTCPServer(getArgs());
          manager.log(trans("TCP Server is started."));
          btTcp.setText(trans("Turn off TCP server"));
        }
        catch
        {
          case e1 : Throwable =>
            {
              manager.logError(e1, trans("On starting H2 Database TCP Server"));
            }
        }
      }
    }
    else if(e == btWeb)
    {
      if(webServer != null)
      {
        try
        {
          webServer.shutdown();
        }
        catch
        {
          case e1 : Throwable =>
            {
              
            }
        }
        webServer = null;
        manager.log(trans("WEB Server is closed."));
        btWeb.setText(trans("Turn on WEB server"));
      }
      else
      {
        try
        {
          webServer = HTool.createWEBServer(getArgs());
          manager.log(trans("WEB Server is started."));
          btWeb.setText(trans("Turn off WEB server"));
        }
        catch
        {
          case e1 : Throwable =>
            {
              manager.logError(e1, trans("On starting H2 Database Web Server"));
            }
        }
      }
    }
    else if(e == btClose)
    {
      close();
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
    
  };
  
  /**
   * <p>이 객체 사용을 중단하기 위해 순환 참조를 끊습니다. 프로그램이 종료될 때 호출됩니다.</p>
   * 
   */
  override def noMoreUse() =
  {
    super.noMoreUse();
    
    // 프로그램이 종료될 때 수행해야 할 작업을 이 곳에 작성합니다.
    if(pgServer != null)
    {
      try
        {
          pgServer.shutdown();
        }
        catch
        {
          case e : Throwable =>
            {
              
            }
        }
    }
    if(tcpServer != null)
    {
      try
        {
          tcpServer.shutdown();
        }
        catch
        {
          case e : Throwable =>
            {
              
            }
        }
    }
    if(webServer != null)
    {
      try
        {
          webServer.shutdown();
        }
        catch
        {
          case e : Throwable =>
            {
              
            }
        }
    }
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
  
  /**
   * <p>텍스트 필드로부터 매개 변수를 받습니다. 콤마로 각 매개 변수들을 구분합니다.</p>
   * 
   */
  def getArgs() : java.util.List[String] =
  {
    var text : String = argPanel.getText();
    var result : java.util.Vector[String] = new java.util.Vector[String]();
    text.split(",").foreach( t => result.add(t.trim()) );
    return result;
  };
}