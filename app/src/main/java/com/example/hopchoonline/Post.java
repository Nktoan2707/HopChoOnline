package com.example.hopchoonline;

public class Post {
    private String id;
    private String title;
    private int price;
    private String description;
    private String imageUrl;
    private String author;
    private boolean isBuy;
    private String location;
    private String date;
    private String duration;

    public Post(String id,String title, int price, String description, boolean isBuy, String imageUrl, String author, String location) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.isBuy = isBuy;
        this.imageUrl = imageUrl;
        this.author = author;
        this.location = location;
    }
    public Post(String id, String title, String imageUrl, int price, boolean isBuy, String description, String location, String author){
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price;
        this.isBuy = isBuy;
        this.description = description;
        this.location = location;
        this.author = author;
    }
    public Post(String id, String title, String imageUrl, int price, boolean isBuy, String description, String location, String author, String date, String duration) {

        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price;
        this.isBuy = isBuy;
        this.description = description;
        this.location = location;
        this.author = author;
        this.date = date;
        this.duration = duration;
    }

    public Post(String title, String imageUrl, int price, boolean isBuy, String description, String location, String author, String date, String duration) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price;
        this.isBuy = isBuy;
        this.description = description;
        this.location = location;
        this.author = author;
        this.date = date;
        this.duration = duration;
    }

    public Post(String id, String title, int price, String description, String imageUrl, String author, boolean isBuy, String location, String date, String duration) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.author = author;
        this.isBuy = isBuy;
        this.location = location;
        this.date = date;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public String getDescription() {
        return description;
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

    public boolean isBuy() {
        return isBuy;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}