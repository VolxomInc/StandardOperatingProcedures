package com.gmpsop.standardoperatingprocedures.Models;

/**
 * Created by BD1 on 07-Jun-17.
 */

public class NotificationModel {

    String notification_text;
//    String notification_date;
    String email;
    int status;


//    String notification_date,
    public NotificationModel(String notification_text, String email, int status) {
        this.notification_text = notification_text;
//        this.notification_date = notification_date;
        this.email = email;
        this.status = status;
    }

    public String getNotificationText() {
        return notification_text;
    }

    public void setNotificationText(String notification_text) {
        this.notification_text = notification_text;
    }

//    public String getNotificationDate() {
//        return notification_date;
//    }
//
//    public void setNotificationDate(String notification_date) {
//        this.notification_date = notification_date;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String question_id) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}


