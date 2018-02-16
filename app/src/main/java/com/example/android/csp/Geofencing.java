package com.example.android.csp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mapkar on 1/31/2018.
 */

// TODO (1) Create a Geofencing class with a Context and GoogleApiClient constructor that
// initializes a private member ArrayList of Geofences called mGeofenceList


public class Geofencing implements ResultCallback<Result> {


    private static final long GEOFENCE_EXPIRY_DURATION = TimeUnit.HOURS.toMillis(24);
    private static final int GEOFENCE_REQ_CODE = 1204;
    Context mContext;
    ArrayList<Geofence> mGeofenceList;

    PendingIntent mGeofencePendingIntent;

    GoogleApiClient mGoogleApiClient;

    GeofencingClient mGeofencingClient;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ChildEventListener mChildListener;

    private final String TAG = "Geofencing";

    public Geofencing(Context Context) {
        this.mContext = Context;
        this.mGeofenceList = new ArrayList<>();

        mGeofencePendingIntent = null;
        this.mGoogleApiClient = null;

        mGeofencingClient = new GeofencingClient(Context);

        mDatabase = FirebaseDatabase.getInstance();
        mRef= mDatabase.getReference();
    }

    public Geofencing(Context Context, GoogleApiClient GoogleApiClient) {
        this.mContext = Context;
        this.mGeofenceList = new ArrayList<>();

        mGeofencePendingIntent = null;
        this.mGoogleApiClient = GoogleApiClient;
    }

    // TODO (2) Inside Geofencing, implement a public method called updateGeofencesList that
// given a PlaceBuffer will create a Geofence object for each Place using Geofence.Builder
// and add that Geofence to mGeofenceList

    public void updateGeofencesList() {




        mGeofenceList = new ArrayList<>();

        Log.d ("Never Ending Service", "inside updateGeofencesList ref"+mRef.toString());
        //mChildListener =
        //mRef.addChildEventListener(mChildListener);

        mRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Never Ending Service", "inside updateGeofences list onChildAdded");

                Log.d("DatabaseParent", dataSnapshot.toString());


                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    Log.d("DatabaseChild", data.toString());
                    PostMessage post = data.getValue(PostMessage.class);

                    //mMarkerList.add(new MarkerOptions().position(new LatLng(post.getLatitude(),post.getLongitude())).title(post.getType()).snippet(post.getAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarkers_trimmed)) );


                    Geofence.Builder builder = new Geofence.Builder();

                    Geofence geofence = builder.setRequestId(post.getPlaceId())
                            .setExpirationDuration(GEOFENCE_EXPIRY_DURATION)
                            .setCircularRegion(post.getLatitude(), post.getLongitude(), 100)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build();

                    mGeofenceList.add(geofence);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d ("Never Ending Service", "inside updateGeofences list size:"+mGeofenceList.size());

    }
    public void updateGeofencesList(PlaceBuffer places) {

        Log.i(TAG,"Inside updateGeofencesList - places count:"+places.getCount()+"Status code:"+places.getStatus().getStatusCode()+" status in string"+places.getStatus().toString());

        if (places == null || places.getCount() == 0) {
            return;
        }
        mGeofenceList = new ArrayList<>();
        for (Place place : places) {

            String placeId = place.getId();
            double placeLat = place.getLatLng().latitude;
            double placeLong = place.getLatLng().longitude;


            Geofence.Builder builder = new Geofence.Builder();

            Geofence geofence = builder.setRequestId(placeId)
                    .setExpirationDuration(GEOFENCE_EXPIRY_DURATION)
                    .setCircularRegion(placeLat, placeLong, 100)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            mGeofenceList.add(geofence);


        }
    }

    // TODO (3) Inside Geofencing, implement a private helper method called getGeofencingRequest that
// uses GeofencingRequest.Builder to return a GeofencingRequest object from the Geofence list
    private GeofencingRequest getGeofencingRequest() {
      /*  GeofencingRequest req = null;
        try {
             req= new GeofencingRequest.Builder().addGeofences(mGeofenceList)
                    .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build();
        }catch (Exception e){
            e.printStackTrace();
        } */
        return new GeofencingRequest.Builder().addGeofences(mGeofenceList)
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();
    }


// TODO (4) Create a GeofenceBroadcastReceiver class that extends BroadcastReceiver and override
// onReceive() to simply log a message when called. Don't forget to add a receiver tag in the Manifest

// TODO (5) Inside Geofencing, implement a private helper method called getGeofencePendingIntent that
// returns a PendingIntent for the GeofenceBroadcastReceiver class

    private PendingIntent getGeofencePendingIntent() {

        if (mGeofencePendingIntent != null) return mGeofencePendingIntent;

      //  Intent intent = new Intent(mContext, GeofenceBroadcastReciever.class);

        Intent intent = new Intent(mContext, GeofenceIntentService.class);
        intent.setAction(ReminderTasks.ACTION_CREATE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getService(mContext,
                GEOFENCE_REQ_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

// TODO (6) Inside Geofencing, implement a public method called registerAllGeofences that
// registers the GeofencingRequest by calling LocationServices.GeofencingApi.addGeofences
// using the helper functions getGeofencingRequest() and getGeofencePendingIntent()

    public void registerAllGeofences() {

        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient == null || !mGoogleApiClient.isConnected() || mGeofenceList == null || mGeofenceList.size() == 0) {
                return;
            }
            try {
                LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(), getGeofencePendingIntent()).setResultCallback(this);

            } catch (SecurityException se) {
                Log.e(TAG, se.getMessage());
            }

            Log.i(TAG, "Inside registerAllGeofences - geofence list count:" + mGeofenceList.size());
        }else if(mGeofencingClient!=null){
            try {
                mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                        .addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Geofences added
                                // ...
                                Log.d ("Never Ending Service", "inside registerAllGeofences- Success");
                            }
                        })
                        .addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to add geofences
                                // ...
                                Log.d ("Never Ending Service", "inside registerAllGeofences- Failure");
                            }
                        });
            }catch (SecurityException se) {
                Log.e(TAG, se.getMessage());
            }
        }
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.e(TAG,"onResult: "+result.getStatus().toString());
    }

// TODO (7) Inside Geofencing, implement a public method called unRegisterAllGeofences that
// unregisters all geofences by calling LocationServices.GeofencingApi.removeGeofences
// using the helper function getGeofencePendingIntent()
public void unRegisterAllGeofences() {
    if(mGoogleApiClient == null || mGoogleApiClient.isConnected()){
        return;
    }
    try{
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,mGeofencePendingIntent).setResultCallback(this);

    }catch (SecurityException se){
        Log.e(TAG,se.getMessage());
    }
}

// TODO (8) Create a new instance of Geofencing using "this" as the context and mClient as the client



}
