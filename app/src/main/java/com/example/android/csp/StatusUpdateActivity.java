package com.example.android.csp;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatusUpdateActivity extends AppCompatActivity {

    TextView mTextViewPrev;
    TextView mTextViewNew;
    ImageView mImageViewPrev;
    ImageView mImageViewNew;
    Button mCaptureImageButton;
    Button mUpdateImageButton;

    FirebaseStorage mFirebaseStorage;

    StorageReference mStorageReference;

    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    DatabaseReference typeRef;
    DatabaseReference mStatusUpdateRef;

    Uri mPhotoUri;
    private final int RC_CAMERA_CAPTURE= 2777;

    AuthorityPostUpdateMessage currentPost;

    ProgressBar mProgressBar;

    String type;

    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_update);
        final String key = getIntent().getExtras().getString("key");
        type = getIntent().getExtras().getString("type");
        Toast.makeText(this,key,Toast.LENGTH_LONG).show();

        mTextViewPrev= (TextView)findViewById(R.id.tv_su_prev);
        mTextViewNew= (TextView)findViewById(R.id.tv_su_new);
        mImageViewNew= (ImageView)findViewById(R.id.iv_su_new);
        mImageViewPrev= (ImageView)findViewById(R.id.iv_su_prev);
        mCaptureImageButton= (Button)findViewById(R.id.btn_su_capture_image);
        mUpdateImageButton= (Button)findViewById(R.id.btn_su_upload_image);

        mProgressBar = (ProgressBar)findViewById(R.id.update_progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mTextViewNew.setVisibility(View.INVISIBLE);
        mImageViewNew.setVisibility(View.INVISIBLE);
        mUpdateImageButton.setVisibility(View.INVISIBLE);

        mDatabase = FirebaseDatabase.getInstance();

        mRef = mDatabase.getReference();
        //DatabaseReference potholeRef = mRef.child("Pothole");
        //DatabaseReference garbageRef = mRef.child("Garbage");
        typeRef= mRef;

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("Images");

        String uriString = getIntent().getExtras().getString("originalPhotoUri");
        Glide.with(StatusUpdateActivity.this).load(Uri.parse(uriString) ).into(mImageViewPrev);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

       // mRef=mRef.child(type);
        //Toast.makeText(StatusUpdateActivity.this,"Salim1",Toast.LENGTH_LONG).show();
        mRef.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Toast.makeText(getApplicationContext(),"CSP",Toast.LENGTH_LONG).show();



                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    Log.d("DatabaseParent",dataSnapshot.toString());
                    Log.d("DatabaseChild", data.toString());

                    Uri uri = Uri.parse(data.getRef().getParent().toString());

                    String type = AuthorityDisplayActivity.getTypeSelected();

                    if(!(type.equals(AuthorityDisplayActivity.TYPE_PENDING) || type.equals(AuthorityDisplayActivity.TYPE_COMPLETED)  ) ){

                       type= "VerifiedPosts";
                    }

                    Log.d("Database",uri.getLastPathSegment());

                    if (uri.getLastPathSegment().equals(type) ){

                       // Log.d("DatabaseParent",dataSnapshot.toString());
                        //Log.d("DatabaseChild", data.toString());
                        AuthorityPostUpdateMessage updatePost = data.getValue(AuthorityPostUpdateMessage.class);
                        String currentUserkey = updatePost.getPostKey();
                        Toast.makeText(StatusUpdateActivity.this,"Key:"+currentUserkey+" originalKey:"+key+" boolean:"+key.equals(currentUserkey),Toast.LENGTH_LONG).show();

                        if (key.equals(currentUserkey)) {
                            currentPost = updatePost;
                            if (currentPost.getUpdatedPhotoUrl().equals("null")) {
                                mTextViewPrev.setText("Status not uploaded yet!");
                                //                Toast.makeText(StatusUpdateActivity.this,"URL:"+updatePost.getPhotoUrl(),Toast.LENGTH_LONG).show();
                                // Glide.with(StatusUpdateActivity.this).load(updatePost.getPhotoUrl()).into(mImageViewPrev);
                            } else {
                                //Glide.with(StatusUpdateActivity.this).load(currentPost.getUpdatedPhotoUrl()).into(mImageViewPrev);
                            }
                        }

                        // mMarkerList.add(new MarkerOptions().position(new LatLng(post.getLatitude(),post.getLongitude())).title(post.getType()).snippet(post.getAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarkers_trimmed)) );

                    }
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
        });

        mCaptureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCameraButton();
            }
        });



       /* Query q= potholeRef.orderByChild("postKey").equalTo("postKey",key);
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(StatusUpdateActivity.this,dataSnapshot.toString(),Toast.LENGTH_LONG).show();
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
        });
*/
        mStatusUpdateRef = mDatabase.getReference().child("Completed");
    mUpdateImageButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

