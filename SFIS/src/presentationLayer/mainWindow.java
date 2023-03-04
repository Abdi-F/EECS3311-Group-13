package presentationLayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import domainLayer.DBProxy;
import domainLayer.Fridge;
import domainLayer.FridgeItem;
import domainLayer.StoredItem;

public class mainWindow extends JFrame implements ActionListener{
	private JFrame jframe = new JFrame("SFIS");
	private JPanel panel;
	private JPanel searchPanel;
	private JLabel titleLabel;
	
	private JTextField search;
	private JList<String> list;
	private JButton addButton, searchButton,incButton,decButton;
	private DefaultListModel<String> fridgeList;
	private Fridge inv;
	private List<StoredItem> displayItems;
	
	private JPanel viewPanel;
	private JPanel compressedView;
	private ExpressiveListView expressiveView;
	private JButton viewToggler;
	
	private ImageIcon compressedIcon;
	private ImageIcon expressiveIcon;
	
	private ListViewManager viewManager;
	
	public mainWindow() {
	    // create our jframe as usual
		List<StoredItem> items = DBProxy.getInstance().loadItems();
		inv = new Fridge(items);
			
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(25,40,40,40));
	    panel.setBackground(Color.black);
		jframe.add(panel,BorderLayout.NORTH);
	
	    
	    titleLabel = new JLabel("Smart Fridge Tracker");
	    titleLabel.setForeground(Color.white);
	    titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
	    panel.add(titleLabel);
	    //search and list panel
	    searchPanel = new JPanel();
	    searchPanel.setBackground(Color.black);
	    searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
	    searchPanel.setBounds(0,100,1000,500);
	    jframe.add(searchPanel);
	    
	    JPanel topPanel = new JPanel();
	    topPanel.setBackground(Color.black);
	    searchPanel.add(topPanel);
	    
	    search = new JTextField();
	    search.setFont(new Font("Arial", Font.PLAIN, 16));
	    search.setBackground(Color.gray);
	    search.setBounds(0,100,300,500);
	    search.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
	    search.setPreferredSize(new Dimension(300,50));
	    topPanel.add(search);
	    
	    
	    searchButton = new JButton("Search");
	    searchButton.addActionListener(this);
	    searchButton.setPreferredSize(new Dimension(100,50));
	    topPanel.add(searchButton);
	    
	    
	    addButton = new JButton("+");
	    addButton.addActionListener(this);
	    addButton.setPreferredSize(new Dimension(50,50));
	    topPanel.add(addButton);
	    
	    compressedIcon = new ImageIcon("resources/CompressedViewIcon.png");
	    compressedIcon = new ImageIcon(compressedIcon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH));
	    expressiveIcon = new ImageIcon("resources/ExpressiveViewIcon.png");
	    expressiveIcon = new ImageIcon(expressiveIcon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH));
	    viewToggler = new JButton();
	    viewToggler.setPreferredSize(new Dimension(50, 50));
	    viewToggler.setIcon(expressiveIcon);
	    viewToggler.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	    viewToggler.addActionListener(this);
	    topPanel.add(viewToggler);
	    
	    List<ListView> views = new ArrayList<ListView>();
	    views.add(new CompressedListView(inv));
	    views.add(new ExpressiveListView(inv));
	    viewManager = new ListViewManager(views);
	    
	    viewPanel = new JPanel();
	    viewPanel.setBackground(Color.black);
	    viewPanel.setPreferredSize(new Dimension(820, 400));
	    viewPanel.add((JPanel) viewManager.getCurrentView());
	    
	    searchPanel.add(viewPanel);
	    
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    jframe.getContentPane().setBackground(Color.black);
	    // set the jframe size and location, and make it visible
	    jframe.setPreferredSize(new Dimension(1000, 650));
	    jframe.pack();
	    jframe.setLocationRelativeTo(null);
	    jframe.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == addButton) {
			new AddWindowController(this, inv);
		}
		else if (e.getSource() == searchButton) {		
			mainSearchHandler();
		}
		else if (e.getSource() == viewToggler) {
			this.mainViewToggleHandler();
		}
	}
	
	public void mainViewToggleHandler() {
		if (viewToggler.getIcon() == compressedIcon) {
			viewToggler.setIcon(expressiveIcon);
		} else {
			viewToggler.setIcon(compressedIcon);
		}
		
		viewPanel.remove((JPanel) viewManager.getCurrentView());
		viewManager.toggle();
		viewManager.getCurrentView().generateList(inv.getFridgeItems());
		
		viewPanel.add((JPanel) viewManager.getCurrentView());
		viewPanel.revalidate();
		viewPanel.repaint();
	}
	
	
	public void mainSearchHandler() {
		//We want to ensure we can search our JList, and let it return to its former state if the search is cleared.
		//We do this by passing our inventory and using .contains to add eveyrthing that matches to our JList 
		
		String searchString = search.getText();
		List<StoredItem> matchingItems = inv.search(searchString);
		System.out.println('g');
		viewManager.setViewLists(matchingItems);
	}
	
	public void addNewItem() {
		int itemIndex = inv.getFridgeItems().size() - 1;
		viewManager.addItemToLists(inv.getFridgeItems().get(itemIndex));
	}
}
