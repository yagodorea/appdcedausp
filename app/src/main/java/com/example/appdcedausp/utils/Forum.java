package com.example.appdcedausp.utils;

public class Forum {

    private String forum_name, forum_description, forum_image;
    private int forum_posts;

    public Forum() {}

    public Forum(String name, String description, String image, int posts) {
        forum_name = name;
        forum_description = description;
        forum_image = image;
        forum_posts = posts;
    }

    public String getForum_name() {
        return forum_name;
    }
    public String getForum_description() {
        return forum_description;
    }
    public String getForum_image() {
        return forum_image;
    }
    public int getForum_posts() {
        return forum_posts;
    }
}
