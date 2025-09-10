package com.example.hopchoonline;

public class Rating {
    int id;
    String voteUserId;
    String receivedUserId;
    int point;
    String content;

    public Rating(String voteUserId, String receivedUserId, int point, String content) {
        this.voteUserId = voteUserId;
        this.receivedUserId = receivedUserId;
        this.point = point;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVoteUserId() {
        return voteUserId;
    }

    public void setVoteUserId(String voteUserId) {
        this.voteUserId = voteUserId;
    }

    public String getReceivedUserId() {
        return receivedUserId;
    }

    public void setReceivedUserId(String receivedUserId) {
        this.receivedUserId = receivedUserId;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
