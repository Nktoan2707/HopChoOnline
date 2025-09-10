package com.example.hopchoonline;

public class User {
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String address;
    private String phone;
    private boolean isPriority;
    private double rating;
    private String avatarUrl = "";

    public User() {
    }

    public User(String id,String username,String password,String fullname,String address,String phone,double rating, String avatarUrl){
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullname;
        this.address  = address;
        this.phone = phone;
        this.rating = rating;
        this.avatarUrl = avatarUrl;
    }
    public User(String id, String username, String password, String fullName, String address, String phone, boolean isPriority, double rating, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.isPriority = isPriority;
        this.rating = rating;
        this.avatarUrl = avatarUrl;
    }

    public User(String address, boolean isPriority, double rating) {
        this.address = address;
        this.isPriority = isPriority;
        this.rating = rating;
    }

    public User(String username, String password, String fullName, String address) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.address = address;
    }

    public User(String id, String username, String password, String fullName, String address) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.address = address;
    }

    public User(String id, String username, String password, String fullName, String address, double rating) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.address = address;
        this.rating = rating;
    }

    public User(String id, String username, String password, String fullName, String address, String phone, double rating) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.rating = rating;
    }

    public User(String id, String username, String password, String fullName, String address, String phone, boolean isPriority, double totalRating) {
        this.id = id;
        this.rating = totalRating;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.isPriority = isPriority;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isPriority() {
        return isPriority;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
    public  void setAvatarUrl(String avatarUrl){ this.avatarUrl = avatarUrl;};
}
