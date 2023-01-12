package com.example.hwada.Model;

public class Report {
    private String id ;
    private String postId;
    private String type;
    private String category;
    private String subCategory;
    private String subSubCategory;

    public Report() {
    }

    public Report(String id, String postId, String type, String category, String subCategory, String subSubCategory) {
        this.id = id;
        this.postId = postId;
        this.type = type;
        this.category = category;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
    }

    public Report(String postId, String type, String category, String subCategory, String subSubCategory) {
        this.postId = postId;
        this.type = type;
        this.category = category;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
    }

    public String getId() {
        return id;
    }

    public String getPostId() {
        return postId;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public String getSubSubCategory() {
        return subSubCategory;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public void setSubSubCategory(String subSubCategory) {
        this.subSubCategory = subSubCategory;
    }
}
