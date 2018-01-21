package com.example.android.csp.Fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.csp.PostMessage;
import com.example.android.csp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ChildEventListener mChildListener;

    List<MarkerOptions> mMarkerList;


    public MapFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);



        mDatabase = FirebaseDatabase.getInstance();
        mRef= mDatabase.getReference();

    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);


        mMarkerList = new ArrayList<MarkerOptions>();
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Log.d("DatabaseParent",dataSnapshot.toString());


                for(DataSnapshot data : dataSnapshot.getChildren()){

                    Log.d("DatabaseChild",data.toString());
                    PostMessage post= data.getValue(PostMessage.class);

                    mMarkerList.add(new MarkerOptions().position(new LatLng(post.getLatitude(),post.getLongitude())).title(post.getType()).snippet(post.getAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarkers_trimmed)) );

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mRef.addChildEventListener(mChildListener);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

               // Toast.makeText(getActivity(),"Inside OnMapReady",Toast.LENGTH_LONG).show();
                googleMap = mMap;

                // For showing a move to my location button

               try {
                   googleMap.setMyLocationEnabled(true);
               }catch (SecurityException e){
                   e.printStackTrace();
               }
                // For dropping a marker at a point on the Map
                LatLng mumbai = new LatLng(19.0760, 72.8777);
                googleMap.addMarker(new MarkerOptions().position(mumbai).title("Marker Title").snippet("Marker Description").icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarkers_trimmed)));

                for (MarkerOptions marker : mMarkerList){

                    googleMap.addMarker(marker);
                }
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(mumbai).zoom(7).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }
    /*

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
          //  inflater.inflate(R.menu.menu_chat_fragment, menu);
            super.onCreateOptionsMenu(menu, inflater);

    }
*/

    @Override
    public void onPause() {
        super.onPause();
        mRef.removeEventListener(mChildListener);
    }
}
