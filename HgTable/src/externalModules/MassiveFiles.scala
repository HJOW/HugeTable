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
import java.io._
import java.awt._
import javax.swing._
import javax.swing.table._
import java.lang.management._
import java.net._
import hjow.hgtable.jscript.SpecialOrder

/**
 * <p>다수의 sql 파일을 모두 동작시키고 그 결과들을 보고받을 수 있는 도구입니다.</p>
 * 
 * @author HJOW
 */
class MassiveFiles extends ScalaDialogModule 
{
  /*
   모듈에 대한 기본 설정들을 지정하는 곳입니다.
   주의 : 모듈 ID는 initializeComponents() 메소드 내에서 지정합니다.
   */
  var moduleName  : String  = trans("Massive Files Executor"); // 모듈 이름을 지정합니다.
  var moduleLoc   : Integer = GUIDialogModule.ON_MENU_TOOL;             // 모듈이 어느 메뉴에 위치해야 할 지를 지정합니다.
  var needThreads : Boolean = true;                                     // 쓰레드를 사용하는 모듈인 경우 true 로 지정해야 합니다.
  
  /*
   필요한 컴포넌트들과 데이터에 해당하는 필드를 선언하는 곳입니다. 
   */
  var dialog      : JDialog = null;
  var mainPanel   : JPanel = null;
  var upPanel     : JPanel = null;
  var centerPanel : JPanel = null;
  var downPanel   : JPanel = null;
  var centerSplit : JSplitPane = null;
  var fileList    : UIList = null;
  var messagePn   : JTextArea = null;
  var cbFile      : UIComboBox = null;
  var cbEncoding  : UIComboBox = null;
  var cbMode      : UIComboBox = null;
  
  var dirDialog       : JFileChooser = null;
  var fileDialog      : JFileChooser = null;
  var sqlFileFilter   : javax.swing.filechooser.FileFilter = null;
  var directoryFilter : javax.swing.filechooser.FileFilter = null;
  
  var progressBar     : JProgressBar = null;
  
  var btClose   : JButton = null;
  var btDelete  : JButton = null;
  var btAdd     : JButton = null;
  var btAddDir  : JButton = null;
  var btExec    : JButton = null;
  var btExecAll : JButton = null;
  
  var needWork  : Boolean = false;
  var execAll   : Boolean = false;
  var targets   : java.util.Set[File] = new java.util.HashSet[File]();
  
