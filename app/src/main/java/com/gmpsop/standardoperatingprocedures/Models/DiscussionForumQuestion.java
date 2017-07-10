package com.gmpsop.standardoperatingprocedures.Models;

import java.io.Serializable;

/**
 * Created by BD1 on 07-Jun-17.
 */

public class DiscussionForumQuestion implements Serializable{

    String id;
    String title;
    String question;
    String ans;
    String views;
    String tags;


    public DiscussionForumQuestion(String id, String title, String question, String ans, String views, String tags) {
        this.id = id;
        this.title = title;
        this.question = question;
        this.ans = ans;
        this.views = views;
        this.tags = tags;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getTags() { return tags; }

    public void setTags(String tags) {this.tags = tags; }

}


