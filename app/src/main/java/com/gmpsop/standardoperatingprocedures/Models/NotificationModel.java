package com.gmpsop.standardoperatingprocedures.Models;

/**
 * Created by BD1 on 07-Jun-17.
 */

public class NotificationModel {

    int id;
    String notification_text;
    int receiving_status;
    long notification_date;
    String email;
    int reading_status;

    public long getNotification_date() {
        return notification_date;
    }

    public void setNotification_date(long notification_date) {
        this.notification_date = notification_date;
    }

    //    String notification_date,
    public NotificationModel(int id , String notification_text,long notification_date , String email, int reading_status , int receiving_status) {
        this.id = id;
        this.notification_text = notification_text;
        this.notification_date = notification_date;
        this.email = email;
        this.reading_status = reading_status;
        this.receiving_status = receiving_status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReceiving_status() {
        return receiving_status;
    }

    public void setReceiving_status(int receiving_status) {
        this.receiving_status = receiving_status;
    }

    public int getReading_status() {
        return reading_status;
    }

    public void setReading_status(int reading_status) {
        this.reading_status = reading_status;
    }

    public String getNotificationText() {
        return notification_text;
    }

    public void setNotificationText(String notification_text) {
        this.notification_text = notification_text;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String question_id) {
        this.email = email;
    }

}


