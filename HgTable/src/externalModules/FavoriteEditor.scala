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
import java.awt.event._
import javax.swing._
import javax.swing.table._
import java.lang.management._
import java.net._
import hjow.hgtable.favorites.Favorites
import hjow.hgtable.favorites.AbstractFavorites
import java.io.File

/**
 * <p>자주 사용하는 스크립트 에디터입니다.</p>
 * 
 * @author HJOW
 */
class FavoriteEditor extends ScalaDialogModule 
{
  /*
   모듈에 대한 기본 설정들을 지정하는 곳입니다.
   주의 : 모듈 ID는 initializeComponents() 메소드 내에서 지정합니다.
   */
  var moduleName  : String  = trans("Favorite Editor"); // 모듈 이름을 지정합니다.
  var moduleLoc   : Integer = GUIDialogModule.ON_MENU_TOOL;             // 모듈이 어느 메뉴에 위치해야 할 지를 지정합니다.
  var needThreads : Boolean = false;                                    // 쓰레드를 사용하는 모듈인 경우 true 로 지정해야 합니다.
  
  /*
   필요한 컴포넌트들과 데이터에 해당하는 필드를 선언하는 곳입니다. 
   */
  var dialog : JDialog = null;
  var mainPanel : JPanel = null;
  
  var nameField : JTextField = null;
  var langField : UIComboBox = null;
  var shortcutField : UIComboBox = null;
  var maskField : UIComboBox = null;
  var descArea : JTextArea = null;
  var scriptArea : CodeEditorPane = null;
  var idField : JSpinner = null;
  var paramList : UIList = null;
  var paramInputField : JTextField = null;
  
  var btSave : JButton = null;
  var btLoad : JButton = null;
  var btClose : JButton = null;
  var btPlus : JButton = null;
  var btMinus : JButton = null;
  
  var paramDataList : Vector[String] = new Vector[String]();
  
