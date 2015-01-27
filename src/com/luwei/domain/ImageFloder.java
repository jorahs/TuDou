package com.luwei.domain;

/**
 * 相册中图片集
 *
 * @author len
 */
public class ImageFloder {
    private String topImagePath;

    private String folderName;

    private int imageCounts;

    private  String topImageId;

    public void setTopImageId(String topImageId){
        this.topImageId = topImageId;
    }

    public String getTopImageId(){
        return  this.topImageId;
    }

    public String getTopImagePath() {
        return topImagePath;
    }

    public void setTopImagePath(String topImagePath) {
        this.topImagePath = topImagePath;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getImageCounts() {
        return imageCounts;
    }

    public void setImageCounts(int imageCounts) {
        this.imageCounts = imageCounts;
    }

}
