package com.example.android.csp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.security.AccessController.getContext;

public class AuthorityDisplayActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerview;

    private FirebaseDatabase mDatabase;

    private DatabaseReference mRef;


    final private String TYPE_POTHOLES = "Pothole";

    final private String TYPE_GARBAGE = "Garbage";

    static String mTypeSelected;

    AuthorityDisplayFragment fragment;

    private int RC_SIGNOUT= 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


/*
        mDatabase = FirebaseDatabase.getInstance();

        mRef =  mDatabase.getReference().child("Non-VerifiedPosts");

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
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGNOUT){
            finish();
        }

    }
}
