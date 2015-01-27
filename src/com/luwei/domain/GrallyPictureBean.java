package com.luwei.domain;

/**
 * 图片加载类
 * Created by luwei on 2015-1-4.
 */
public class GrallyPictureBean {
    String image_id;
    String patch;

    public GrallyPictureBean(String image_id, String patch) {
        this.image_id = image_id;
        this.patch = patch;
    }

    public String getImage_id() {
        return this.image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getPatch() {
        return this.patch;
    }

    public void setPatch(String patch) {
        this.patch = patch;
    }
}
