package com.example.android.csp;

/**
 * Created by Mapkar on 1/17/2018.
 */

public class AuthorityPostUpdateMessage extends PostMessage {

    String updatedPhotoUrl;

    public AuthorityPostUpdateMessage(){
    }


    public AuthorityPostUpdateMessage(String postKey , String userId, String name, String address, String photoUrl , String type, String placeId, double latitude, double longitude,  String updatedPhotoUrl) {
        this.postKey = postKey;

        this.address = address;
        this.name = name;
        this.userId= userId;
        this.photoUrl = photoUrl;
        this.type = type;

        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;

        this.updatedPhotoUrl= updatedPhotoUrl;
    }

    public AuthorityPostUpdateMessage(PostMessage post){
        this.postKey = post.postKey;

        this.address = post.address;
        this.name = post.name;
        this.userId= post.userId;
        this.photoUrl = post.photoUrl;
        this.type = post.type;

        this.placeId = post.placeId;
        this.latitude = post.latitude;
        this.longitude = post.longitude;
        this.isVerified= post.isVerified;
        this.creationDate= post.creationDate;
        this.updatedPhotoUrl= "null";


    }

    public String getUpdatedPhotoUrl() {
        return updatedPhotoUrl;
    }

    public void setUpdatedPhotoUrl(String updatedPhotoUrl) {
        this.updatedPhotoUrl = updatedPhotoUrl;
    }
}
