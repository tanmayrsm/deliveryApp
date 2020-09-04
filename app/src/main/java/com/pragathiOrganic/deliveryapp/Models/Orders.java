package com.pragathiOrganic.deliveryapp.Models;

public class Orders {
    public String bill_no, number, total_amount, total_discount, total_deliveryamy, total_item, address, order_status, payment_status;
    public Orders(){}

    public Orders(String bill_no, String number, String total_amount, String total_discount, String total_deliveryamy, String total_item, String address, String order_status, String payment_status) {
        this.bill_no = bill_no;
        this.number = number;
        this.total_amount = total_amount;
        this.total_discount = total_discount;
        this.total_deliveryamy = total_deliveryamy;
        this.total_item = total_item;
        this.address = address;
        this.order_status = order_status;
        this.payment_status = payment_status;
    }

    public String getBill_no() {
        return bill_no;
    }

    public void setBill_no(String bill_no) {
        this.bill_no = bill_no;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getTotal_discount() {
        return total_discount;
    }

    public void setTotal_discount(String total_discount) {
        this.total_discount = total_discount;
    }

    public String getTotal_deliveryamy() {
        return total_deliveryamy;
    }

    public void setTotal_deliveryamy(String total_deliveryamy) {
        this.total_deliveryamy = total_deliveryamy;
    }

    public String getTotal_item() {
        return total_item;
    }

    public void setTotal_item(String total_item) {
        this.total_item = total_item;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }
}
