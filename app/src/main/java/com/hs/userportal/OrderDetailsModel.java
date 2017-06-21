package com.hs.userportal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishabh on 20/6/17.
 */

public class OrderDetailsModel {

    private String orderID , centreName, testID , testName , billingAddress , orderDateTime;
    private int orderActualAmount , orderBillingAmount , orderDiscountAmount , orderStatus, promoCodeDiscount, discountPercentage ;
    private Boolean samplePickUpStatus ;
    private List<OrderTestNames> listOfOrderTestNames = new ArrayList<>() ;
    private OrderTestNames orderTestNamesObject ;

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

    public int getPromoCodeDiscount() {
        return promoCodeDiscount;
    }

    public void setPromoCodeDiscount(int promoCodeDiscount) {
        this.promoCodeDiscount = promoCodeDiscount;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
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
    }
}
