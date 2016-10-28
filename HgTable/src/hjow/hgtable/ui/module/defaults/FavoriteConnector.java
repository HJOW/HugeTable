package hjow.hgtable.ui.module.defaults;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import hjow.hgtable.dao.JdbcDao;
import hjow.hgtable.ui.AccessInfo;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.module.GUIConnectModule;
import hjow.hgtable.ui.swing.UIList;
import hjow.hgtable.util.DataUtil;

/**
 * 
 * 데이터 소스 접속을 편하게 할 수 있는 도구입니다.
 * 
 * @author HJOW
 *
 */
public class FavoriteConnector extends GUIConnectModule
{
	private static final long serialVersionUID = -3325116062899226037L;
	public static final transient long sid = serialVersionUID;
	protected JPanel mainPanel;
	protected UIList list;
	protected volatile Vector<AccessInfo> accessInfos = new Vector<AccessInfo>();
	protected JButton btConnect;
	protected JButton btRemove;
	protected JButton btAdd;
	protected JDialog dialog;
	protected JButton dialog_btSave;
	protected JButton dialog_btClose;
	protected volatile JTextField urlField;
	protected volatile JTextField idField;
	protected volatile JPasswordField pwField;
	protected volatile JTextField driverField;
	
	/**
	 * 데이터 소스 접속 패널 객체를 생성합니다.
	 * 
	 * @param managerObj : GUI 기반 매니저 객체
	 */
	public FavoriteConnector(GUIManager managerObj)
	{
		super();
		super.manager = managerObj;
		setName(trans("Favorites"));
		setModuleId(sid);
	}
	
