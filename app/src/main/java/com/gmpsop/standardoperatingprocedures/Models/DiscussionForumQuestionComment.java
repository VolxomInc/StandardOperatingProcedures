package com.gmpsop.standardoperatingprocedures.Models;

/**
 * Created by BD1 on 07-Jun-17.
 */

public class DiscussionForumQuestionComment {

    String comment;
    String question_id;
    String commented_by;


    public DiscussionForumQuestionComment(String comment, String question_id, String commented_by) {
        this.comment = comment;
        this.question_id = question_id;
        this.commented_by = commented_by;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getQuestionId() {
        return question_id;
    }

    public void setQuestionId(String question_id) {
        this.question_id = question_id;
    }

    public String getCommentedBy() {
        return commented_by;
    }

    public void setCommentedBy(String views) {
        this.commented_by = commented_by;
    }
}


