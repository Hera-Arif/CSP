package com.example.android.csp.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.android.csp.AuthorityDisplayFragment;
import com.bumptech.glide.Glide;
import com.example.android.csp.AuthorityDisplayActivity;
import com.example.android.csp.AuthorityPostUpdateMessage;
import com.example.android.csp.PostMessage;
import com.example.android.csp.R;
import com.example.android.csp.StatusUpdateActivity;
import com.example.android.csp.StatusViewActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Mapkar on 12/23/2017.
 */

public class AuthorityDisplayFragment extends Fragment {

    private RecyclerView mRecyclerview;

    private FirebaseDatabase mDatabase;

    private DatabaseReference mRef;


    final private String TYPE_POTHOLES = "Pothole";

    final private String TYPE_GARBAGE = "Garbage";

    static String mTypeSelected;


    private int RC_SIGNOUT= 4000;


    public AuthorityDisplayFragment(){
        
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        View rootView = inflater.inflate(R.layout.content_authority_display, container, false);



        mDatabase = FirebaseDatabase.getInstance();

        mRef =  mDatabase.getReference().child("Non-VerifiedPosts");

        mRecyclerview = (RecyclerView) rootView.findViewById(R.id.authority_recyclerview);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        initializeAdapter();

        return rootView;
    }

    private void initializeAdapter() {
        mTypeSelected = AuthorityDisplayActivity.getTypeSelected();
        Toast.makeText(getActivity(),"Value of mTypeselected is :"+mTypeSelected.toString(),Toast.LENGTH_LONG).show();
        if(mTypeSelected!=null){


            mRef =  mDatabase.getReference().child("VerifiedPosts").child(mTypeSelected);

            FirebaseRecyclerAdapter<AuthorityPostUpdateMessage,AuthorityDisplayFragment.PostViewHolder> adapter = new FirebaseRecyclerAdapter<AuthorityPostUpdateMessage, AuthorityDisplayFragment.PostViewHolder>(
                    new  FirebaseRecyclerOptions.Builder<AuthorityPostUpdateMessage>().setQuery(mRef,AuthorityPostUpdateMessage.class).build()) {

                /*       PostMessage.class,
                R.layout.single_post_layout,
                PostViewHolder.class,
                mDatabase)
*/

                @Override
                protected void onBindViewHolder(AuthorityDisplayFragment.PostViewHolder holder, int position, final AuthorityPostUpdateMessage model) {

                    holder.setUser(model.getName() );

                    holder.setType(model.getType() );

                    holder.setAddress(model.getAddress() );

                    holder.setImage( Uri.parse(model.getPhotoUrl()), model.getPostKey() );

                   /* holder.getVerifyButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DatabaseReference mVerifiedRef =  mDatabase.getReference().child("VerifiedPosts").child(model.getType());
                            mVerifiedRef.push().setValue(model);
                        }
                    });
                    */
                    Log.d("RecyclerView", "onBindViewHolder: "+position);

                }

                @Override
                public AuthorityDisplayFragment.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.single_post_layout, parent, false);

                    return new AuthorityDisplayFragment.PostViewHolder(view);
                }
            };

            adapter.startListening();
            mRecyclerview.setAdapter(adapter);

        }
    }

    public static String getTypeSelected() {
        return mTypeSelected;
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public PostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mView.findViewById(R.id.card_view_verify_button).setVisibility(View.INVISIBLE);


        }

        /*ImageButton getVerifyButton(){
            return mView.findViewById(R.id.card_view_share_button);
        }
        */

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

        void setImage(final Uri uri, final String key){
            ImageView mImageView = (ImageView) mView.findViewById(R.id.card_view_image);
             Glide.with(getContext()).load(uri).into(mImageView);

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), StatusUpdateActivity.class);
                    intent.putExtra("type",getTypeSelected());
                    intent.putExtra("originalPhotoUri",uri.toString());
                    intent.putExtra("key",key);
                    startActivity(intent);
                }
            });

        }

    }
        
}