  /**
   * <p>이 메소드에서 컴포넌트들을 초기화합니다. 모듈 내 컴포넌트들을 초기화하기 위해 이 메소드를 재정의합니다.</p>
   * 
   */
  override def initializeComponents() =
  {
    super.initializeComponents();
    
    // 모듈 ID는 고유하게 지정해야 합니다. long 형 정수 타입 (64 bits 길이)으로 지정하시면 됩니다.
    setModuleId(1627452183734L);
    
    // 기본 대화 상자와 컴포넌트들을 생성합니다.
    dialog = manager.asInstanceOf[GUIManager].newDialog(false).asInstanceOf[JDialog];
    
    var scSize : Dimension = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setSize(620, 450);
    dialog.setLocation((scSize.getWidth()/2 - dialog.getWidth()/2).toInt, (scSize.getHeight()/2 - dialog.getHeight()/2).toInt);
    
    dialog.setLayout(new BorderLayout());
    dialog.setTitle(getName());
    
    dialog.addWindowListener(this);
    
    mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    
    dialog.add(mainPanel, BorderLayout.CENTER);
    
    // 이 곳에 추가 컴포넌트들과 각종 변수들을 생성합니다.
    var tabPanel : JTabbedPane = new JTabbedPane();
    var southPanel : JPanel = new JPanel();
    southPanel.setLayout(new FlowLayout());
    mainPanel.add(tabPanel, BorderLayout.CENTER);
    mainPanel.add(southPanel, BorderLayout.SOUTH);
    
    var mainTab : JPanel = new JPanel();
    mainTab.setLayout(new BorderLayout());
    tabPanel.add(trans("Information"), mainTab);
    
    var scriptTab : JPanel = new JPanel();
    scriptTab.setLayout(new BorderLayout());
    tabPanel.add(trans("Script"), scriptTab);
    
    var infoPanel : JPanel = new JPanel();
    var descPanel : JPanel = new JPanel();
    infoPanel.setLayout(new BorderLayout());
    descPanel.setLayout(new BorderLayout());
    
    var infoGridPanel : JPanel = new JPanel();
    var infoPns : java.util.List[JPanel] = new ArrayList[JPanel]();
    infoPns.add(new JPanel());
    infoPns.add(new JPanel());
    infoPns.add(new JPanel());
    infoPns.add(new JPanel());
    infoGridPanel.setLayout(new GridLayout(infoPns.size(), 1));
    for(i <- 0 to infoPns.size() - 1)
    {
      infoPns.get(i).setLayout(new FlowLayout());
      infoGridPanel.add(infoPns.get(i));
    }
    
    var nameLabel : JLabel = new JLabel(trans("Name"));
    nameField = new JTextField(20);
    infoPns.get(0).add(nameLabel);
    infoPns.get(0).add(nameField);
    
    var langLabel : JLabel = new JLabel(trans("Language"));
    langField = new UIComboBox();
    langField.setEditable(true);
    langField.addItem("SQL");
    var scriptList : java.util.List[String] = ScriptUtil.getAvailableEngineNames();
    for(i <- 0 to scriptList.size() - 1)
    {
      langField.addItem(scriptList.get(i));
    }
    infoPns.get(1).add(langLabel);
    infoPns.get(1).add(langField);
    
    var shortcutLabel : JLabel = new JLabel(trans("Shortcut"));
    maskField = new UIComboBox();
    maskField.addItem("---");
    maskField.addItem("CTRL");
    maskField.addItem("SHIFT");
    maskField.addItem("ALT");
    shortcutField = new UIComboBox();
    shortcutField.addItem("---");
    for(i <- 0 to 9)
    {
      shortcutField.addItem(String.valueOf(i));
    }
    
    infoPns.get(2).add(shortcutLabel);
    infoPns.get(2).add(maskField);
    infoPns.get(2).add(shortcutField);
    
    idField = new JSpinner(new SpinnerNumberModel(new java.lang.Long(new Random().nextLong()), new java.lang.Long(Long.MinValue + 1), new java.lang.Long(Long.MaxValue - 1), 1));
    infoPns.get(3).add(idField);
    
    infoPanel.add(infoGridPanel, BorderLayout.CENTER);
    
    descArea = new JTextArea();
    descPanel.add(new JScrollPane(descArea));
    
    mainTab.add(new JScrollPane(infoPanel), BorderLayout.NORTH);
    mainTab.add(descPanel, BorderLayout.CENTER);
    
    scriptArea = new UISyntaxView(getDialog());
    scriptArea.setHighlightMode("SQL");
    scriptTab.add(scriptArea.getComponent(), BorderLayout.CENTER);
    
    var paramPanel : JPanel = new JPanel();
    paramPanel.setLayout(new BorderLayout());
    scriptTab.add(paramPanel, BorderLayout.SOUTH);
    
    paramList = new UIList();
    paramList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    paramPanel.add(new JScrollPane(paramList), BorderLayout.CENTER);
    
    var paramInputPanel : JPanel = new JPanel();
    paramInputPanel.setLayout(new BorderLayout());
    paramPanel.add(paramInputPanel, BorderLayout.SOUTH);
    
    paramInputField = new JTextField();
    paramInputPanel.add(paramInputField, BorderLayout.CENTER);
    
    var paramButtonPanel : JPanel = new JPanel();
    paramButtonPanel.setLayout(new FlowLayout());
    paramInputPanel.add(paramButtonPanel, BorderLayout.EAST);
    
    btPlus = new JButton("+");
    btMinus = new JButton("-");
    paramButtonPanel.add(btPlus);
    paramButtonPanel.add(btMinus);
    
    btSave = new JButton(trans("Save"));
    btLoad = new JButton(trans("Load"));
    btClose = new JButton(trans("Close"));
    
    addListener(btPlus);
    addListener(btMinus);
    
    addListener(btSave);
    addListener(btLoad);
    addListener(btClose);
    
    southPanel.add(btSave);
    southPanel.add(btLoad);
    southPanel.add(btClose);
    
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
    if(additionalData == null)
    {
      
    }
    else if(additionalData.get("type") != null && additionalData.get("type").equals("scriptChanged"))
    {
      var befores : String = scriptArea.getText();
      scriptArea.setHighlightMode(String.valueOf(langField.getSelectedItem()));
      scriptArea.setText(befores);
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
      var fav : Favorites = new Favorites();
      fav.setName(nameField.getText());
      fav.setLanguage(String.valueOf(langField.getSelectedItem()));
      var id : Object = idField.getValue();
      fav.setUniqueId(id.asInstanceOf[Long]);
      fav.setScript(scriptArea.getText());
      fav.setDescription(descArea.getText());
      var maskSelected : String = String.valueOf(maskField.getSelectedItem());
      var shortcutSelected : String = String.valueOf(shortcutField.getSelectedItem());
      if(maskSelected.equals("---")) fav.setMask(null);
      else if(maskSelected.equalsIgnoreCase("CTRL")) fav.setMask(ActionEvent.CTRL_MASK);
      else if(maskSelected.equalsIgnoreCase("ALT")) fav.setMask(ActionEvent.ALT_MASK);
      else if(maskSelected.equalsIgnoreCase("SHIFT")) fav.setMask(ActionEvent.SHIFT_MASK);
      if(shortcutSelected.equals("---")) fav.setShortcut(null);
      else fav.setShortcut(shortcutSelected.toInt + KeyEvent.VK_0);
      var paramSet : java.util.Set[String] = new java.util.HashSet[String]();
      paramSet.addAll(paramDataList);
      fav.setParameterSet(paramSet);
      var file : File = new File(Manager.getOption("config_path") + "favorites" + Manager.getOption("file_separator"));
      try
      {
        if(! file.exists()) file.mkdir();
      }
      catch
      {
        case e : Throwable =>
          {
            manager.logError(e, trans("On making directory") + " : " + file, true);
          }
      }
      GUIStreamUtil.saveText(getDialog(), fav.serialize(), null, Manager.getOption("config_path") + "favorites" + Manager.getOption("file_separator"));
    }
    else if(e == btLoad)
    {
      var path : String = Manager.getOption("config_path") + "favorites" + Manager.getOption("file_separator");
      var reads : String = GUIStreamUtil.readTextFrom(getDialog(), null, path);
      if(reads != null)
      {
        try
        {
          var fav : AbstractFavorites = FavoriteUtil.instantiation(reads);
          if(fav.getType.equalsIgnoreCase("Multiple")) alert(trans("This favorites includes multiple scripts.\nFavorite Editor cannot treat this perfectly."));
          nameField.setText(fav.getName());
          langField.setSelectedItem(fav.getLanguage());
          idField.setValue(fav.getUniqueId());
          scriptArea.setText(fav.getScript());
          descArea.setText(fav.getDescription());
          var maskVal : Int = fav.getMask();
          if(maskVal == ActionEvent.CTRL_MASK) maskField.setSelectedItem("CTRL");
          else if(maskVal == ActionEvent.ALT_MASK) maskField.setSelectedItem("ALT");
          else if(maskVal == ActionEvent.SHIFT_MASK) maskField.setSelectedItem("SHIFT");
          else maskField.setSelectedItem("---");
          var shortcutVal : Int = fav.getShortcut();
          shortcutField.setSelectedItem(String.valueOf(shortcutVal - KeyEvent.VK_0));
          paramDataList.clear();
          paramDataList.addAll(fav.getParameterSet());
          paramList.setListData(paramDataList);
        }
        catch
        {
          case e : Throwable =>
            {
              manager.logError(e, trans("Cannot read") + " " + path);
            }
        }
      }
    }
    else if(e == btPlus)
    {
      paramDataList.add(paramInputField.getText());
      paramInputField.setText("");
      paramList.setListData(paramDataList);
    }
    else if(e == btMinus)
    {
      var index : Int = paramList.getSelectedIndex();
      if(index >= 0)
      {
        paramDataList.remove(index);
        paramList.setListData(paramDataList);
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