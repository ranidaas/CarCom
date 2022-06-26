package com.example.carcom;

import java.util.ArrayList;

public class User {
    public String email,fullName,license,uid;
    public ArrayList<String> users_i_messaged;

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public ArrayList<String> getUsers_i_messaged() {
        return users_i_messaged;
    }

    public void setUsers_i_messaged(ArrayList<String> users_i_messaged) {
        this.users_i_messaged = users_i_messaged;
    }

    public User(){

    }

    public User(String email, String fullName, String license, String uid){
        this.email=email;
        this.fullName=fullName;
        this.license=license;
        this.uid=uid;
        users_i_messaged = new ArrayList<String>();
    }
}