//

            // TODO : Check classifier output and distance range

           /* try {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(StatusUpdateActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object

                                    if(location.getLatitude()-currentPost.getLatitude()<0.01 && location.getLatitude()-currentPost.getLatitude()<0.01){

                                    if(currentPost.getType.equals("Pothole")){
                                    classifierTask();
                                    }
                                    else{
                                    postUpdate();
                                    }
                                }else
                                Toast.makeText().toShow();
                            }
                        });

            }catch(SecurityException e){
                e.printStackTrace();
            }*/

            DatabaseReference nonVerifiedRef = mDatabase.getReference().child("Non-VerifiedPosts");
            DatabaseReference verifiedRef = mDatabase.getReference().child("VerifiedPosts");
            DatabaseReference pendingRef = mDatabase.getReference().child("Pending");

            nonVerifiedRef.child(currentPost.getPostKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("Removing nonverifi post","isComplete? :"+task.isComplete());
                }
            });
            verifiedRef.child(currentPost.getPostKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("Removing verified post","isComplete? :"+task.isComplete());

                }
            });

            pendingRef.child(currentPost.getPostKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("Removing nonverifi post","isComplete? :"+task.isComplete());
                }
            });

            StorageReference childReference =mStorageReference.child(mPhotoUri.getLastPathSegment() );
            mProgressBar.setEnabled(true);
            mProgressBar.setVisibility(View.VISIBLE);

            childReference.putFile(mPhotoUri).addOnSuccessListener(StatusUpdateActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri mdownloadUri=taskSnapshot.getDownloadUrl();
                    if(mdownloadUri!=null){

                        currentPost.setUpdatedPhotoUrl(mdownloadUri.toString());

                     //   typeRef.child(currentPost.getPostKey()).setValue(currentPost);

                        mStatusUpdateRef = mDatabase.getReference().child("Completed");
                        mStatusUpdateRef.child(currentPost.getPostKey()).setValue(currentPost);

                        mProgressBar.setEnabled(false);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(StatusUpdateActivity.this, "Image Upload Success", Toast.LENGTH_LONG).show();
                        //startActivityForResult(new Intent(Image_And_Location.this,DisplayPost.class),RC_POST_UPLOADED);
                        finish();
                    }else{
                        Toast.makeText(StatusUpdateActivity.this,"Image upload failed!",Toast.LENGTH_LONG).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressBar.setEnabled(false);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            });


        }
    });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_CAMERA_CAPTURE && resultCode==RESULT_OK){

            mTextViewNew.setVisibility(View.VISIBLE);
            mImageViewNew.setVisibility(View.VISIBLE);
            mUpdateImageButton.setVisibility(View.VISIBLE);

           // Uri uri = data.getData();
            Glide.with(this).load(mPhotoUri).into(mImageViewNew);
        }
    }

    public void onClickCameraButton(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
               // mPhotoFile=photoFile;
                Uri photoURI = Uri.fromFile(photoFile);
                mPhotoUri= photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, RC_CAMERA_CAPTURE);
            }
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mPhotoPath = image.getAbsolutePath();
        // Toast.makeText(this, mPhotoPath,Toast.LENGTH_LONG).show();
        return image;
    }

}
