package com.example.android.csp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.csp.Fragment.NotificationsFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OpenedPostActivity extends AppCompatActivity {


    private RecyclerView mRecyclerview;

    private FirebaseDatabase mDatabase;

    private DatabaseReference mRef;

    private FirebaseUser mUser;

    ImageView mImageView;

    TextView mUserNameTextView;

    TextView mTypeTextView;

    TextView mAddressTextView;

    Button mSendButton;

    Button mMapButton;

    EditText mEditText;

    final static String COMMENTS= "Comments";

    String mKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_post);
        mUser= FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        mImageView = (ImageView)findViewById(R.id.opened_post_image);

        mUserNameTextView = (TextView)findViewById(R.id.opened_post_User_name);
        mTypeTextView = (TextView)findViewById(R.id.opened_post_type);
        mAddressTextView = (TextView)findViewById(R.id.opened_post_address);

        mEditText = (EditText)findViewById(R.id.opened_post_messageEditText);

        mSendButton = (Button)findViewById(R.id.opened_post_sendButton);
        mMapButton = (Button)findViewById(R.id.opened_post_map_btn);


        mRecyclerview = (RecyclerView)findViewById(R.id.opened_post_recycler_view);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(this) );

        Intent intent = getIntent();
        mKey = intent.getExtras().getString("key");
        String type = intent.getExtras().getString("type");

        //intent.putExtra("key", key);
        String address = intent.getExtras().getString("address");
        final Double latitude=intent.getExtras().getDouble("latitude");
        final Double longitude=intent.getExtras().getDouble("longitude" );
        //intent.putExtra("type");
        String username=intent.getExtras().getString("username");
        String originalPhotoUri=intent.getExtras().getString("originalPhotoUri");

        mUserNameTextView.setText(username);

        mTypeTextView.setText(type);
        mAddressTextView.setText(address);

        Uri photoUri =Uri.parse(originalPhotoUri);

        Glide.with(this).load(photoUri).into(mImageView);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Comment comment = new Comment(mUser.getDisplayName(),mEditText.getText().toString());
                mEditText.setText("");
                mRef.child(COMMENTS).child(mKey).push().setValue(comment);
            }
        });

        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String location = "geo:"+latitude+","+longitude+"?q="+latitude+","+longitude;

                Uri geoLocation = Uri.parse(location);
                Log.d("OpenedPost",geoLocation.toString());
                Intent intent = new Intent(Intent.ACTION_VIEW,geoLocation);

                // COMPLETED (3) Set the data of the Intent to the Uri passed into this method
        /*
         * Using setData to set the Uri of this Intent has the exact same affect as passing it in
         * the Intent's constructor. This is simply an alternate way of doing this.
         *
         */



               // intent.setData(geoLocation);


                // COMPLETED (4) Verify that this Intent can be launched and then call startActivity
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        // Toast.makeText(getContext(), "onStart",Toast.LENGTH_SHORT).show();
        DatabaseReference ref = mDatabase.getReference().child(COMMENTS).child(mKey);


      /*  query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("RecyclerView", "Query: "+dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerAdapter<Comment, OpenedPostActivity.PostViewHolder> adapter = new FirebaseRecyclerAdapter<Comment,
                OpenedPostActivity.PostViewHolder>(
                new FirebaseRecyclerOptions.Builder<Comment>().setQuery(ref, Comment.class).build()) {

                /*       Comment.class,
                R.layout.single_post_layout,
                PostViewHolder.class,
                mDatabase)0
*/

            @Override
            protected void onBindViewHolder(OpenedPostActivity.PostViewHolder holder, int position, final Comment model) {

            /*    holder.setUser(model.getName());

                holder.setType(model.getType());

                holder.setAddress(model.getAddress());

                holder.setImage(Uri.parse(model.getPhotoUrl()), model.getPostKey());

                if (model.isVerified()) {

                    holder.setVerified("YES");

                    holder.getVerifyCount().setVisibility(View.INVISIBLE);
                } else {
                    holder.setVerified("NO");
                    String str = "" + (3 - model.getNumVerified());
                    holder.getVerifyCount().setText(str);
                }

                if (NotificationsFragment.getType().equals("Non-VerifiedPosts")) {

                    holder.getVerifyButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if (!model.isVerified() && model.getNumVerified() == 2) {
                                model.setVerified(true);
                                AuthorityPostUpdateMessage update = new AuthorityPostUpdateMessage(model);
                                DatabaseReference mVerifiedRef = mDatabase.getReference().child("VerifiedPosts").child(model.getType());
                                mVerifiedRef.child(model.getPostKey()).setValue(update);
                                model.setNumVerified(-1000);


                            } else {
                                model.setNumVerified(model.getNumVerified() + 1);
                            }
                            DatabaseReference mNonVerifiedRef = mDatabase.getReference().child("Non-VerifiedPosts").child(model.getPostKey());
                            mNonVerifiedRef.setValue(model);
                        }

                    });
                } else {
                    holder.getVerifyButton().setVisibility(View.INVISIBLE);
                }

                */

            holder.setUser(model.getUsername());
            holder.setComment(model.getComment());


                Log.d("OpenedPost-RecyclerView", "onBindViewHolder: " +model.getUsername()+ ""+position);

            }

            @Override
            public OpenedPostActivity.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_chat_layout, parent, false);

                return new OpenedPostActivity.PostViewHolder(view);
            }
        };

        adapter.startListening();
        mRecyclerview.setAdapter(adapter);
    }


    class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public PostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


        }


        void setUser(String user) {

            Log.d("OpenedPost-RecyclerView", "onBindViewHolder: setView"+user);
            TextView mTextView = (TextView) mView.findViewById(R.id.single_chat_name);

            mTextView.setText(user);
        }

        void setComment(String type) {

            Log.d("OpenedPost-RecyclerView", "onBindViewHolder: setView"+type);
            TextView mTextView = (TextView) mView.findViewById(R.id.single_chat_details);

            mTextView.setText(type);
        }



     /*   void setVerified(String verified) {
            TextView mTextView = (TextView) mView.findViewById(R.id.card_view_isverified);

            if (verified.equals("YES")) {
                TextView mLabelText = (TextView) mView.findViewById(R.id.card_view_verify_count_label);
                mLabelText.setVisibility(View.INVISIBLE);
            }

            mTextView.setText(verified);
        }

        TextView getVerifyCount() {
            TextView mTextView = (TextView) mView.findViewById(R.id.card_view_verify_count);

            return mTextView;

        }

        Button getVerifyButton() {
            return mView.findViewById(R.id.card_view_verify_button);
        }

        void setUser(String user) {
            TextView mTextView = (TextView) mView.findViewById(R.id.card_view_User_name);

            mTextView.setText(user);
        }

        void setType(String type) {
            TextView mTextView = (TextView) mView.findViewById(R.id.card_view_type);

            mTextView.setText(type);
        }

        void setAddress(String address) {
            TextView mTextView = (TextView) mView.findViewById(R.id.card_view_add_desc);

            mTextView.setText(address);
        }

        void setImage(final Uri uri, final String key) {
            ImageView mImageView = (ImageView) mView.findViewById(R.id.card_view_image);
            Glide.with(OpenedPostActivity.this).load(uri).listener(new RequestListener<Uri, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                    Log.e("IMAGE_EXCEPTION", "Exception " + e.toString());
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    Log.d("IMAGE_EXCEPTION", "Sometimes the image is not loaded and this text is not displayed");
                    return false;
                }
            }).into(mImageView);

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (NotificationsFragment.getType().equals("Completed")) {
                        Intent intent = new Intent(OpenedPostActivity.this, StatusViewActivity.class);
                        intent.putExtra("key", key);
                        intent.putExtra("originalPhotoUri", uri.toString());
                        startActivity(intent);
                    }


                }
            });

        }
    */
    }


}

