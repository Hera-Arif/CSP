package com.example.android.csp;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class StatusViewActivity extends AppCompatActivity {


    TextView mTextViewPrev;
    TextView mTextViewNew;
    ImageView mImageViewPrev;
    ImageView mImageViewNew;
    //Button mCaptureImageButton;
    //Button mUpdateImageButton;

    FirebaseDatabase mDatabase;
    DatabaseReference mRef;

    AuthorityPostUpdateMessage currentPost;

    boolean isUpdated=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_view);

        final String key = getIntent().getExtras().getString("key");
        Toast.makeText(this,key,Toast.LENGTH_LONG).show();

        mTextViewPrev= (TextView)findViewById(R.id.tv_sv_prev);
        mTextViewNew= (TextView)findViewById(R.id.tv_sv_new);
        mImageViewNew= (ImageView)findViewById(R.id.iv_sv_new);
        mImageViewPrev= (ImageView)findViewById(R.id.iv_sv_prev);

        // From activity PostsFragment
        String str = getIntent().getExtras().getString("originalPhotoUri");
        Uri originalPhotoUri = Uri.parse(str);
        Glide.with(StatusViewActivity.this).load(originalPhotoUri).into(mImageViewPrev);

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        //.child("Completed");
        mRef.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

               // dataSnapshot=dataSnapshot.child("Completed");
                Log.d("DatabaseParent",dataSnapshot.toString());

                if(dataSnapshot.getKey().equals("Completed")) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        Log.d("DatabaseChild", data.toString());
                        AuthorityPostUpdateMessage updatePost = data.getValue(AuthorityPostUpdateMessage.class);
                        String currentUserkey = updatePost.getPostKey();
                        Toast.makeText(StatusViewActivity.this, "Key:" + currentUserkey + " originalKey:" + key + " boolean:" + key.equals(currentUserkey), Toast.LENGTH_LONG).show();

                        if (key.equals(currentUserkey)) {
                            isUpdated=true;
                            currentPost = updatePost;

                                Glide.with(StatusViewActivity.this).load(currentPost.getUpdatedPhotoUrl()).into(mImageViewNew);

                        }

                        // mMarkerList.add(new MarkerOptions().position(new LatLng(post.getLatitude(),post.getLongitude())).title(post.getType()).snippet(post.getAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarkers_trimmed)) );

                    }
                    if(!isUpdated){
                        mTextViewNew.setText("Status not yet uploaded by authorities");
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


    }
}
