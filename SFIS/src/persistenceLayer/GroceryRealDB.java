package persistenceLayer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import domainLayer.GroceryList;
import domainLayer.StoredItem;
import domainLayer.FoodItem.CreationType;
import domainLayer.FoodItem.StockType;
import domainLayer.FridgeItem;

public class GroceryRealDB {
	
	DBHelpers helper = new DBHelpers();
	String createGroceryTable = "Create Table if not exists groceryitem" + "(name VARCHAR(255)," + "StockType INT,"
			+ "Amount INT," + "CreationType INT," + "Date DATE DEFAULT NULL," + "PRIMARY KEY ( name))";
	String updateDrop = "drop table groceryitem;";
	String url = "jdbc:mysql://localhost:3306/SIFSDB";
	String queryInsert = "insert into groceryitem VALUES (?, ? , ?, ?, ?) " + "ON DUPLICATE KEY UPDATE amount = ?;";
	String select = "use SIFSDB";
	String selectFrom = "SELECT name, stocktype, amount, creationtype, date from groceryitem;";
	
	public GroceryRealDB() {
		
	}
	
	public void addGroceryItem(FridgeItem Fridge, String user, String password) {
		String type = "groceryitem";
		helper.adder(type, user, password, Fridge);
	}
	
	public List<StoredItem> loadGroceryItems(String user, String password) {
		List<StoredItem> grocery = new ArrayList<StoredItem>();
		try {
			Connection con = DriverManager.getConnection(url, user, password);
			Statement createState = con.createStatement();
			createState.execute(select);

			ResultSet rs = createState.executeQuery(selectFrom);

			while (rs.next()) {

				String Name = rs.getString(1);
				int stockEnum = rs.getInt(2);
				int amount = rs.getInt(3);
				int creatEnum = rs.getInt(4);
				LocalDate Date;
				if (rs.getDate(5) == null) {
					Date = null;
				} 
				else {
					Date = rs.getDate(5).toLocalDate();
				}
				FridgeItem item = helper.fridgeItemBuilder(Name, stockEnum, amount, creatEnum, Date);

				grocery.add(item);
			}

		}

		catch (SQLException e) {
			e.printStackTrace();
		}

		return grocery;
	}

	public void updateGroceryItems(GroceryList groceries, String user, String password) {
		try {
			Connection con = DriverManager.getConnection(url, user, password);
			Statement createState = con.createStatement();

			createState.executeUpdate(updateDrop);
			createState.executeUpdate(createGroceryTable);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (int x = 0; x < groceries.getItems().size(); x++) {
			FridgeItem itemToAdd = (FridgeItem) groceries.getItems().get(x);

			addGroceryItem(itemToAdd, user, password);

		}
		
		
	}
}
