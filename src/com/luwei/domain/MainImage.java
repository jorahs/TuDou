package com.luwei.domain;

import java.io.Serializable;

/**
 * @author luwei 主Image ListView 需求的bean
 */
public class MainImage implements Serializable {
    private static final long serialVersionUID = 1231L;
    private int id;
    private String avatar_url;
    private String name;
    private String time;
    private String sort;
    private String image_url;
    private String context;
    private int screenWidth;

    public MainImage(int id, String avatar_url, String name, String time,
                     String sort, String image_url, String context, int screenWidth) {
        super();
        this.screenWidth = screenWidth;
        this.id = id;
        this.avatar_url = avatar_url;
        this.name = name;
        this.time = time;
        this.sort = sort;
        this.image_url = image_url;
        this.context = context;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    @Override
    public String toString() {
        return "MainImage [id=" + id + ", avatar_url=" + avatar_url + ", name="
                + name + ", time=" + time + ", sort=" + sort + ", image_url="
                + image_url + ", context=" + context + "]";
    }


}
