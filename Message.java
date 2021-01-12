package com.example.lab5;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Message {
    //Variables
    private String postID;
    private String post;
    private int votes;
    private ArrayList<Message> replies = new ArrayList<Message>();

    //Constructors
    public Message() {

    }

    public Message(String postID, String post, int votes) {
        this.postID = postID;
        this.post = post;
        this.votes = votes;
    }

    //Methods
    public String getPostID() {
        return this.postID;
    }

    public String getPost() {
        return this.post;
    }

    public int getVotes() {
        return this.votes;
    }

    public void upVote() {
        this.votes++;
    }

    public void downVote() {
        this.votes--;
    }

    public void replyToMessage(Message reply) {
        replies.add(reply);
    }
}
