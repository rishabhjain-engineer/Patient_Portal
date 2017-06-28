package com.hs.userportal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import utils.DateSorter;

/**
 * Created by rishabh on 20/6/17.
 */

public class OrderDetailsModel implements DateSorter{

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");

    private String orderID , centreName, testID , testName , billingAddress , orderDateTime, discountPercentage, promoCodeDiscount;
    private int orderActualAmount , orderBillingAmount , orderDiscountAmount , orderStatus ;
    private Boolean samplePickUpStatus ;
    private List<OrderTestNames> listOfOrderTestNames = new ArrayList<>() ;
    private OrderTestNames orderTestNamesObject ;
    private Date orderIdDate;

    public Date getOrderIdDate() {
        return orderIdDate;
    }

    public void setOrderIdDate(Date orderIdDate) {
        this.orderIdDate = orderIdDate;
    }

    public OrderTestNames getOrderTestNames() {
        return orderTestNamesObject;
    }

    public OrderDetailsModel(String orderID) {
        this.orderID = orderID ;
        orderTestNamesObject = new OrderTestNames() ;
        listOfOrderTestNames.add(orderTestNamesObject) ;

    }

    public String getOrderID() {
        return orderID;
    }


    public String getCentreName() {
        return centreName;
    }

    public void setCentreName(String centreName) {
        this.centreName = centreName;
    }

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }


    public List<OrderTestNames> getListOfOrderTestNames() {
        return listOfOrderTestNames;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public int getOrderActualAmount() {
        return orderActualAmount;
    }

    public void setOrderActualAmount(int orderActualAmount) {
        this.orderActualAmount = orderActualAmount;
    }

    public int getOrderBillingAmount() {
        return orderBillingAmount;
    }

    public void setOrderBillingAmount(int orderBillingAmount) {
        this.orderBillingAmount = orderBillingAmount;
    }

    public int getOrderDiscountAmount() {
        return orderDiscountAmount;
    }

    public void setOrderDiscountAmount(int orderDiscountAmount) {
        this.orderDiscountAmount = orderDiscountAmount;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPromoCodeDiscount() {
        return promoCodeDiscount;
    }

    public void setPromoCodeDiscount(String promoCodeDiscount) {
        this.promoCodeDiscount = promoCodeDiscount;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Boolean getSamplePickUpStatus() {
        return samplePickUpStatus;
    }

    public void setSamplePickUpStatus(Boolean samplePickUpStatus) {
        this.samplePickUpStatus = samplePickUpStatus;
    }

    public String getOrderDateTime() {

        return orderDateTime;

    }

    public void createNewTestNameObject(){
        orderTestNamesObject = new OrderTestNames();
        listOfOrderTestNames.add(orderTestNamesObject) ;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
        try {
            orderIdDate = formatter.parse(orderDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Date getDate() {
        return orderIdDate;
    }
}
