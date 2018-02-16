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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.csp.AuthorityDisplayActivity;
import com.example.android.csp.AuthorityPostUpdateMessage;
import com.example.android.csp.OpenedPostActivity;
import com.example.android.csp.PostMessage;
import com.example.android.csp.R;
import com.example.android.csp.StatusViewActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Mapkar on 2/11/2018.
 */

public class CustomPostFragment extends Fragment {

    private RecyclerView mRecyclerview;

    private FirebaseDatabase mDatabase;

    private DatabaseReference mRef;

    private FirebaseUser mUser;

    public CustomPostFragment(){

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser= FirebaseAuth.getInstance().getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Todo Add notification

        View view =inflater.inflate(R.layout.fragment_custom_post, container, false);
        mRecyclerview = (RecyclerView)view.findViewById(R.id.recyclerview_navigate);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        mRef =  mDatabase.getReference().child(NotificationsFragment.getType());

      //  if (NotificationsFragment.getType().equals("Completed")&& mRef.h)



        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        // Toast.makeText(getContext(), "onStart",Toast.LENGTH_SHORT).show();
        Query query = mRef.orderByChild("userId").equalTo(mUser.getUid());

      /*  query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("RecyclerView", "Query: "+dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerAdapter<PostMessage,CustomPostFragment.PostViewHolder> adapter = new FirebaseRecyclerAdapter<PostMessage,
                CustomPostFragment.PostViewHolder>(
                new  FirebaseRecyclerOptions.Builder<PostMessage>().setQuery(query,PostMessage.class).build()) {

                /*       PostMessage.class,
                R.layout.single_post_layout,
                PostViewHolder.class,
                mDatabase)0
*/

            @Override
            protected void onBindViewHolder(CustomPostFragment.PostViewHolder holder, int position, final PostMessage model) {

                holder.setUser(model.getName() );

                holder.setType(model.getType() );

                holder.setAddress(model.getAddress() );

                holder.setImage( Uri.parse(model.getPhotoUrl()) , model.getPostKey(), model);

                if(model.isVerified()){

                    holder.setVerified("YES");

                    holder.getVerifyCount().setVisibility(View.INVISIBLE);
                }else {
                    holder.setVerified("NO");
                    String str= ""+(3-model.getNumVerified());
                    holder.getVerifyCount().setText(str);
                }

                if(NotificationsFragment.getType().equals("Non-VerifiedPosts") ) {

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
                }
                else {
                    holder.getVerifyButton().setVisibility(View.INVISIBLE);
                }


                Log.d("RecyclerView", "onBindViewHolder: "+position);

            }

            @Override
            public CustomPostFragment.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_post_layout, parent, false);

                return new CustomPostFragment.PostViewHolder(view);
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

        void setVerified(String verified){
            TextView mTextView = (TextView) mView.findViewById(R.id.card_view_isverified);

            if(verified.equals("YES")){
                TextView mLabelText = (TextView) mView.findViewById(R.id.card_view_verify_count_label);
                mLabelText.setVisibility(View.INVISIBLE);
            }

            mTextView.setText(verified);
        }

        TextView getVerifyCount(){
            TextView mTextView = (TextView) mView.findViewById(R.id.card_view_verify_count);

            return mTextView;

        }

        Button getVerifyButton(){
            return mView.findViewById(R.id.card_view_verify_button);
        }

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

        void setImage(final Uri uri, final String key, final PostMessage model){
            ImageView mImageView = (ImageView) mView.findViewById(R.id.card_view_image);
            Glide.with(CustomPostFragment.this).load(uri).listener(new RequestListener<Uri, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                    Log.e("IMAGE_EXCEPTION", "Exception " + e.toString());
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    Log.d("IMAGE_EXCEPTION","Sometimes the image is not loaded and this text is not displayed");
                    return false;
                }
            }).into(mImageView);

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(NotificationsFragment.getType().equals("Completed") ) {
                        Intent intent = new Intent(getContext(), StatusViewActivity.class);
                        intent.putExtra("key", key);
                        intent.putExtra("originalPhotoUri", uri.toString());
                        startActivity(intent);
                    }else{

                        Intent intent = new Intent(getContext(), OpenedPostActivity.class);
                        intent.putExtra("key", key);
                        intent.putExtra("address",model.getAddress() );
                        intent.putExtra("latitude", model.getLatitude());
                        intent.putExtra("longitude", model.getLongitude() );
                        intent.putExtra("type", model.getType());
                        intent.putExtra("username", model.getName());
                        intent.putExtra("originalPhotoUri", uri.toString());
                        startActivity(intent);

                    }


                }
            });

        }

    }

}
