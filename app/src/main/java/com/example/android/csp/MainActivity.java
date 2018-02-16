package com.example.android.csp;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    ChildEventListener mChildEventListener;

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    FirebaseUser mUser;

    Button mLoginWithEmail;
    Button mLoginWithGmail;
    boolean isSignedIn;

    ScrollView scrollView;

    LinearLayout layout;

    ProgressBar mProgressBar;

    final private static int RC_SIGN_IN_AUTHORITY= 2000;
    final private static int RC_SIGN_IN_USER= 4000;

    final private static int Start_Ativity= 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*
        Display d = getWindowManager().getDefaultDisplay();
        layout = (LinearLayout) findViewById(R.id.layout_of_twice_height);
        Point size = new Point();
        d.getSize(size);
        Toast.makeText(this,"width:"+size.x+"height:"+size.y,Toast.LENGTH_LONG).show();
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        //params.height= size.y;
        params.height=0;
        params.width= size.x;

        layout.setLayoutParams(params);

*/
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        AuthUI.getInstance().signOut(this);
        isSignedIn= false;

        mLoginWithEmail = (Button)findViewById(R.id.login_email);
        mLoginWithGmail = (Button)findViewById(R.id.login_gmail);

        mProgressBar= findViewById(R.id.login_progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar.setEnabled(true);

        mDatabase = FirebaseDatabase.getInstance();

        mRef= mDatabase.getReference();

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();



                if(user!=null){

                    if(mProgressBar.isEnabled()){
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mProgressBar.setEnabled(false);
                    }
                   // Toast.makeText(MainActivity.this,user.getUid(),Toast.LENGTH_LONG).show();
                    if(user.getProviders().get(0).equalsIgnoreCase("google.com") ) {
                        startActivityForResult(new Intent(MainActivity.this, DisplayPost.class), Start_Ativity);
                    }else{
                        startActivityForResult(new Intent(MainActivity.this,AuthorityDisplayActivity.class),Start_Ativity);

                    }
                }
            }
        };

        mLoginWithEmail.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(!isSignedIn){
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setEnabled(true);

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN_AUTHORITY);
                }

            }
        } );


        mLoginWithGmail.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(!isSignedIn){
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setEnabled(true);
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN_USER);
                }

            }
        } );

       Intent mServiceIntent = new Intent(this, GeofenceIntentService.class);
        if (!isMyServiceRunning(GeofenceIntentService.class)) {
            // TODO : Start service
           // startService(mServiceIntent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d ("Never Ending Service", "isMyServiceRunning?"+true+"");
                return true;
            }
        }
        Log.d ("Never Ending Service", "isMyServiceRunning?"+false+"");
        return false;
    }


    public void addContentFragment(View view)
    {

        scrollView.post(new Runnable() {
            @Override
            public void run() {

                View view = findViewById(R.id.linear_layout_bottom);
                scrollView.smoothScrollTo(0, view.getTop());
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RC_SIGN_IN_USER){
            if(requestCode==RESULT_OK){
                isSignedIn=true;
                startActivityForResult(new Intent(MainActivity.this,DisplayPost.class),Start_Ativity);

            }
        }else  if(resultCode==RC_SIGN_IN_AUTHORITY){
            if(requestCode==RESULT_OK){
                isSignedIn=true;
                startActivityForResult(new Intent(MainActivity.this,AuthorityDisplayActivity.class),Start_Ativity);

            }
        }else if(requestCode==RESULT_CANCELED){
            finish();
        }else if(requestCode==Start_Ativity){
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener!=null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

}