	@Override
	public void initializeComponents()
	{
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		
		JPanel rightPanel = new JPanel();
		JPanel[] rightPanels = new JPanel[3];
		for(int i=0; i<rightPanels.length; i++)
		{
			rightPanels[i] = new JPanel();
			rightPanels[i].setLayout(new BorderLayout());
			rightPanel.add(rightPanels[i]);
		}
		rightPanel.setLayout(new GridLayout(rightPanels.length, 1));
		
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(rightPanel, BorderLayout.EAST);
		
		list = new UIList();
		list.setMinimumSize(new Dimension(50, 10));
		list.setMaximumSize(new Dimension(8192, 100));
		JScrollPane scroll = new JScrollPane(list);
		centerPanel.add(scroll, BorderLayout.CENTER);
		
		btConnect = new JButton(trans("Connect"));
		btConnect.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						int selectedIdx = list.getSelectedIndex();
						if(selectedIdx < 0)
						{
							manager.alert(trans("Select connection first to connect."));
							return;
						}
						AccessInfo selected = accessInfos.get(selectedIdx);
						if(DataUtil.isEmpty(selected.getPw()))
						{
							String pw = manager.askInput(trans("Input password to connect") + "...\n" + selected.toString());
							if(DataUtil.isEmpty(pw))
							{
								manager.log(trans("Connection is cancelled."));
								return;
							}
							selected.setPw(pw);
						}
						JdbcDao dao = new JdbcDao(manager);
						dao.setAccessInfo(selected);
						manager.addDao(dao);
						dao.connectParallely(new Runnable()
						{
							@Override
							public void run()
							{
								SwingUtilities.invokeLater(new Runnable()
								{
									@Override
									public void run()
									{
										((GUIManager) manager).refreshDaos(true, false);
									}
								});
							}
						});
					}
				});
			}
		});
		btRemove = new JButton(trans("Remove"));
		btRemove.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						int selectedIdx = list.getSelectedIndex();
						if(selectedIdx < 0)
						{
							manager.alert(trans("Select connection first to remove."));
							return;
						}
						accessInfos.remove(selectedIdx);
						saveAccessInfos();
					}
				});
			}
		});
		
		btAdd = new JButton(trans("Add"));
		btAdd.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				dialog.setVisible(true);
			}
		});
		
		rightPanels[0].add(btAdd);
		rightPanels[1].add(btRemove);
		rightPanels[2].add(btConnect);
		
		dialog = new JDialog(((GUIManager) manager).getFrame(), true);
		dialog.setSize(300, 200);
		
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		dialog.setLocation((int)(scrSize.getWidth()/2 - dialog.getWidth()/2), (int)(scrSize.getHeight()/2 - dialog.getHeight()/2));
		dialog.setTitle(trans("New Favorite Connection"));
		dialog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				dialog.setVisible(false);
			}
		});
		
		dialog.setLayout(new BorderLayout());
		
		JPanel dialog_mainPanel = new JPanel();
		dialog_mainPanel.setLayout(new BorderLayout());
		dialog.add(dialog_mainPanel, BorderLayout.CENTER);
		
		JPanel dialog_centerPanel = new JPanel();
		JPanel dialog_downPanel = new JPanel();
		
		dialog_centerPanel.setLayout(new BorderLayout());
		dialog_downPanel.setLayout(new FlowLayout());
		
		dialog_mainPanel.add(dialog_centerPanel, BorderLayout.CENTER);
		dialog_mainPanel.add(dialog_downPanel, BorderLayout.SOUTH);
		
		JPanel dialog_leftPanel = new JPanel();
		JPanel dialog_rightPanel = new JPanel();
		
		dialog_centerPanel.add(dialog_leftPanel, BorderLayout.WEST);
		dialog_centerPanel.add(dialog_rightPanel, BorderLayout.CENTER);
		
		JPanel[] dialog_lefts = new JPanel[4];
		JPanel[] dialog_rights = new JPanel[dialog_lefts.length];
		
		dialog_leftPanel.setLayout(new GridLayout(dialog_lefts.length, 1));
		dialog_rightPanel.setLayout(new GridLayout(dialog_rights.length, 1));
		
		for(int i=0; i<dialog_lefts.length; i++)
		{
			dialog_lefts[i] = new JPanel();
			dialog_rights[i] = new JPanel();
			
			dialog_lefts[i].setLayout(new BorderLayout());
			dialog_rights[i].setLayout(new BorderLayout());
			
			dialog_leftPanel.add(dialog_lefts[i]);
			dialog_rightPanel.add(dialog_rights[i]);
		}
		
		JLabel driverLabel = new JLabel(trans("Driver"));
		driverField = new JTextField();
		dialog_lefts[0].add(driverLabel);
		dialog_rights[0].add(driverField);
		
		JLabel urlLabel = new JLabel(trans("URL"));
		urlField = new JTextField();
		dialog_lefts[1].add(urlLabel);
		dialog_rights[1].add(urlField);
		
		JLabel idLabel = new JLabel(trans("ID"));
		idField = new JTextField();
		dialog_lefts[2].add(idLabel);
		dialog_rights[2].add(idField);
		
		JLabel pwLabel = new JLabel(trans("PW"));
		pwField = new JPasswordField();
		dialog_lefts[3].add(pwLabel);
		dialog_rights[3].add(pwField);
		
		dialog_btSave = new JButton(trans("Save"));
		dialog_btSave.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						AccessInfo newAccessInfo = new AccessInfo();
						newAccessInfo.setClassPath(driverField.getText());
						newAccessInfo.setUrl(urlField.getText());
						newAccessInfo.setId(idField.getText());
						newAccessInfo.setPw(new String(pwField.getPassword()));
						
						accessInfos.add(newAccessInfo);
						saveAccessInfos();
						dialog.setVisible(false);
					}
				});
			}
		});
		dialog_downPanel.add(dialog_btSave);
		
		dialog_btClose = new JButton(trans("Cancel"));
		dialog_btClose.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				dialog.setVisible(false);
			}
		});
		
		dialog_downPanel.add(dialog_btClose);
		
		loadAccessInfos();
	}
	
	/**
	 * 모듈 속성 저장소에 자주 사용하는 접속 정보들을 저장합니다.
	 */
	protected void saveAccessInfos()
	{
		String optionText = "";
		for(int i=0; i<accessInfos.size(); i++)
		{
			optionText = optionText + "ID:" + accessInfos.get(i).getId();
			if(! DataUtil.isEmpty(accessInfos.get(i).getPw())) optionText = optionText + "||PW:" + accessInfos.get(i).getPw();
			if(! DataUtil.isEmpty(accessInfos.get(i).getUrl())) optionText = optionText + "||URL:" + accessInfos.get(i).getUrl();
			if(! DataUtil.isEmpty(accessInfos.get(i).getClassPath())) optionText = optionText + "||DRIVER:" + accessInfos.get(i).getClassPath();
			if(i < accessInfos.size() - 1) optionText = optionText + "|||";
		}
		
		getOptions().put("Saves", optionText);
		
		saveOptions();
		loadAccessInfos();
	}
	
	/**
	 * 모듈 속성 저장소로부터 자주 사용하는 접속 정보들을 가져옵니다.
	 */
	protected void loadAccessInfos()
	{
		accessInfos.clear();
		
		Map<String, String> options = getOptions();
		if(options == null || options.isEmpty()) return;
		
		String accessInfoOption = options.get("Saves");
		if(DataUtil.isEmpty(accessInfoOption)) return;
		StringTokenizer threeBarTokenizer = new StringTokenizer(accessInfoOption, "|||");
		
		while(threeBarTokenizer.hasMoreTokens())
		{
			String oneElement = threeBarTokenizer.nextToken().trim();
			
			StringTokenizer twoBarTokenizer = new StringTokenizer(oneElement, "||");
			Map<String, String> currentParam = new Hashtable<String, String>();
			while(twoBarTokenizer.hasMoreTokens())
			{
				String ones = twoBarTokenizer.nextToken().trim();
				StringTokenizer colonTokenizer = new StringTokenizer(ones, ":");
				String key = null;
				String value = "";
				while(colonTokenizer.hasMoreTokens())
				{
					if(key == null)
					{
						key = colonTokenizer.nextToken().trim();
					}
					else
					{
						if(! DataUtil.isEmpty(value)) value = value + ":";
						value = value + colonTokenizer.nextToken().trim();
					}
				}
				currentParam.put(key, value);
			}
			
			AccessInfo newAccessInfo = new AccessInfo();
			if(currentParam.containsKey("URL")) newAccessInfo.setUrl(currentParam.get("URL"));
			if(currentParam.containsKey("ID")) newAccessInfo.setId(currentParam.get("ID"));
			if(currentParam.containsKey("PW")) newAccessInfo.setPw(currentParam.get("PW"));
			if(currentParam.containsKey("DRIVER")) newAccessInfo.setClassPath(currentParam.get("DRIVER"));
			
			accessInfos.add(newAccessInfo);
		}
		
		list.setListData(accessInfos);
	}

	@Override
	public Component getComponent()
	{
		return mainPanel;
	}
	
	@Override
	public void noMoreUse()
	{
		try
		{
			list.removeAll();
		}
		catch(Exception e)
		{
			
		}
		super.noMoreUse();
	}
}
