package edu.unf.alloway.happybrain.models;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * Uses {@link com.google.gson.Gson} to map the JSON result returned
 * from the server into a Page object
 * */
public class Page {
    @SerializedName("Title")
    private String title;

    @SerializedName("Point_1")
    private String firstBullet;

    @SerializedName("Point_2")
    private String secondBullet;

    @SerializedName("Point_3")
    private String thirdBullet;

    @SerializedName("Message")
    private String message;

    @SerializedName("Reflect")
    private boolean isReflect;

    @SerializedName("URL")
    private String studyLink;

    @SerializedName("Filename")
    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public String getFirstBullet() {
        return firstBullet;
    }

    public String getSecondBullet() {
        return secondBullet;
    }

    public String getThirdBullet() {
        return thirdBullet;
    }

    public String getMessage() {
        return message.trim();
    }

    public boolean isReflect() {
        return isReflect;
    }

    public String getStudyLink() {
        return studyLink;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public static Page fromJson(String json) {
        return new GsonBuilder().create().fromJson(json, Page.class);
    }
}
