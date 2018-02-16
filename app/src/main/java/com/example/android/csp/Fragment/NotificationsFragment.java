package com.example.android.csp.Fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.csp.AuthorityDisplayActivity;
import com.example.android.csp.AuthorityPostUpdateMessage;
import com.example.android.csp.DisplayPost;
import com.example.android.csp.PostMessage;
import com.example.android.csp.R;
import com.example.android.csp.StatusViewActivity;
import com.example.android.csp.utilities.NotificationUtils;
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

import java.util.ArrayList;
import java.util.List;

import static com.example.android.csp.AuthorityDisplayActivity.KEY_AUTHORITY_NOTIFIED;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment implements
        AdapterView.OnItemSelectedListener  {


    private static String mType= "Completed";

    private static String mNotificationTitle= "Check out the Filter tab!";

    private static String  mNotificationBody= "Some of your new post has been upadated by the authorities, give your confirmation along with a feedback";



    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        FragmentManager fm = getFragmentManager();

        fm.beginTransaction()
                .add(R.id.container_custom_post,new CustomPostFragment())
                .commit();

        //TODO Send notification

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Completed");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0){
                    if (!(savedInstanceState != null && savedInstanceState.containsKey(KEY_AUTHORITY_NOTIFIED) && savedInstanceState.getBoolean(KEY_AUTHORITY_NOTIFIED))) {

                        // savedInstanceState.putBoolean(KEY_AUTHORITY_NOTIFIED,true);
                        NotificationUtils.remindUserThroughNotification(getContext(),mNotificationTitle,mNotificationBody);
                        Log.d("Noftifications fragment", "notification sent");

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_notifications, container, false);




        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) view.findViewById(R.id.spinner_navigate);
        spin.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Completed");
        categories.add("VerifiedPosts");
        categories.add("Non-VerifiedPosts");


        //Creating the ArrayAdapter instance having the country list

        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,categories);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);


        return view;
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



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        // On selecting a spinner item
        String item = adapterView.getItemAtPosition(i).toString();

        mType=item;


        FragmentManager fm = getFragmentManager();

        fm.beginTransaction()
                .replace(R.id.container_custom_post,new CustomPostFragment())
                .commit();

        // Showing selected spinner item
        Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static String getType(){
        return mType;
    }

}


    /*

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_contacts_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
*/

