package presentationLayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import domainLayer.DBProxy;
import domainLayer.UserHistory;
import persistenceLayer.StubDB;
import presentationLayer.swingExtensions.CustomPanel;
import presentationLayer.swingExtensions.GridConstraintsSpec;

public class HistoryView extends JPanel {
	
	public static void main(String[] args) {
		DBProxy.getInstance().setDB(new StubDB());
		HistoryView lay = new HistoryView(DBProxy.getInstance().loadUserHistory());
		
		
		JFrame jframe = new JFrame("Hi");
		jframe.add(lay);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    jframe.getContentPane().setBackground(Color.black);
	    jframe.setPreferredSize(new Dimension(1140, 600));
	    jframe.pack();
	    jframe.setLocationRelativeTo(null);
	    jframe.setVisible(true);
	}
	
	public HistoryView(UserHistory data) {
		this.setBackground(Color.black);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel titleLabel = new JLabel("User History");
		titleLabel.setForeground(Color.white);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.add(titleLabel);
		
		JPanel dataPanel = new CustomPanel(Color.black, new GridBagLayout());
		dataPanel.setBorder(BorderFactory.createMatteBorder(2, 1, 2, 1, Color.white));
		for (int day = 0; day < 7; day++) {
			HistoryDayComponent dayPanel = new HistoryDayComponent(data, 6-day);
			dataPanel.add(dayPanel, GridConstraintsSpec.stretchableFillConstraints(day, 0, 1, 1, GridBagConstraints.BOTH));
		}
		this.add(dataPanel);
	}
}
