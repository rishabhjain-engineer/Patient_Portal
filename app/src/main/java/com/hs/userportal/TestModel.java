package com.cloudchowk.patient;

public class TestModel {

	private String labName;
	private String Price;
	private String Discount;
	private String FinalPrice;
	private String rating;
	private String distance;
	private String area;
	private String totalTest;
	private String centreTest;
	private String discountRuppes;
	private String TieUpWithLab;

	public String getCompleteAddress() {
		return completeAddress;
	}

	public void setCompleteAddress(String completeAddress) {
		this.completeAddress = completeAddress;
	}

	private String completeAddress;
	public String getDiscountRuppes() {
		return discountRuppes;
	}

	public void setDiscountRuppes(String discountRuppes) {
		this.discountRuppes = discountRuppes;
	}

	boolean homeColl, openNow, twentyFour, Our;

	public boolean isHomeColl() {
		return homeColl;
	}

	public void setHomeColl(boolean homeColl) {
		this.homeColl = homeColl;
	}

	public boolean isOpenNow() {
		return openNow;
	}

	public void setOpenNow(boolean openNow) {
		this.openNow = openNow;
	}

	public boolean isTwentyFour() {
		return twentyFour;
	}

	public void setTwentyFour(boolean twentyFour) {
		this.twentyFour = twentyFour;
	}

	public boolean isOur() {
		return Our;
	}

	public void setOur(boolean our) {
		Our = our;
	}

	public TestModel() {

	}

	public String getTotalTest() {
		return totalTest;
	}

	public void setTotalTest(String totalTest) {
		this.totalTest = totalTest;
	}

	public String getCentreTest() {
		return centreTest;
	}

	public void setCentreTest(String centreTest) {
		this.centreTest = centreTest;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getLabName() {
		return labName;
	}

	public void setLabName(String labName) {
		this.labName = labName;
	}

	public String getPrice() {
		return Price;
	}

	public void setPrice(String price) {
		Price = price;
	}

	public String getTieUpWithLab() {
		return TieUpWithLab;
	}

	public void setTieUpWithLab(String tieUpWithLab) {
		TieUpWithLab = tieUpWithLab;
	}

	public String getDiscount() {
		return Discount;
	}

	public void setDiscount(String discount) {
		Discount = discount;
	}

	public String getFinalPrice() {
		return FinalPrice;
	}

	public void setFinalPrice(String finalPrice) {
		FinalPrice = finalPrice;
	}

}
