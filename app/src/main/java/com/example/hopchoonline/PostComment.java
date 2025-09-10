package com.example.hopchoonline;

import java.util.Date;

public class PostComment {
    private String id;
    private String userId;
    private String postId;

    private String content;
    public PostComment(String id, String userId, String postId, String content) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.content = content;
    }

    public PostComment(String userId, String postId, String content) {
        this.userId = userId;
        this.postId = postId;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getPostId() {
        return postId;
    }

    public String getContent() {
        return content;
    }
}
