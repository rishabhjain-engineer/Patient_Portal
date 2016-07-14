package com.hs.userportal;

/**
 * Created by ashish on 10/27/2015.
 */
public class OrderList {

    private String OrderhistoryId;
    private String UserId;
    private String OrderId;
    private String CentreName;
    private String TestId;
    private String TestName;
    private String OrderDateTime;
    private String OrderActualAmount;
    private String OrderDiscount;
    private String OrderBillingAmount;
    private String BillingAddress;
    private String PromoCodeDiscount;
    private String DiscountInPercentage;
    private String str_peractual_amnt;
    private String OrderStatus;
    private String SamplePickupstatus;

    public OrderList() {
    }

    public OrderList(String orderhistoryId, String userId, String orderId, String centreName, String testId, String testName, String orderDateTime, String orderActualAmount, String orderDiscount, String orderBillingAmount, String billingAddress, String PromoCodeDiscount) {
        this.OrderhistoryId = orderhistoryId;
        this.UserId = userId;
        this.OrderId = orderId;
        this.CentreName = centreName;
        this.TestId = testId;
        this.TestName = testName;
        this.OrderDateTime = orderDateTime;
        this.OrderActualAmount = orderActualAmount;
        this.OrderDiscount = orderDiscount;
        this.OrderBillingAmount = orderBillingAmount;
        this.BillingAddress = billingAddress;
        this.PromoCodeDiscount = PromoCodeDiscount;
    }

    public String getTestName() {
        return TestName;
    }

    public void setTestName(String testName) {
        TestName = testName;
    }

    public String getOrderhistoryId() {
        return OrderhistoryId;
    }

    public void setOrderhistoryId(String orderhistoryId) {
        OrderhistoryId = orderhistoryId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getCentreName() {
        return CentreName;
    }

    public void setCentreName(String centreName) {
        CentreName = centreName;
    }

    public String getTestId() {
        return TestId;
    }

    public void setTestId(String testId) {
        TestId = testId;
    }

    public String getOrderDateTime() {
        return OrderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        OrderDateTime = orderDateTime;
    }

    public String getOrderDiscount() {
        return OrderDiscount;
    }

    public void setOrderDiscount(String orderDiscount) {
        OrderDiscount = orderDiscount;
    }

    public String getOrderActualAmount() {
        return OrderActualAmount;
    }

    public String getDiscountInPercentage() {
        return DiscountInPercentage;
    }

    public void setDiscountInPercentage(String discountInPercentage) {
        DiscountInPercentage = discountInPercentage;
    }

    public String getStr_peractual_amnt() {
        return str_peractual_amnt;
    }

    public void setStr_peractual_amnt(String str_peractual_amnt) {
        this.str_peractual_amnt = str_peractual_amnt;
    }

    public String getPromoCodeDiscount() {
        return PromoCodeDiscount;
    }

    public void setPromoCodeDiscount(String promoCodeDiscount) {
        PromoCodeDiscount = promoCodeDiscount;
    }

    public void setOrderActualAmount(String orderActualAmount) {
        OrderActualAmount = orderActualAmount;
    }

    public String getOrderBillingAmount() {
        return OrderBillingAmount;
    }

    public void setOrderBillingAmount(String orderBillingAmount) {
        OrderBillingAmount = orderBillingAmount;
    }

    public String getBillingAddress() {
        return BillingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        BillingAddress = billingAddress;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getSamplePickupstatus() {
        return SamplePickupstatus;
    }

    public void setSamplePickupstatus(String samplePickupstatus) {
        SamplePickupstatus = samplePickupstatus;
    }

}
