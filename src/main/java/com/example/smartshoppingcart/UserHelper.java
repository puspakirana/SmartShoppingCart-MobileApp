package com.example.smartshoppingcart;

public class UserHelper {

    String cust_name, cust_id, cust_password;
    int cust_balance, NoPurchase;

    public UserHelper() {

    }

    public UserHelper(String cust_name, String cust_id, String cust_password, int cust_balance, int NoPurchase) {
        this.cust_name = cust_name;
        this.cust_id = cust_id;
        this.cust_password = cust_password;
        this.cust_balance = cust_balance;
        this.NoPurchase = NoPurchase;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

    public String getCust_password() {
        return cust_password;
    }

    public void setCust_password(String cust_password) {
        this.cust_password = cust_password;
    }

    public int getCust_balance() {
        return cust_balance;
    }

    public void setCust_balance(int cust_balance) {
        this.cust_balance = cust_balance;
    }

    public int getNoPurchase() {
        return NoPurchase;
    }

    public void setNoPurchase(int NoPurchase) {
        this.NoPurchase = NoPurchase;
    }
}
