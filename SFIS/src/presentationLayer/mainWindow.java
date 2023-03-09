package presentationLayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

import appLayer.App;
import domainLayer.DBProxy;
import domainLayer.Fridge;
import domainLayer.FridgeItem;
import domainLayer.StoredItem;
import presentationLayer.swingExtensions.CustomBoxPanel;
import presentationLayer.swingExtensions.CustomButton;
import presentationLayer.swingExtensions.CustomPanel;

public class mainWindow implements ActionListener{	
	// Input Components
	private JTextField search;
	private JButton addButton, searchButton, favoritesButton;
	
	// Domain-logic
	private Fridge inv;
	
	// Components required to manage the view for item list
	private JPanel viewPanel;
	private JButton viewToggler;
	private ListViewManager viewManager;
	
	// View Toggle Icons
	private ImageIcon compressedIcon;
	private ImageIcon expressiveIcon;
	
	//frame
	private JFrame jframe;
	
	
	public mainWindow() {
	    // create our jframe as usual
		inv = App.getInstance().getInventory();
		
		jframe = new JFrame("SFIS");
		
		headerSetup();	 
	    
	    // START: Search and Item List Panel
	    JPanel searchPanel = new CustomBoxPanel(Color.black, BoxLayout.Y_AXIS);
	    searchPanel.setBounds(0,100,1000,500);
	    jframe.add(searchPanel);
	    
	    // START: Item Search Panel
	    JPanel topPanel = new CustomPanel(Color.black, null);
	    searchPanel.add(topPanel);
	    
	    search = new JTextField();
	    search.setFont(new Font("Arial", Font.PLAIN, 16));
	    search.setBackground(Color.gray);
	    search.setBounds(0,100,300,500);
	    search.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
	    search.setPreferredSize(new Dimension(300,50));
	    topPanel.add(search);
	    
	    searchButton = new CustomButton("Search", this, 50, 100);
	    topPanel.add(searchButton);
	    
	    addButton = new CustomButton("+", this, 50, 50);
	    topPanel.add(addButton);
	    
	    favoritesButton = new CustomButton("Favorites List", this, 15);
	    topPanel.add(favoritesButton);
	    // END: Search Panel
	    
	    // Set up View Toggler Button
	    compressedIcon = new ImageIcon("resources/CompressedViewIcon.png");
	    compressedIcon = new ImageIcon(compressedIcon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH));
	    expressiveIcon = new ImageIcon("resources/ExpressiveViewIcon.png");
	    expressiveIcon = new ImageIcon(expressiveIcon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH));
	    viewToggler = new CustomButton(null, this, 50, 50);
	    viewToggler.setIcon(expressiveIcon);
	    viewToggler.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	    topPanel.add(viewToggler);
	    
	    // Set up the Item List View Panel
	    List<ListView> views = new ArrayList<ListView>();
	    views.add(new CompressedListView(inv));
	    views.add(new ExpressiveListView(inv, true));
	    viewManager = new ListViewManager(views);
	    viewPanel = new CustomPanel(Color.black, null);
	    viewPanel.setPreferredSize(new Dimension(820, 400));
	    viewPanel.add((JPanel) viewManager.getCurrentView());
	    
	    searchPanel.add(viewPanel);
	    // END: Search and Item List Panel
	    
	    // Grocery List Panel
	    JPanel rightPanel = new JPanel();
	    rightPanel.setBackground(Color.black);
	    rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    rightPanel.setPreferredSize(new Dimension(400, 400));
	    
	    
	    rightPanel.add(new GroceryListView());
	    jframe.add(rightPanel, BorderLayout.EAST);
	    
	    // Update DB when closing the window
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    jframe.addWindowListener(new WindowAdapter() {
	    	@Override
	    	public void windowClosing(WindowEvent e) {
	    		DBProxy.getInstance().updateFridge(mainWindow.this.inv);
	    	}
	    });
	    
	    // set the jframe size and location, and make it visible
	    jframe.getContentPane().setBackground(Color.black);
	    jframe.setPreferredSize(new Dimension(1260, 650));
	    jframe.pack();
	    jframe.setLocationRelativeTo(null);
	    jframe.setVisible(true);
		
	}
	
	public void headerSetup() {
		JPanel panel = new CustomPanel(Color.black, null);
		panel.setBorder(BorderFactory.createEmptyBorder(25,40,40,40));
		jframe.add(panel,BorderLayout.NORTH);
	    
	    JLabel titleLabel = new JLabel("Smart Fridge Tracker");
	    titleLabel.setForeground(Color.white);
	    titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
	    panel.add(titleLabel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == addButton) {
			new addWindow(this);
			jframe.setVisible(false);
		}
		else if (e.getSource() == favoritesButton) {
			new FavoritesView(this);
			jframe.setVisible(false);
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
		viewManager.getCurrentView().generateList(inv.getItems());
		
		viewPanel.add((JPanel) viewManager.getCurrentView());
		viewPanel.revalidate();
		viewPanel.repaint();
	}
	
	
	public void mainSearchHandler() {
		//We want to ensure we can search our JList, and let it return to its former state if the search is cleared.
		//We do this by passing our inventory and using .contains to add eveyrthing that matches to our JList 
		
		String searchString = search.getText();
		List<StoredItem> matchingItems = inv.search(searchString);
		viewManager.setViewLists(matchingItems);
	}
	
	public void addNewItem() {
		int itemIndex = inv.getItems().size() - 1;
		viewManager.addItemToLists(inv.getItems().get(itemIndex));
	}
	
	//makes frame visible
	public void makeVisible() {
		jframe.setVisible(true);
	}
}
