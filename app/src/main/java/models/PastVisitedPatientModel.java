package models;

import java.io.Serializable;

/**
 * Created by ayaz on 23/6/17.
 */

public class PastVisitedPatientModel implements Serializable {
    private String patientName;
    private String RequestTime;
    private String ConsultId;
    private String ConsultMode;
    private String PatientId;
    private String ConsultTime;
    private String PaymentId;
    private String Amount;
    private String Coupon;
    private String Discount;
    private String Tax;
    private String PaymentMode;
    private String Gateway;
    private String ReferenceId;
    private String ServiceFees;
    private String PaymentStatus;


    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getRequestTime() {
        return RequestTime;
    }

    public void setRequestTime(String requestTime) {
        RequestTime = requestTime;
    }

    public String getConsultId() {
        return ConsultId;
    }

    public void setConsultId(String consultId) {
        ConsultId = consultId;
    }

    public String getConsultMode() {
        return ConsultMode;
    }

    public void setConsultMode(String consultMode) {
        ConsultMode = consultMode;
    }

    public String getPatientId() {
        return PatientId;
    }

    public void setPatientId(String patientId) {
        PatientId = patientId;
    }

    public String getConsultTime() {
        return ConsultTime;
    }

    public void setConsultTime(String consultTime) {
        ConsultTime = consultTime;
    }

    public String getPaymentId() {
        return PaymentId;
    }

    public void setPaymentId(String paymentId) {
        PaymentId = paymentId;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getCoupon() {
        return Coupon;
    }

    public void setCoupon(String coupon) {
        Coupon = coupon;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getTax() {
        return Tax;
    }

    public void setTax(String tax) {
        Tax = tax;
    }

    public String getPaymentMode() {
        return PaymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        PaymentMode = paymentMode;
    }

    public String getGateway() {
        return Gateway;
    }

    public void setGateway(String gateway) {
        Gateway = gateway;
    }

    public String getReferenceId() {
        return ReferenceId;
    }

    public void setReferenceId(String referenceId) {
        ReferenceId = referenceId;
    }

    public String getServiceFees() {
        return ServiceFees;
    }

    public void setServiceFees(String serviceFees) {
        ServiceFees = serviceFees;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        PaymentStatus = paymentStatus;
    }
}
