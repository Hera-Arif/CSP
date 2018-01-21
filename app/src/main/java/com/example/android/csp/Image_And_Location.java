package com.example.android.csp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.csp.utilities.NetworkUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Image_And_Location extends AppCompatActivity {

    TextView mType;
    TextView mConfidence;
    TextView mlocation;
    ImageView mDisplayImage;
    EditText mDescription;
    Button mbutton;
    String result;

    Uri mphotoUri;
    Uri mdownloadUri;

    FirebaseStorage mFirebaseStorage;
    FirebaseDatabase mFirebaseDatabase;

    StorageReference mStorageReference;
    DatabaseReference mNonVerifiedPostRef;

    FirebaseUser mUser;


    PostMessage post = null;

    Place mPlace;


    int PLACE_PICKER_REQUEST = 1;

    final private String TYPE_POTHOLES = "Pothole";

    final private String TYPE_GARBAGE = "Garbage";

    final private int RC_POST_UPLOADED =9000;

    String mTypeSelected;

    ProgressBar mProgressBar;

    boolean isClassified= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image__and__location);

        mTypeSelected = TYPE_POTHOLES;

        mType = (TextView) findViewById(R.id.type);
        mConfidence = (TextView) findViewById(R.id.conf);
        mlocation = (TextView) findViewById(R.id.loc);
        mDisplayImage = (ImageView) findViewById(R.id.iv_preview);
        mDescription = (EditText) findViewById(R.id.description);
        mbutton= (Button)findViewById(R.id.post);


        mphotoUri= DisplayPost.mPhotoUri;
        mdownloadUri = null;

        Glide.with(this).load(mphotoUri).into(mDisplayImage);

        // Get Image
       /* BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = 1;

        Bitmap bitmap = BitmapFactory.decodeFile(DisplayPost.mPhotoPath, bmOptions);

        // Display Image
        mDisplayImage.setImageBitmap(bitmap);
*/
        URL resp = NetworkUtils.buildUrl();


        //FireBase initializations

        mFirebaseDatabase =FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mStorageReference = mFirebaseStorage.getReference().child("Images");
        mNonVerifiedPostRef = mFirebaseDatabase.getReference().child("Non-VerifiedPosts");

        mUser= FirebaseAuth.getInstance().getCurrentUser();


        mProgressBar = (ProgressBar)findViewById(R.id.upload_progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        new ClassifierTask().execute(resp);
        isClassified= true;
        //mbutton.setEnabled(false);
        mDescription.setVisibility(View.VISIBLE);
        mbutton.setVisibility(View.VISIBLE);


        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            mProgressBar.setEnabled(true);
            mProgressBar.setVisibility(View.VISIBLE);
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }

    public void onClickGetLocation(View v) {
       // mlocation.setVisibility(View.VISIBLE);
        mDescription.setVisibility(View.VISIBLE);
        mbutton.setVisibility(View.VISIBLE);


        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            mProgressBar.setEnabled(true);
            mProgressBar.setVisibility(View.VISIBLE);
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if(mProgressBar.isEnabled()){
            mProgressBar.setVisibility(View.INVISIBLE);
            mProgressBar.setEnabled(false);
        }

        if(requestCode==PLACE_PICKER_REQUEST && resultCode==RESULT_OK){


            Place place = PlacePicker.getPlace(this,data);
            mPlace= place;
            Geocoder geocoder = new Geocoder(this);
            try
            {
                if(TextUtils.isEmpty(place.getAddress() ) || place.getAddress()=="" || place.getAddress()==null) {
                   // Toast.makeText(Image_And_Location.this, "In the if condition", Toast.LENGTH_LONG).show();
                    List<Address> addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getAddressLine(1);
                    //String country = addresses.get(0).getAddressLine(2);

                    mDescription.setText(address);
                }
                else{
                    //Toast.makeText(Image_And_Location.this, "In the else condition", Toast.LENGTH_LONG).show();
                    mDescription.setText(place.getAddress());
                }

            } catch (IOException e)
            {

                e.printStackTrace();
            }
          //  Toast.makeText(this , place.toString(),Toast.LENGTH_LONG).show();

        }
        else if( requestCode==RC_POST_UPLOADED && resultCode==RESULT_OK ){
            finish();
        }
    }

    public void onClickPost(View v) {

        //Toast.makeText(this,"Todo:Post the details to users",Toast.LENGTH_LONG).show();
        StorageReference childReference =mStorageReference.child(mphotoUri.getLastPathSegment() );
        mProgressBar.setEnabled(true);
        mProgressBar.setVisibility(View.VISIBLE);

        childReference.putFile(mphotoUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                mdownloadUri=taskSnapshot.getDownloadUrl();
                if(mdownloadUri!=null){

                    DatabaseReference keyRef = mNonVerifiedPostRef.push();
                    String key =keyRef.getKey();

                    post = new PostMessage(key,mUser.getUid(),
                            mUser.getDisplayName(),
                            mDescription.getText().toString(),
                            mdownloadUri.toString(),
                            mTypeSelected, mPlace.getId(),
                            mPlace.getLatLng().latitude, mPlace.getLatLng().longitude);

                    Toast.makeText(Image_And_Location.this, key, Toast.LENGTH_LONG).show();
                            keyRef.setValue(post);
                    mProgressBar.setEnabled(false);
                    mProgressBar.setVisibility(View.INVISIBLE);

                    startActivityForResult(new Intent(Image_And_Location.this,DisplayPost.class),RC_POST_UPLOADED);
                }else{
                    Toast.makeText(Image_And_Location.this,"Image upload failed!",Toast.LENGTH_LONG).show();
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




    public class ClassifierTask extends AsyncTask<URL, Void, String> {


        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(searchResults!=null)
            Log.d("Url:", searchResults);
            result=searchResults;


            return searchResults;
        }

        // COMPLETED (3) Override onPostExecute to display the results in the TextView
        @Override
        protected void onPostExecute(String searchResults) {
            String type="Pothole";
            String confidence="--";
           if (searchResults != null && !searchResults.equals("")) {
                try {
                    JSONObject json = new JSONObject(searchResults);
                    if(json.getString("status")=="1"||true){
                        type= json.getString("type");
                        confidence = json.getString("confidence");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                mTypeSelected= type;
            mType.setText( type );
            mConfidence.setText(confidence);
            //    mDescription.setText(result);
            }
        }
    }


}
