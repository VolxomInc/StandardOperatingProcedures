package com.gmpsop.standardoperatingprocedures.Models;

/**
 * Created by BD1 on 05-May-17.
 */

public class ApplicationUser {

    private String fullName, userName, email, password;
    private int subscription, loginStatus;

    public ApplicationUser() {
        this.fullName = "";
        this.userName = "";
        this.email = "";
        this.password = "";
        this.subscription = 0;
        this.loginStatus = 0;
    }

    public ApplicationUser(String fullName, String userName,  String email, String password, int subscription, int loginStatus) {
        this.fullName = fullName;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.subscription = subscription;
        this.loginStatus = loginStatus;
    }

    public int getSubscription() {
        return subscription;
    }

    public void setSubscription(int subscription) {
        this.subscription = subscription;
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
