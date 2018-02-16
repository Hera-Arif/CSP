/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.csp;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.time.Instant;
import java.util.Map;

public class PostMessage {

      String postKey;
     String userId;

     String placeId;
     double latitude;
     double longitude;
     String name;
     String address;
     String photoUrl;
     String type;

     int numVerified;
    boolean isVerified;

    String creationDate;

    public PostMessage(){
    }
    

    public PostMessage(String postKey , String userId, String name,
                       String address, String photoUrl , String type,
                       String placeId, double latitude, double longitude, String creationDate) {
        this.postKey = postKey;

        this.address = address;
        this.name = name;
        this.userId= userId;
        this.photoUrl = photoUrl;
        this.type = type;

        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;

        this.creationDate= creationDate;
        this.isVerified= false;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getNumVerified() {
        return numVerified;
    }

    public void setNumVerified(int numVerified) {
        this.numVerified = numVerified;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }





    public String getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(String creationDate) {
        this.creationDate= creationDate;
    }
}
