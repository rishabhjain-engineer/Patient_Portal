package swipelist;

public class ItemRow {

	String testname,priceactual,discountprice;
	
	public ItemRow(String testname, String priceactual,String discountprice) {
		super();
		this.testname = testname;
		this.priceactual = priceactual;
		this.discountprice = discountprice;
	}
	
	
	public String getTestname() {
		return testname;
	}




	public void setTestname(String testname) {
		this.testname = testname;
	}




	public String getPriceactual() {
		return priceactual;
	}




	public void setPriceactual(String priceactual) {
		this.priceactual = priceactual;
	}




	public String getDiscountprice() {
		return discountprice;
	}




	public void setDiscountprice(String discountprice) {
		this.discountprice = discountprice;
	}





	
	
	
}
