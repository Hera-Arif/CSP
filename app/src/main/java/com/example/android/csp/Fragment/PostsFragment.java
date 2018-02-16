package com.example.android.csp.Fragment;


import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.csp.AuthorityPostUpdateMessage;
import com.example.android.csp.DisplayPost;
import com.example.android.csp.OpenedPostActivity;
import com.example.android.csp.PostMessage;
import com.example.android.csp.R;
import com.example.android.csp.StatusViewActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostsFragment extends Fragment {

    private RecyclerView mRecyclerview;

    private FirebaseDatabase mDatabase;

    private DatabaseReference mRef;


    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);

        // Toast.makeText(getContext(), "onCreate",Toast.LENGTH_SHORT).show();

        mDatabase = FirebaseDatabase.getInstance();

        mRef = mDatabase.getReference().child("Non-VerifiedPosts");


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //  Toast.makeText(getContext(), "onCreateView",Toast.LENGTH_SHORT).show();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //  Toast.makeText(getContext(), "onViewCreated",Toast.LENGTH_SHORT).show();

        mRecyclerview = (RecyclerView) getView().findViewById(R.id.post_recyclerview);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));


    }

    /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      //  inflater.inflate(R.menu.menu_calls_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


*/

    @Override
    public void onStart() {
        super.onStart();

        // Toast.makeText(getContext(), "onStart",Toast.LENGTH_SHORT).show();

        FirebaseRecyclerAdapter<PostMessage, PostViewHolder> adapter = new FirebaseRecyclerAdapter<PostMessage, PostViewHolder>(
                new FirebaseRecyclerOptions.Builder<PostMessage>().setQuery(mRef, PostMessage.class).build()) {

                /*       PostMessage.class,
                R.layout.single_post_layout,
                PostViewHolder.class,
                mDatabase)
*/

            @Override
            protected void onBindViewHolder(PostViewHolder holder, int position, final PostMessage model) {

                holder.setUser(model.getName());

                holder.setType(model.getType());

                holder.setAddress(model.getAddress());

                holder.setImage(Uri.parse(model.getPhotoUrl()), model.getPostKey(), model);

                if (model.isVerified()) {

                    holder.setVerified("YES");

                    holder.getVerifyCount().setVisibility(View.INVISIBLE);
                } else {
                    holder.setVerified("NO");
                    String str = "" + (3 - model.getNumVerified());
                    holder.getVerifyCount().setText(str);
                }

                holder.getVerifyButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (!model.isVerified() && model.getNumVerified() == 2) {
                            model.setVerified(true);
                            AuthorityPostUpdateMessage update = new AuthorityPostUpdateMessage(model);
                            // DatabaseReference mVerifiedRef = mDatabase.getReference().child("VerifiedPosts").child(model.getType());
                            DatabaseReference mVerifiedRef = mDatabase.getReference().child("VerifiedPosts");
                            mVerifiedRef.child(model.getPostKey()).setValue(update);
                            model.setNumVerified(-1000);


                        } else {
                            model.setNumVerified(model.getNumVerified() + 1);
                        }
                        DatabaseReference mNonVerifiedRef = mDatabase.getReference().child("Non-VerifiedPosts").child(model.getPostKey());
                       if(model.isVerified()) {mNonVerifiedRef.removeValue();}
                       else
                        mNonVerifiedRef.setValue(model);
                    }

                });


                Log.d("RecyclerView", "onBindViewHolder: " + position);

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


    class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public PostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


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

        void setVerified(String verified) {
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

        void setAddress(String address) {
            TextView mTextView = (TextView) mView.findViewById(R.id.card_view_add_desc);

            mTextView.setText(address);
        }

        void setImage(final Uri uri, final String key, final PostMessage model) {
            ImageView mImageView = (ImageView) mView.findViewById(R.id.card_view_image);
            Glide.with(PostsFragment.this).load(uri).listener(new RequestListener<Uri, GlideDrawable>() {
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

                 /*

                 mImageView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         Intent intent = new Intent(getContext(), StatusViewActivity.class);
                         intent.putExtra("key",key);
                         intent.putExtra("originalPhotoUri",uri.toString());
                         startActivity(intent);
                     }
                 });


                 */

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent intent = new Intent(getContext(), OpenedPostActivity.class);
                    intent.putExtra("key", key);
                    intent.putExtra("address", model.getAddress());
                    intent.putExtra("latitude", model.getLatitude());
                    intent.putExtra("longitude", model.getLongitude());
                    intent.putExtra("type", model.getType());
                    intent.putExtra("username", model.getName());
                    intent.putExtra("originalPhotoUri", uri.toString());
                    startActivity(intent);


                }

            });
        }
    }
}
