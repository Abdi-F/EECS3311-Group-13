package domainLayer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FridgeItem extends StoredItem{
	
	private LocalDate expDate;
	
	public FridgeItem() {
		
	}
	
	public FridgeItem(FridgeItem item, LocalDate expDate) {
		this.setFoodItem(item.getFoodItem());
		this.setStockableItem(item.getStockableItem().copy());
		this.expDate = expDate;
	}
	
	@Override
	public void executeIncrement() {
		StockableItem stock = getStockableItem();
		stock.increment();
	}

	@Override
	public void executeDecrement() {
		StockableItem stock = getStockableItem();
		stock.decrement();
	}
	
	@Override
	public FridgeItem copy() {
		return new FridgeItem(this, this.expDate);
	}
	
	public void setExpDate(LocalDate expDate) {
		this.expDate = expDate;
		
	}
	public LocalDate getExpDate() {
			return this.expDate;
	}
	
	public boolean isExpiring() {
		if (LocalDate.now().until(this.expDate, ChronoUnit.WEEKS) < 1) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public String getDescription() {
		if (this.expDate == null)
			return super.getDescription();
		
		String desc = super.getDescription() + ", Exp: " + expDate.toString();
		if (this.isExpiring()) {
			desc += " [EXPIRY WARNING]";
		}
		return desc;
	}
}
