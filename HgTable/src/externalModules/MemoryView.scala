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
 * <p>메모리 관리 대화 상자입니다.</p>
 * 
 * @author HJOW
 */
class MemoryView extends ScalaDialogModule
{
  var dialog : JDialog = null;
  var mainPanel : JPanel = null;
  var upPanel : JPanel = null;
  var centerPanel : JPanel = null;
  var downPanel : JPanel = null;
  var pns : Vector[JPanel] = null;
  var labels : Vector[JLabel] = null;
  var totalField : JTextField = null;
  var maxField : JTextField = null;
  var usingField : JTextField = null;
  var totalBar : JProgressBar = null;
  var btClose : JButton = null;
  var btGc : JButton = null;
  var otherArea : JTextArea = null;
  var otherScroll : JScrollPane = null;
  
  var runtimes : Runtime = Runtime.getRuntime();
  
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
    dialog.setSize(250, 350);
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
    downPanel.setLayout(new BorderLayout());
    
    var pnsSize : Int = 5;
    pns = new Vector[JPanel]();
    labels = new Vector[JLabel]();
    upPanel.setLayout(new GridLayout(pnsSize, 1));
    
    var i : Int = 1;
    for(i <- 1 to pnsSize)
    {
      var newPanel : JPanel = new JPanel();
      var newLabel : JLabel = new JLabel();
      newPanel.setLayout(new FlowLayout());
      newPanel.add(newLabel);
      pns.add(newPanel);
      labels.add(newLabel);
      upPanel.add(newPanel);
    }
    
    labels.get(1).setText(trans("Total Memory"));
    totalField = new JTextField(15);
    totalField.setEditable(false);
    pns.get(1).add(totalField);
    
    labels.get(2).setText(trans("Using Memory"));
    usingField = new JTextField(15);
    usingField.setEditable(false);
    pns.get(2).add(usingField);
    
    labels.get(3).setText(trans("Max Memory"));
    maxField = new JTextField(15);
    maxField.setEditable(false);
    pns.get(3).add(maxField);
    
    totalBar = new JProgressBar();
    totalBar.setMinimum(0);
    totalBar.setMaximum(100);
    totalBar.setValue(0);
    
    pns.get(4).add(totalBar);
    
    btClose = new JButton(trans("Close"));
    btGc = new JButton(trans("Clean"));
    
    pns.get(0).add(btGc);
    pns.get(0).add(btClose);
    
    otherArea = new JTextArea();
    otherArea.setEditable(false);
    otherScroll = new JScrollPane(otherArea);
    
    centerPanel.add(otherScroll, BorderLayout.CENTER);
    
    dialog.addWindowListener(this);
    addListener(btClose);
    addListener(btGc);
    
    setListeners();
    
    threadGap = 250;
  };
  
  /**
   * <p>이 메소드는 매니저 객체로부터 호출됩니다. 호출된 사유는 additionalData 에 whyCalled 라는 원소에 텍스트로 삽입됩니다.</p>
   * @param additionalData : 매니저 객체로부터 받은 정보들
   */
  override def refresh(additionalData : java.util.Map[String, Object]) =
  {
    var totals : Long = runtimes.totalMemory();
    var maxes : Long = runtimes.maxMemory();
    var frees : Long = runtimes.freeMemory();
    
    totalField.setText(DataUtil.toByteUnit(totals));
    maxField.setText(DataUtil.toByteUnit(maxes));
    usingField.setText(DataUtil.toByteUnit(totals - frees));
    
    totalBar.setValue((((totals - frees) * 100.0) / totals).toInt);
    otherArea.setText(ConsoleUtil.memoryData(false, true));
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
    return trans("Memory Viewer");
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
    runtimes = null;
    super.noMoreUse();
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
    else if(e == btGc)
    {
      System.gc();
    }
  };
};