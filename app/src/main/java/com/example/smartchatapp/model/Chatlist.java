package com.example.smartchatapp.model;

public class Chatlist {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    Chatlist(){

    }

    Chatlist(String id){
        this.id=id;
    }
}
