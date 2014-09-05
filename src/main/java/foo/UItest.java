package foo;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.AWTException;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.Vector;


public class UItest
{
	private static JFrame frame;
	
	private static JPanel topMenuPanel;
	private static JPanel topStatePanel;
	private static JPanel leftPanel;
	private static JPanel rightPanel;
	private static JPanel bottomPanel;
	private static JPanel statePanel;
	
	private static JPanel SendPanel;
	private static JPanel addressPanel;
	private static JPanel amountPanel;
	
	private static JPanel TransactionPanel;
	
	private static JLabel balanceLabel;
	private static JTextField addressTextField;
	private static JTextField amountTextField;
	
	private static JLabel myaddressLabel;
	
	private static JTable table;
	private static Vector<Vector> rowData;
	
	final private static String trayiconStr = "res/image/money_yen.png";
	
	private static TrayIcon trayicon;
	private static JPanel networkLabelContainer;
	private static JLabel networkLabel;
	private static ImageIcon network0;
	private static ImageIcon network1;
	private static ImageIcon network2;
	private static ImageIcon network3;
	private static ImageIcon network4;
	
	private static JProgressBar downloadBar;
	
	private static Font font;
	
	
	////update UI
	
	public static void UpdateBalance(String str)
	{
		balanceLabel.setText("Balance "+ str +"BTC");
	}
	
	public static void UpdateWalletAddress(String str)
	{
		myaddressLabel.setText(str);
	}
	
	public static void UpdateTable()
	{
		rowData.clear();
		rowData.addAll(App.getTableDataFromWallet());
		table.updateUI();
	}
	
	//downloadBar value
	public static void UpdateDownloadBarValue(int n)
	{
		downloadBar.setValue(n);
	}
	
	//downlaodBar text
	public static void UpdateDownloadBarText(String str)
	{
		downloadBar.setString(str);
	}
	
	//network
	public static void UpdateNetLabel(int peerCount)
	{
		if(peerCount == 0)
		{
			networkLabel.setIcon(network0);
		}
		else if(peerCount == 1)
		{
			networkLabel.setIcon(network1);
		}
		else if(peerCount == 2)
		{
			networkLabel.setIcon(network2);
		}
		else if(peerCount == 3)
		{
			networkLabel.setIcon(network3);
		}	
		else if(peerCount == 4)
		{
			networkLabel.setIcon(network4);
		}
		//networkLabelContainer.revalidate();
	}
	
	//////////////////////////////////////////////////////////////////////////////
	
	private static void init()
	{
		font = new Font("Dialog", 1 , 15);
		
		network0 = new ImageIcon("res/image/connect0_16.png");
		network1 = new ImageIcon("res/image/connect1_16.png");
		network2 = new ImageIcon("res/image/connect2_16.png");
		network3 = new ImageIcon("res/image/connect3_16.png");
		network4 = new ImageIcon("res/image/connect4_16.png");
	}
	
