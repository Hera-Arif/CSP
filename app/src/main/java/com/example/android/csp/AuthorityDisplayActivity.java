package com.example.android.csp;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeUnit;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.csp.Fragment.AuthorityDisplayFragment;
import com.example.android.csp.Fragment.PostsFragment;
import com.example.android.csp.utilities.NotificationUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.Instant;

import java.util.Date;

import static java.security.AccessController.getContext;

public class AuthorityDisplayActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerview;

    private FirebaseDatabase mDatabase;

    private DatabaseReference mRef;


    final public static String TYPE_POTHOLES = "Pothole";

    final public static String TYPE_GARBAGE = "Garbage";

    final public static String TYPE_PENDING = "Pending";

    final public static String TYPE_COMPLETED = "Completed";

    final public static String KEY_AUTHORITY_NOTIFIED = "isFirstTime";

    private static String mNotificationTitle= "Check out the Pending Section!";

    private static String  mNotificationBody= "There are some post whose Service Level Agreement has been extended, check out the pending section to fix them";


    DatabaseReference mCompleteRef;

    static String mTypeSelected;

    AuthorityDisplayFragment fragment;

    private int RC_SIGNOUT= 4000;

    boolean isChildChanged;

    int numDays;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isChildChanged= false;

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mTypeSelected=TYPE_POTHOLES;

        AndroidThreeTen.init(this);




        mDatabase = FirebaseDatabase.getInstance();

        mRef =  mDatabase.getReference();


        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Todo Add notification


                Log.d("AuthorityDisplay", "Inside child added");


                for (DataSnapshot data : dataSnapshot.getChildren()) {


                    Uri uri = Uri.parse(data.getRef().getParent().toString());
                    if (uri.getLastPathSegment().equals("VerifiedPosts") ) {


                        //data.getRef().toString()

                        Log.d("AuthorityDisplay", "Ref :" + data.getRef().getParent().toString());

                        Log.d("AuthorityDisplay", "Data :" + data.toString());

                        AuthorityPostUpdateMessage post = data.getValue(AuthorityPostUpdateMessage.class);

                        if (post.getType().equals(TYPE_POTHOLES)){
                            numDays= 15;
                        }
                        else {
                            numDays=7;
                        }

                        String strdate1 = post.getCreationDate();

                        // SimpleDateFormat date = new SimpleDateFormat(strdate);

                        Instant instant = Instant.now();
                        Date current_date = DateTimeUtils.toDate(instant);

                        String strdate2 = current_date.toString();

                        //long diff =1;java.util.concurrent.TimeUnit.MICROSECONDS.toDays(epoch_date-post.creationDate);
                        if (compareDates(strdate1, strdate2, numDays)) {

                            mCompleteRef = mDatabase.getReference().child("Pending");

                            mCompleteRef.child(post.getPostKey()).setValue(post);

                            if (!(savedInstanceState != null && savedInstanceState.containsKey(KEY_AUTHORITY_NOTIFIED) && savedInstanceState.getBoolean(KEY_AUTHORITY_NOTIFIED))) {

                                // savedInstanceState.putBoolean(KEY_AUTHORITY_NOTIFIED,true);
                                NotificationUtils.remindUserThroughNotification(AuthorityDisplayActivity.this, mNotificationTitle,mNotificationBody);
                                Log.d("AuthorityDisplay", "Data exceeds 15 days- inside childadded");

                            }

                            //  Log.d("AuthorityDisplay",diff+"Data: "+data.toString());
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Log.d("AuthorityDisplay", "Inside child changed");

                for(DataSnapshot data :dataSnapshot.getChildren() ){


                    Uri uri = Uri.parse(data.getRef().getParent().toString());
                    if (uri.getLastPathSegment().equals("VerifiedPosts") ) {


                        //data.getRef().toString()

                        Log.d("AuthorityDisplay", "Ref :" + data.getRef().getParent().toString());

                        Log.d("AuthorityDisplay", "Data :" + data.toString());

                        AuthorityPostUpdateMessage post = data.getValue(AuthorityPostUpdateMessage.class);

                        if (post.getType().equals(TYPE_POTHOLES)){
                            numDays= 15;
                        }
                        else {
                            numDays=7;
                        }

                        String strdate1 = post.getCreationDate();

                        // SimpleDateFormat date = new SimpleDateFormat(strdate);

                        Instant instant = Instant.now();
                        Date current_date = DateTimeUtils.toDate(instant);

                        String strdate2 = current_date.toString();

                        //long diff =1;java.util.concurrent.TimeUnit.MICROSECONDS.toDays(epoch_date-post.creationDate);
                        if (compareDates(strdate1, strdate2, numDays)) {

                            mCompleteRef = mDatabase.getReference().child("Pending");

                            mCompleteRef.child(post.getPostKey()).setValue(post);

                           // if ( (!(savedInstanceState != null && savedInstanceState.containsKey(KEY_AUTHORITY_NOTIFIED) && savedInstanceState.getBoolean(KEY_AUTHORITY_NOTIFIED) ) || !isChildChanged )) {

                                // savedInstanceState.putBoolean(KEY_AUTHORITY_NOTIFIED,true);
                            NotificationUtils.remindUserThroughNotification(AuthorityDisplayActivity.this, mNotificationTitle,mNotificationBody);
                                Log.d("AuthorityDisplay", "Data exceeds 15 days- inside childadded");

                                isChildChanged =true;

                            //}

                            //  Log.d("AuthorityDisplay",diff+"Data: "+data.toString());
                        }
                    }
                    //  Log.d("AuthorityDisplay",diff+"Data: "+data.toString());
                }

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
/*
        mRecyclerview = (RecyclerView) findViewById(R.id.authority_recyclerview);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        if(mTypeSelected!=null){

            mRef =  mDatabase.getReference().child("VerifiedPosts").child(mTypeSelected);

            FirebaseRecyclerAdapter<PostMessage,PostViewHolder> adapter = new FirebaseRecyclerAdapter<PostMessage, PostViewHolder>(
                    new  FirebaseRecyclerOptions.Builder<PostMessage>().setQuery(mRef,PostMessage.class).build()) {

      */          /*       PostMessage.class,
                R.layout.single_post_layout,
                PostViewHolder.class,
                mDatabase)
*/

          /*      @Override
                protected void onBindViewHolder(PostViewHolder holder, int position, final PostMessage model) {

                    holder.setUser(model.getName() );

                    holder.setType(model.getType() );

                    holder.setAddress(model.getAddress() );

                    holder.setImage( Uri.parse(model.getPhotoUrl()) );

            */       /* holder.getVerifyButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DatabaseReference mVerifiedRef =  mDatabase.getReference().child("VerifiedPosts").child(model.getType());
                            mVerifiedRef.push().setValue(model);
                        }
                    });
                    */
              /*      Log.d("RecyclerView", "onBindViewHolder: "+position);

                }

                @Override
                public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.single_post_layout, parent, false);

                    return new PostViewHolder(view);
                }
            };

            adapter.startListening();
            mRecyclerview.setAdapter(adapter);

        }

    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public PostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


        }
*/
        /*ImageButton getVerifyButton(){
            return mView.findViewById(R.id.card_view_share_button);
        }
        */
/*
        void setUser(String user){
            TextView mTextView = (TextView) mView.findViewById(R.id.card_view_User_name);

            mTextView.setText(user);
        }

        void setType(String type){
            TextView mTextView = (TextView) mView.findViewById(R.id.card_view_type);

            mTextView.setText(type);
        }

        void setAddress(String address){
            TextView mTextView = (TextView) mView.findViewById(R.id.card_view_add_desc);

            mTextView.setText(address);
        }

        void setImage(Uri uri){
            ImageView mImageView = (ImageView) mView.findViewById(R.id.card_view_image);
           // Glide.with(getContext()).load(uri).into(mImageView);


        }

    }
*/

        fragment = new AuthorityDisplayFragment();

        FragmentManager fm = getSupportFragmentManager();


        fm.beginTransaction()
                .add(R.id.authority_fragment_container,fragment)
                .commit();

    }
    /**
     * Compares if strdate2 is greater than strdate1 by numDays
     *
     * Note- It works for consecutive months only and assumes the fact that strdate2> strdate1
     */

    private boolean compareDates(String strdate1, String strdate2, int numDays) {


        String[] splitDate1 =strdate1.split(" ");

        String[] splitDate2 =strdate2.split(" ");

        int day1 = Integer.parseInt(splitDate1[2]);

        int day2 = Integer.parseInt(splitDate2[2]);

        if (!splitDate1[1].equals(splitDate2[1]) ){

            day2 +=30;
        }
        Log.d("AuthorityDisplay","Diff:  "+day2+" "+day1+" "+(day2-day1));
        if(day2-day1>numDays){
            return true;
        }

        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putBoolean(KEY_AUTHORITY_NOTIFIED,true);
        // etc.
    }

    public static String getTypeSelected() {
        return mTypeSelected;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.authority_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out_menu:
                startActivityForResult(new Intent(this,MainActivity.class), RC_SIGNOUT);
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id==R.id.nav_show_potholes){
            mTypeSelected=TYPE_POTHOLES;

        }else if(id==R.id.nav_show_garbage){
            mTypeSelected= TYPE_GARBAGE;
        }else if(id==R.id.nav_show_completed){
            mTypeSelected=TYPE_COMPLETED;

        }else if(id==R.id.nav_show_pending){
            mTypeSelected= TYPE_PENDING;
        }
/*
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

  */
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction().replace(R.id.authority_fragment_container,new AuthorityDisplayFragment()).commit();
/*
        fm.beginTransaction()
                .add(R.id.authority_fragment_container,fragment)
                .commit();

*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGNOUT){
            finish();
        }

    }
}
