/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.csp.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.example.android.csp.DisplayPost;
import com.example.android.csp.MainActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {


    final static String BASE_URL ="http://192.168.0.101:5000/";
           // "http://(ipaddress ipv4):5000/search/";

    final static String SAVE_URL = BASE_URL+"save";
    final static String CLASSIFY_URL = BASE_URL+"classify_image";
    final static String IMAGE_REQ_URL = BASE_URL+"get_image";
    public static final int GET_CLASSIFIER_RESPONSE = 10111 ;
    public static final int GET_IMAGE_RESPONSE = 20111 ;

    public static URL buildUrl() {
        Uri builtUri = Uri.parse(BASE_URL);


        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    public static URL buildUrl(String strUrl) {
        Uri builtUri = Uri.parse(strUrl);


        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static Object getResponseFromHttpUrl(URL url , int reqId) throws IOException {

        if(reqId==GET_CLASSIFIER_RESPONSE){
           return postImageAndClassifierResponse(url);
        }else if(reqId==GET_IMAGE_RESPONSE){

         return getSavedImageReponse(url);
        }

        return null;
    }

    private static Bitmap getSavedImageReponse(URL url) {

        Bitmap resp;
        try {
          /*  InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
*/

            // resp = runClassifier(url);
            resp = runImageDownloader();

        } catch(Exception e) {
            //  urlConnection.disconnect();
            resp=null;
        }

        return resp;
    }



    public static String  postImageAndClassifierResponse(URL url)  {
       // HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String resp;
        try {
          /*  InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
*/

         // resp = runClassifier(url);
            resp = runClassifier();

        } catch(Exception e) {
          //  urlConnection.disconnect();
            resp=null;
        }

        return resp;
    }


    static String runClassifier() throws IOException {

        // Post image to flask server
        URL url = buildUrl(CLASSIFY_URL);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("image/jpeg"), DisplayPost.mPhotoFile) )
                .build();


        Response response = client.newCall(request).execute();

        // GET request for data obtained after image Classification
      /*  url = buildUrl(CLASSIFY_URL);

        request = new Request.Builder()
                .url(url)
                .get()
                .build();


        response = client.newCall(request).execute();
        */
        String s= response.body().string();

        return s;
    }



    static Bitmap runImageDownloader() throws IOException {

        // Post image to flask server
        URL url = buildUrl(IMAGE_REQ_URL);
        OkHttpClient client = new OkHttpClient();
       /* Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("image/jpeg"), DisplayPost.mPhotoFile) )
                .build();
*/
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();



        Response response = client.newCall(request).execute();

        // GET request for data obtained after image Classification
      /*  url = buildUrl(CLASSIFY_URL);

        request = new Request.Builder()
                .url(url)
                .get()
                .build();


        response = client.newCall(request).execute();
        */
       // BitmapFactory.decodeStream()
        Bitmap bitmap=  BitmapFactory.decodeStream(response.body().byteStream() );

        return bitmap;
    }
}