  /**
   * <p>이 메소드에서 컴포넌트들을 초기화합니다. 모듈 내 컴포넌트들을 초기화하기 위해 이 메소드를 재정의합니다.</p>
   * 
   */
  override def initializeComponents() =
  {
    super.initializeComponents();
    
    // 모듈 ID는 고유하게 지정해야 합니다. long 형 정수 타입 (64 bits 길이)으로 지정하시면 됩니다.
    setModuleId(6189451616846126L);
    
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
    
    
    fileDialog = new JFileChooser();
    dirDialog  = new JFileChooser();
    
    sqlFileFilter = new javax.swing.filechooser.FileFilter()
    {
      override def accept(pathname : File) : Boolean =
      {
        try
        {
          if(pathname.getAbsolutePath().endsWith(".sql") || pathname.getAbsolutePath().endsWith(".SQL"))
          {
            return true;
          }
          else return false;
        }
        catch
        {
          case e : Exception =>
          {
              
          }
        }
        
        return false;
      };
      
      override def getDescription() : String =
      {
        return trans("SQL File (*.sql)");
      };
    };
    directoryFilter = new javax.swing.filechooser.FileFilter()
    {
      override def accept(pathname : File) : Boolean =
      {
        try
        {
          return pathname.isDirectory();
        }
        catch
        {
          case e : Exception =>
          {
              
          }
        }
        
        return false;
      };
      
      override def getDescription() : String =
      {
        return trans("Directory");
      };
    };
    
    fileDialog.setFileFilter(sqlFileFilter);
    dirDialog.setFileFilter(directoryFilter);
    dirDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    
    upPanel     = new JPanel();
    downPanel   = new JPanel();
    centerPanel = new JPanel();
    
    upPanel.setLayout(new BorderLayout());
    downPanel.setLayout(new BorderLayout());
    centerPanel.setLayout(new BorderLayout());
    
    mainPanel.add(upPanel    , BorderLayout.NORTH);
    mainPanel.add(centerPanel, BorderLayout.CENTER);
    mainPanel.add(downPanel  , BorderLayout.SOUTH);
    
    centerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    centerPanel.add(centerSplit);
    
    fileList = new UIList();
    fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    
    centerSplit.setTopComponent(new JScrollPane(fileList));
    
    messagePn = new JTextArea();
    messagePn.setLineWrap(true);
    messagePn.setEditable(false);
    centerSplit.setBottomComponent(new JScrollPane(messagePn));
    
    progressBar = new JProgressBar();
    progressBar.setMinimum(0);
    progressBar.setMaximum(100);
    downPanel.add(progressBar, BorderLayout.NORTH);
    
    var controlDataPanel : JPanel = new JPanel();
    var controlExecPanel : JPanel = new JPanel();
    
    controlDataPanel.setLayout(new FlowLayout());
    controlExecPanel.setLayout(new FlowLayout());
    
    upPanel.add(controlDataPanel  , BorderLayout.CENTER);
    downPanel.add(controlExecPanel, BorderLayout.CENTER);
    
    cbFile = new UIComboBox();
    cbFile.addItem("Only sql");
    cbFile.addItem("Not bak");
    cbFile.addItem("All");
    cbFile.setEditable(true);
    
    cbEncoding = new UIComboBox();
    cbEncoding.addItem("UTF-8");
    cbEncoding.addItem("UTF-16");
    cbEncoding.addItem("EUC-KR");
    cbEncoding.addItem("MS949");
    cbEncoding.setEditable(true);
    
    cbMode = new UIComboBox();
    cbMode.addItem("SQL");
    cbMode.addItem("SQL (Commit at each)");
    cbMode.addItem("JScript");
    
    var scriptList : java.util.List[String] = ScriptUtil.getPreparedScriptNames();
    for(i <- 0 to scriptList.size() - 1)
    {
      cbMode.addItem(scriptList.get(i));
    }
    
    btAdd     = new JButton(trans("Add"));
    btAddDir  = new JButton(trans("Add Directory"));
    btDelete  = new JButton(trans("Remove Selected Files From List"));
    btExec    = new JButton(trans("Execute"));
    btExecAll = new JButton(trans("Execute All"));
    btClose   = new JButton(trans("Close"));
    
    addListener(btAdd, btAddDir, btDelete, btExec, btExecAll, btClose);
    
    controlDataPanel.add(cbFile);
    controlDataPanel.add(btAdd);
    controlDataPanel.add(btAddDir);
    controlDataPanel.add(btDelete);
    controlExecPanel.add(cbEncoding);
    controlExecPanel.add(cbMode);
    controlExecPanel.add(btExec);
    controlExecPanel.add(btExecAll);
    controlExecPanel.add(btClose);
    
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
      if(DataUtil.parseBoolean(additionalData.get("targetChanged")))
      {
        var newList : java.util.Vector[String] = new java.util.Vector[String]();
        for(t <- targets.toArray())
        {
          newList.add(t.asInstanceOf[File].getAbsolutePath());
        }
        fileList.setListData(newList);
      }
    }
  };
  
  /**
   * <p>입력한 파일이 디렉토리인 경우 내부를 탐색하여 해당되는 모든 파일 목록을 반환합니다.</p>
   * <p>입력한 파일이 디렉토리가 아닌 경우 해당 파일만 하나 들어 있는 파일 목록을 반환합니다.</p>
   * 
   * @param file : 대상 파일
   * @param fileFilter : 파일 필터, null 사용 가능, 필터 객체 삽입 시 해당하는 파일만 목록에 포함
   * @return 내부 파일 목록
   */
  def getFileList(file : File, fileFilter : FileFilter) : java.util.List[File] =
  {
    var list : java.util.List[File] = new java.util.Vector[File]();
    
    if(file.isDirectory())
    {
      var filesInDir : Array[File] = null;
      if(fileFilter == null) filesInDir = file.listFiles();
      else                   filesInDir = file.listFiles(fileFilter);
      for(f <- filesInDir)
      {
        list.addAll(getFileList(f, fileFilter));
      }
    }
    else
    {
      list.add(file);
    }
    
    return list;
  };
  
  /**
   * <p>사용자로부터 파일 선택을 입력받습니다.</p>
   * 
   * @param fileFilter : 파일 필터, null 사용 가능
   * @return 선택한 파일 목록
   */
  def askFile(fileFilter : FileFilter) : java.util.List[File] =
  {
    var selection : Int = fileDialog.showOpenDialog(dialog);
    if(selection == JFileChooser.APPROVE_OPTION)
    {
      var file : Array[File] = fileDialog.getSelectedFiles();
      var list : java.util.List[File] = new java.util.Vector[File]();
      var subs : java.util.List[File] = null;
      
      if(file.length == 0)
      {
        file = new Array[File](1);
        file(0) = dirDialog.getSelectedFile();
      }
      
      for(f <- file)
      {
        subs = getFileList(f, fileFilter);
        if(DataUtil.isNotEmpty(subs)) list.addAll(subs);
      }
      
      return list;
    }
    
    return null;
  };
  
  /**
   * <p>사용자로부터 디렉토리 선택을 입력받습니다.</p>
   * 
   * @param fileFilter : 파일 필터, null 사용 가능
   * @return 선택한 파일 목록
   */
  def askDir(fileFilter : FileFilter) : java.util.List[File] =
  {
    var selection : Int = dirDialog.showOpenDialog(dialog);
    if(selection == JFileChooser.APPROVE_OPTION)
    {
      var file : Array[File] = dirDialog.getSelectedFiles();
      var list : java.util.List[File] = new java.util.Vector[File]();
      var subs : java.util.List[File] = null;
      
      if(file.length == 0)
      {
        file = new Array[File](1);
        file(0) = dirDialog.getSelectedFile();
      }
      
      for(f <- file)
      {
        println(f.getAbsolutePath());
        subs = getFileList(f, fileFilter);
        if(DataUtil.isNotEmpty(subs)) list.addAll(subs);
      }
      
      return list;
    }
    
    return null;
  };
  
  /**
   * <p>특정 문자열을 파일 필터로 변환합니다.</p>
   * 
   * @param selected : 사용자가 입력/선택했을 텍스트
   */
  def getFileFilterFrom(selected : String) : FileFilter =
  {
    if(selected.equals("Only sql"))
    {
      return new FileFilter()
      {
        override def accept(pathname : File) : Boolean =
        {
          return (pathname.getAbsolutePath().endsWith(".sql") || pathname.getAbsolutePath().endsWith(".SQL"));
        };
      };
    }
    else if(selected.equals("Not bak"))
    {
      return new FileFilter()
      {
        override def accept(pathname : File) : Boolean =
        {
          return (! (pathname.getAbsolutePath().endsWith("bak") || pathname.getAbsolutePath().endsWith("BAK")));
        };
      };
    }
    else if(selected.equals("All"))
    {
      return null;
    }
    else if(selected.startsWith("Only"))
    {
      return new FileFilter()
      {
        override def accept(pathname : File) : Boolean =
        {
          return (pathname.getAbsolutePath().endsWith(selected.substring(4).trim()) 
              || pathname.getAbsolutePath().endsWith(selected.substring(4).toUpperCase().trim()));
        };
      };
    }
    else if(selected.startsWith("Not"))
    {
      return new FileFilter()
      {
        override def accept(pathname : File) : Boolean =
        {
          return (! (pathname.getAbsolutePath().endsWith(selected.substring(3).trim()) 
              || pathname.getAbsolutePath().endsWith(selected.substring(3).toUpperCase().trim())));
        };
      };
    }
    else
    {
      return new FileFilter()
      {
        override def accept(pathname : File) : Boolean =
        {
          return (! (pathname.getAbsolutePath().endsWith(selected) 
              || pathname.getAbsolutePath().endsWith(selected.toUpperCase())));
        };
      };
    }
  };
  
  /**
   * <p>사용자가 선택한 파일 필터를 반환합니다.</p>
   * 
   */
  def getSelectedFileFilter() : FileFilter =
  {
    var cbModeSelected : String = String.valueOf(cbFile.getSelectedItem());
    
    var cbModeTokened : Array[String] = cbModeSelected.split(",");
    if(cbModeTokened.length >= 2)
    {
      return new FileFilter()
      {
        override def accept(pathname : File) : Boolean =
        {
          var temp : FileFilter = null;
          for(t : String <- cbModeTokened)
          {
            temp = getFileFilterFrom(t.trim());
            if(temp != null)
            {
              if(! (temp.accept(pathname)))
              {
                return false;
              }
            }
          }
          return true;
        };
      };
    }
    else
    {
      return getFileFilterFrom(cbModeSelected);
    }
  };
  
  /**
   * <p>사용자가 파일을 추가하려 했을 때 호출됩니다.</p>
   */
  def addFile() : java.util.Map[String, Object] =
  {
    var additionalData : java.util.Map[String, Object] = new java.util.Hashtable[String, Object]();
    var fileFilter : FileFilter = null;
    var askedFile : java.util.List[File] = null;
    
    fileFilter = getSelectedFileFilter();
    
    askedFile = askFile(fileFilter);
    if(DataUtil.isNotEmpty(askedFile))
    {
      targets.addAll(askedFile);
      additionalData.put("targetChanged", "true");
    }
    
    return additionalData;
  };
  
  /**
   * <p>사용자가 디렉토리를 추가하려 했을 때 호출됩니다.</p>
   * 
   */
  def addDir() : java.util.Map[String, Object] =
  {
    var additionalData : java.util.Map[String, Object] = new java.util.Hashtable[String, Object]();
    var fileFilter : FileFilter = null;
    var askedFile : java.util.List[File] = null;
    
    fileFilter = getSelectedFileFilter();
    
    askedFile = askDir(fileFilter);
    if(DataUtil.isNotEmpty(askedFile))
    {
      targets.addAll(askedFile);
      additionalData.put("targetChanged", "true");
    }
    
    return additionalData;
  };
  
  /**
   * <p>이 메소드는 버튼이 클릭되었을 때 호출됩니다.</p>
   * 
   * @param e : 클릭된 버튼 객체
   */
  override def actionPerformed(e : AbstractButton) =
  {
    var additionalData : java.util.Map[String, Object] = new java.util.Hashtable[String, Object]();
    if(e == btAdd)
    {
      additionalData.putAll(addFile());
    }
    else if(e == btAddDir)
    {
      additionalData.putAll(addDir());
    }
    else if(e == btClose)
    {
      close();
    }
    else if(e == btDelete)
    {
      var selected : Array[Object] = fileList.getSelectedValues();
      var targetArr : Array[Object]  = targets.toArray();
      for(s <- selected)
      {
        var i : Int = 0;
        while(i < targetArr.length)
        {
          if(targetArr(i).asInstanceOf[File].getAbsolutePath().equals(String.valueOf(s)))
          {
            targets.remove(targetArr(i));
            targetArr = targets.toArray();
            i = 0;
          }
          else
          {
            i = i + 1;
          }
        }
      }
      additionalData.put("targetChanged", "true");
    }
    else if(e == btExec || e == btExecAll)
    {
      if(e == btExec) execAll = false;
      else execAll = true;
      
      messagePn.setText("");
      progressBar.setValue(0);
      
      cbFile.setEnabled(false);
      btAdd.setEnabled(false);
      btAddDir.setEnabled(false)
      btDelete.setEnabled(false);
      btExec.setEnabled(false);
      btExecAll.setEnabled(false);
      btClose.setEnabled(false);
      
      needWork = true;
    }
    refresh(additionalData);
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
      
      var selected : java.util.List[File] = new java.util.Vector[File]();
      if(execAll)
      {
        selected.addAll(targets);
      }
      else
      {
        var items : Array[Object] = fileList.getSelectedValues();
        for(i <- items)
        {
          selected.add(new File(String.valueOf(i)));
        }
      }
      
      var sqls : String = null;
      var encoding : String = String.valueOf(cbEncoding.getSelectedItem());
      if(DataUtil.isEmpty(encoding)) encoding = "UTF-8";
      encoding = encoding.trim();
      
      var successCnt : Int = 0;
      var failCnt    : Int = 0;
      var totalCnt   : Int = selected.size();
      
      var mode : String = String.valueOf(cbMode.getSelectedItem());
      
      for(i <- 0 to totalCnt - 1)
      {
        try
        {
          sqls = StreamUtil.readText(selected.get(i), encoding);
          sqls = sqls.trim();
          if(sqls.endsWith("/") || sqls.endsWith("\\")) sqls = sqls.substring(0, sqls.length() - 1); 
          
          messagePn.append(trans("Executing") + " : " + selected.get(i).getAbsolutePath() + "...");
          
          if(mode.equalsIgnoreCase("SQL"))
          {
            manager.getDao().queryWithoutLog(sqls, this);
          }
          else if(mode.equalsIgnoreCase("SQL (Commit at each)"))
          {
            manager.getDao().queryWithoutLog(sqls, this);
            manager.getDao().commit();
          }
          else if(mode.equalsIgnoreCase("JScript"))
          {
            manager.log(manager.getMainScriptEngine(this).execute(sqls));
          }
          else
          {
            var simplifyErrOption : Boolean = ScriptUtil.getRunner(mode).isDefaultErrorSimplicityOption();
            var result : Object = ScriptUtil.getRunner(mode).execute(sqls);
            if(result.isInstanceOf[TableSet]) manager.logTable(result.asInstanceOf[TableSet]);
            else if(result.isInstanceOf[SpecialOrder]) SpecialOrderUtil.act(result.asInstanceOf[SpecialOrder].getOrder(), manager.getMainScriptEngine(this));
            else manager.log(result);
          }
          
          messagePn.append(trans("Success") + "\n");
          successCnt = successCnt + 1;
        }
        catch
        {
          case e : Throwable =>
          {
            try
            {
              manager.logError(e, trans("On executing") + " " + selected.get(i).getAbsolutePath());
              messagePn.append(trans("Fail") + "\n" + trans("Why") + " : " + e.getMessage() + "\n");
              if(mode.equalsIgnoreCase("SQL (Commit at each)"))
              {
                manager.getDao().rollback();
              }
            }
            catch
            {
              case e1 : Throwable =>
                {
                  e1.printStackTrace();
                }
            }
            failCnt = failCnt + 1;
          }
        }
        
        messagePn.setCaretPosition(messagePn.getDocument().getLength());
        progressBar.setValue(((i.asInstanceOf[Double] / totalCnt.asInstanceOf[Double]) * 100.0).asInstanceOf[Int]);
      }
      
      cbFile.setEnabled(true);
      btAdd.setEnabled(true);
      btAddDir.setEnabled(true)
      btDelete.setEnabled(true);
      btExec.setEnabled(true);
      btExecAll.setEnabled(true);
      btClose.setEnabled(true);
      
      messagePn.append(trans("Complete") + ", " + trans("Success") + " : " + successCnt + " / " + trans("Total") + " : " + totalCnt + "\n");
      messagePn.setCaretPosition(messagePn.getDocument().getLength());
      progressBar.setValue(100);
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