	private static void createTopMenuPanel()
	{
		topMenuPanel = new JPanel();
		topMenuPanel.setLayout(new BoxLayout(topMenuPanel, BoxLayout.X_AXIS));
		
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("菜单");
		
		JMenuItem menuItem = new JMenuItem("跨平台外观");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
					UIManager.setLookAndFeel(lookAndFeel);
				} catch(Throwable e1) {
					e1.printStackTrace();
				}
				SwingUtilities.updateComponentTreeUI(frame);
			}
		});
		
		JMenuItem menuItem2 = new JMenuItem("系统外观");
		menuItem2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
					UIManager.setLookAndFeel(lookAndFeel);
				} catch(Throwable e1) {
					e1.printStackTrace();
				}
				SwingUtilities.updateComponentTreeUI(frame);
			}
		});
		
		menuBar.add(menu);
		menu.add(menuItem);
		menu.add(menuItem2);
		topMenuPanel.add(menuBar);
		topMenuPanel.add(Box.createGlue());
	}
	
	private static void createTopStatePanel()
	{
		topStatePanel = new JPanel();
		topStatePanel.setLayout(new BoxLayout(topStatePanel, BoxLayout.Y_AXIS));
		topStatePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		balanceLabel = new JLabel("Balance 0.00BTC");
	    balanceLabel.setFont(font);
		
	    //Panel with balanceLabel
	    JPanel middlePanel = new JPanel();
	    middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.X_AXIS)); 
	    middlePanel.add(Box.createHorizontalStrut(15));
	    middlePanel.add(balanceLabel);
	    middlePanel.add(Box.createHorizontalGlue());
	    
	    topStatePanel.add(middlePanel);
	    topStatePanel.add(Box.createVerticalStrut(10));
	}
	
	
	//put a JSplitPane with a divider in the bottomPanel
	private static void createBottomPanel()
	{
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		//Tab Wallet
		JTabbedPane walletbook = new JTabbedPane();
		//wallet panel
		JPanel walletPanel = new JPanel();
		walletPanel.setLayout(new BoxLayout(walletPanel, BoxLayout.Y_AXIS));
		//add a test button
		JButton testButton = new JButton("test");
		testButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				App.testAction();
			}
		});
		
		//my address label
		myaddressLabel = new JLabel();
		JTextField myaddressTextField = new JTextField();
		myaddressTextField.setText(App.getMyAddressString());
		myaddressTextField.setEditable(false);
		myaddressTextField.setBorder(null);
		
		JPanel textFieldPanel = new JPanel();
		textFieldPanel.setLayout(new FlowLayout());
		textFieldPanel.add(myaddressTextField);
		//textFieldPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		walletPanel.add(textFieldPanel);
		walletPanel.add(Box.createVerticalStrut(2));
		//walletPanel.add(myaddressLabel);
		//walletPanel.add(myaddressTextField);
		walletPanel.add(testButton);
		walletPanel.add(Box.createVerticalGlue());
		walletbook.addTab("Wallets", null, walletPanel);
		
		//Tab right
		JTabbedPane book = new JTabbedPane();	
		  //add SendPanel to Tab Send
		createSendPanel();
		  //add TXPanel to Tab Send
		createTransactionPanel();
		book.addTab("Send", null, SendPanel, "Send bitcoin to someone");
		book.addTab("Transactions", null, TransactionPanel, "View transactions");
		book.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				UpdateTable();
			}
		});
		
		//Create State
		createStatePanel();
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(walletbook);
		splitPane.setRightComponent(book);
		
		JPanel splitContainer = new JPanel();
		splitContainer.setLayout(new BoxLayout(splitContainer, BoxLayout.X_AXIS));
		splitContainer.add(splitPane);
		bottomPanel.add(splitContainer);
		bottomPanel.add(statePanel);
	}
	
	private static void createStatePanel()
	{
		statePanel = new JPanel();
		statePanel.setLayout(new BoxLayout(statePanel, BoxLayout.X_AXIS));
		//statePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		networkLabel = new JLabel(network0);
		
		networkLabelContainer = new JPanel();
		networkLabelContainer.setLayout(new BoxLayout(networkLabelContainer, BoxLayout.Y_AXIS));
		networkLabelContainer.add(networkLabel);
		
		
		downloadBar = new JProgressBar();
		downloadBar.setStringPainted(true);
		
		JPanel downloadBarContainer = new JPanel();
		downloadBarContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
		downloadBarContainer.add(downloadBar);

		
		statePanel.add(downloadBarContainer);
		statePanel.add(Box.createHorizontalGlue());
		statePanel.add(networkLabelContainer);
		statePanel.add(Box.createHorizontalStrut(5));
	}
	
	//SendPanel Tab in the BottomPanel
	private static void createSendPanel()
	{
		SendPanel = new JPanel();
		SendPanel.setLayout(new BoxLayout(SendPanel, BoxLayout.Y_AXIS));
		SendPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		//SendPanel.add(Box.createVerticalStrut(10));
		createAddressPanel();
		createAmountPanel();
		
		SendPanel.add(Box.createVerticalStrut(10));
		SendPanel.add(addressPanel);
		SendPanel.add(Box.createVerticalStrut(10));
		SendPanel.add(amountPanel);
		SendPanel.add(Box.createVerticalGlue());
	}
	
	//addressPanel in the SendPanel
	private static void createAddressPanel()
	{
		addressPanel = new JPanel();
		addressPanel.setLayout(new BoxLayout(addressPanel, BoxLayout.X_AXIS));	
		//addressPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		JLabel addressLabel = new JLabel("Address");
		addressLabel.setFont(font);
		addressTextField = new JTextField();
		addressTextField.setPreferredSize(new Dimension(260, 25));
		addressTextField.setMinimumSize(addressTextField.getPreferredSize());
		addressTextField.setMaximumSize(addressTextField.getPreferredSize());
		JPanel textPanel = new JPanel();
		textPanel.add(addressTextField);
		
		addressPanel.add(Box.createHorizontalStrut(10));
		addressPanel.add(addressLabel);
		addressPanel.add(Box.createHorizontalStrut(10));
		addressPanel.add(addressTextField);
		addressPanel.add(Box.createHorizontalGlue());
		
		//addressPanel.add(Box.createHorizontalStrut(100));
	}
	
	//amountPanel in the SendPanel
	private static void createAmountPanel()
	{
		amountPanel = new JPanel();
		amountPanel.setLayout(new BoxLayout(amountPanel, BoxLayout.X_AXIS));
		//amountPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		JLabel amountLabel = new JLabel("Amount");
		amountLabel.setFont(font);
		
		amountTextField = new JTextField();
		amountTextField.setPreferredSize(new Dimension(100, 25));
		amountTextField.setMinimumSize(addressTextField.getPreferredSize());
		amountTextField.setMaximumSize(addressTextField.getPreferredSize());
		
		
		//amountPanel.add(Box.createHorizontalStrut(10));
		JLabel amountBTCLabel = new JLabel("BTC");
		amountBTCLabel.setFont(font);
		
		//TODO check textfield value, address value and Glue()
		//add after send 
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				App.simpleSendToAddress(amountTextField.getText(), addressTextField.getText());
			}
		});
		
		amountPanel.add(Box.createHorizontalStrut(10));
		amountPanel.add(amountLabel);
		amountPanel.add(Box.createHorizontalStrut(10));
		amountPanel.add(amountTextField);
		amountPanel.add(amountBTCLabel);
		amountPanel.add(Box.createHorizontalStrut(20));
		amountPanel.add(sendButton);
		amountPanel.add(Box.createHorizontalGlue());
		//amountPanel.add(Box.createHorizontalStrut(200));
	}
	
	
	private static void createTransactionPanel()
	{
		TransactionPanel = new JPanel();
		TransactionPanel.setLayout(new BoxLayout(TransactionPanel, BoxLayout.Y_AXIS));
		
		//add Table
		//String[] columnName = {"Status", "Data" , "Description", "Amount (BTC)"};
		Vector<String> columnName = new Vector<String>(4);
		columnName.add("Status");
		columnName.add("Data");
		columnName.add("Description");
		columnName.add("Amount (BTC)");
		
		rowData = new Vector<Vector>(1);
		Vector<String> row = new Vector<String>(4);
		
		//add list and scroll
		table = new JTable(rowData, columnName);
		table.getColumnModel().getColumn(0).setMaxWidth(60);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		TableRowSorter sorter = new TableRowSorter(new DefaultTableModel(rowData, columnName));
		table.setRowSorter(sorter);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		//add show detail tx button
		JButton txhistorybutton = new JButton("显示交易详情...");
		txhistorybutton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int row = table.getSelectedRow();
				System.out.println(App.getTxhashFromRow(row));
				System.out.println(App.getTransactionFormTxhash(App.getTxhashFromRow(row)).toString());
				TxDialog txdialog = new TxDialog(frame, "test", true);
				txdialog.setVisible(true);
			}
		});
		
		TransactionPanel.add(scrollPane);
		TransactionPanel.add(txhistorybutton);
		
		
	}
	
	/////////////get image
	private static Image getImage(String str) throws HeadlessException
	{
		Icon defaultIcon = new ImageIcon(str);
		Image img = new BufferedImage(defaultIcon.getIconWidth(), defaultIcon.getIconHeight(),
				BufferedImage.SCALE_SMOOTH);
		defaultIcon.paintIcon(new Panel(), img.getGraphics(), 0, 0);
		return img;
	}
	////
	
	
	/////////add SystemTray
	private static void addSystemTray()
	{
		trayicon = new TrayIcon(getTrayImage(), "DemoWallet",
				createPopupMenu());
		trayicon.setImageAutoSize(true);
		
		try {
			SystemTray.getSystemTray().add(trayicon);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void displayTrayMessage(String title, String content)
	{
		trayicon.displayMessage(title, content, TrayIcon.MessageType.INFO);
	}
	
	private static Image getTrayImage() throws HeadlessException
	{
		Icon defaultIcon = new ImageIcon(trayiconStr);
		Image img = new BufferedImage(defaultIcon.getIconWidth(), defaultIcon.getIconHeight(),
				BufferedImage.SCALE_SMOOTH);
		defaultIcon.paintIcon(new Panel(), img.getGraphics(), 0, 0);
		return img;
	}
	
	private static PopupMenu createPopupMenu() throws HeadlessException
	{
		PopupMenu menu = new PopupMenu();
		MenuItem exit = new MenuItem("Exit");
		exit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		menu.add(exit);
		return menu;
	}
	//////////////////////////////////////////////////////////
	
	public static void createAndShowGUI()
	{
		//set the font
		init();
		
		//add SystemTray
		addSystemTray();
		
		createTopMenuPanel();
		createTopStatePanel();
		//createLeftPanel();
		//createRightPanel();
		createBottomPanel();
		//createStatePanel();
		
		//create main panel with GridBagLayout
		JPanel panelContainer = new JPanel();
		panelContainer.setLayout(new GridBagLayout());
		
		GridBagConstraints cTopMenu = new GridBagConstraints();
		cTopMenu.gridx = 0;
		cTopMenu.gridy = 0;
		cTopMenu.gridwidth = 5;
		cTopMenu.fill = GridBagConstraints.HORIZONTAL;
		panelContainer.add(topMenuPanel, cTopMenu);
		
		//add each panel in the panelContainer
		GridBagConstraints cTopState = new GridBagConstraints();
		cTopState.gridx = 0;
		cTopState.gridy = 1;
		cTopState.gridwidth = 5;
		cTopState.weightx = 0;
		cTopState.weighty = 0;
		cTopState.fill = GridBagConstraints.HORIZONTAL;
		panelContainer.add(topStatePanel, cTopState);
		
		GridBagConstraints cBottom = new GridBagConstraints();
		cBottom.gridx = 0;
		cBottom.gridy = 2;
		cBottom.weightx = 1.0;
		cBottom.weighty = 1.0;
		cBottom.fill = GridBagConstraints.BOTH;
		panelContainer.add(bottomPanel, cBottom);
		
		/*
		GridBagConstraints cState = new GridBagConstraints();
		cState.gridx = 0;
		cState.gridy = 2;
		cState.weightx = 1.0;
		cState.weighty = 1.0;
		cState.fill = GridBagConstraints.HORIZONTAL;
		panelContainer.add(statePanel, cState);
		*/
		
		/*
		GridBagConstraints cLeft = new GridBagConstraints();
		cLeft.gridx = 0;
		cLeft.gridy = 1;
		cLeft.gridwidth = 2;
		cLeft.weightx = 1.0;
		cLeft.weighty = 1.0;
		cLeft.fill = GridBagConstraints.BOTH;
		panelContainer.add(leftPanel, cLeft);
		
		GridBagConstraints cRight = new GridBagConstraints();
		cRight.gridx = 2;
		cRight.gridy = 1;
		cRight.gridwidth = 3;
		cRight.weightx = 1.0;
		cRight.weighty = 1.0;
		cRight.fill = GridBagConstraints.BOTH;
		panelContainer.add(rightPanel, cRight);
		*/
		
		//create and set frame
		frame = new JFrame("DemoWallet");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panelContainer.setOpaque(true);
		//frame.pack();
		
		//frame.setLocationRelativeTo(null);
		
		frame.setSize(new Dimension(840, 660));
		frame.setContentPane(panelContainer);
		frame.setVisible(true);
	}
}


/*
public class UItest extends JPanel implements ActionListener
{
	JButton button;
	JButton button2;
	static JLabel label1;
	JTextField text1;
	
	public UItest()
	{
		//构造函数调用父类JPanel的构造函数并用BorderLayout()初始化
		super(new BorderLayout());
		//新建button并设置大小
		button = new JButton("Show Wallet");
		button2 = new JButton("Send");
		label1 = new JLabel("label");
		text1 = new JTextField();
		
		text1.setPreferredSize(new Dimension(60, 20));
		//button.setPreferredSize(new Dimension(200, 80));
		//加入button到BorderLayout
		add(button, BorderLayout.CENTER);
		add(button2, BorderLayout.EAST);
		add(label1, BorderLayout.NORTH);
		add(text1, BorderLayout.WEST);
		
		button.addActionListener(this);
		button2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				App.simpleSend(text1.getText());
			}
		});
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		//Toolkit.getDefaultToolkit().beep();
		App.printtest();
		//System.out.println(text1.getText());
	}

	public static void UpdataLabel(String str)
	{
		label1.setText(str);
	}
	
	public static void createAndShowGUI()
	{
		JFrame frame = new JFrame("DemoWallet");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//新建本类并加入到JFrame中
		JComponent newContentPane = new UItest();
		//设置不透明
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);
		//设置窗口适应组建
		frame.pack();
		//设置窗口可见
		frame.setVisible(true);
	}

}
*/