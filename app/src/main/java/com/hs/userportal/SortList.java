package com.cloudchowk.patient;

/**
 * Created by rahul2 on 08-Apr-15.
 */
public class SortList {
	private String name;
	private String area;
	private String centerID;
	private String rating;
	private String discount;
	private String avail;
	private String TieUpWithLab;

	public String getCompeteAddress() {
		return competeAddress;
	}

	public void setCompeteAddress(String competeAddress) {
		this.competeAddress = competeAddress;
	}

	private String competeAddress;
	Float coordinates;
	Double lat, lng;
	boolean parking, seating, washroom, drinking, homeColl, openNow, twentyFour, Our;

	public SortList() {

	}

	public SortList(Float coordinates, String name, String rating, String area, String centerID, Double lat, Double lng,
			boolean parking, boolean seating, boolean drinking, boolean washroom, boolean homeColl, boolean openNow,
			String discount) {
		this.name = name;
		this.area = area;
		this.coordinates = coordinates;
		this.centerID = centerID;
		this.lat = lat;
		this.lng = lng;
		this.rating = rating;
		this.seating = seating;
		this.washroom = washroom;
		this.seating = seating;
		this.drinking = drinking;
		this.homeColl = homeColl;
		this.openNow = openNow;
		this.discount = discount;
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

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public boolean isOpenNow() {
		return openNow;
	}

	public void setOpenNow(boolean openNow) {
		this.openNow = openNow;
	}

	public boolean isHomeColl() {
		return homeColl;
	}

	public void setHomeColl(boolean homeColl) {
		this.homeColl = homeColl;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public boolean isParking() {
		return parking;
	}

	public void setParking(boolean parking) {
		this.parking = parking;
	}

	public boolean isSeating() {
		return seating;
	}

	public void setSeating(boolean seating) {
		this.seating = seating;
	}

	public boolean isWashroom() {
		return washroom;
	}

	public String getTieUpWithLab() {
		return TieUpWithLab;
	}

	public void setTieUpWithLab(String tieUpWithLab) {
		TieUpWithLab = tieUpWithLab;
	}

	public void setWashroom(boolean washroom) {
		this.washroom = washroom;
	}

	public boolean isDrinking() {
		return drinking;
	}

	public void setDrinking(boolean drinking) {
		this.drinking = drinking;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Float getCo() {
		return coordinates;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public void setCo(Float coordinates) {
		this.coordinates = coordinates;
	}

	public void setCenterID(String centerID) {
		this.centerID = centerID;
	}

	public String getCenterID() {
		return centerID;
	}

	public String getAvail() {
		return avail;
	}

	public void setAvail(String avail) {
		this.avail = avail;
	}

}
