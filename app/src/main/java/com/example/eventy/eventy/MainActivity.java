package com.example.eventy.eventy;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity  {

    private FirebaseAuth mAuth;
    private DatabaseReference mydatabase;
    private FirebaseStorage storage;
    private StorageReference eventsref;
    private StorageReference eventspicsref;
    private StorageReference picref;


    private ArrayList<Event> events;
    private List<String> demo;

    private static final String TAG = "RecyclerViewExample";
    private List<FeedItem> feedsList;
    private List<FeedItem> feedsListnew;
    private List<FeedItem> feedsListrec;

    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewnew;
    private RecyclerView mRecyclerViewrec;
    private MyRecyclerViewAdapter adapter;
    private MyRecyclerViewAdapter adapternew;
    private MyRecyclerViewAdapter adapterrec;


    private ProgressBar progressBar;
    private String uri1;

    private TextView textView4;
    private String demo1;
    private Task <Uri> uri2;
    private ImageView imageLogin;
    String userimageurl;
    private long d;



    LinearLayoutManager linearLayoutManager;
    LinearLayoutManager linearLayoutManagernew;
    LinearLayoutManager linearLayoutManagerrec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mydatabase=FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        eventsref =storage.getReference();
        eventspicsref=eventsref.child("eventspics");
         d = new Date().getTime();






        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManagernew = new LinearLayoutManager(this);
        linearLayoutManagernew.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManagerrec = new LinearLayoutManager(this);
        linearLayoutManagerrec.setOrientation(LinearLayoutManager.HORIZONTAL);



        mRecyclerView = (RecyclerView) findViewById(R.id.RecViewall);
        mRecyclerViewnew = (RecyclerView) findViewById(R.id.RecViewnew);
        mRecyclerViewrec = (RecyclerView) findViewById(R.id.RecViewrec);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerViewnew.setLayoutManager(linearLayoutManagernew);
        mRecyclerViewrec.setLayoutManager(linearLayoutManagerrec);


        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        demo1= new String();
        uri1=new String();




        imageLogin = (ImageView) findViewById(R.id.LoginIm);


        String userUid = mAuth.getCurrentUser().getUid();
        try {
            userimageurl = mAuth.getCurrentUser().getPhotoUrl().toString();
            if (userimageurl.isEmpty())
                userimageurl="R.drawable.loginim";
        } catch (Exception ex) {
            userimageurl="R.drawable.loginim";
        }

        Picasso.get().load(userimageurl)
                .error(R.drawable.loginim)
                .placeholder(R.drawable.loginim)
                .transform(new CropCircleTransformation())
                .into(imageLogin);




        mydatabase.child("users").child(userUid).child("org").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Myuser.isorg= (boolean) dataSnapshot.getValue();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Myuser.name = mAuth.getCurrentUser().getDisplayName();
        Myuser.PhotoURL=userimageurl;
        Myuser.Uid= userUid;





        mydatabase.child("events").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                events = new ArrayList<>();

                if (snapshot.exists()) {

                    for (DataSnapshot snapshot1 :
                            snapshot.getChildren()) {

                             Event element = snapshot1.getValue(Event.class);
                             if (element!= null)

                            events.add(element);


                    }
                }
                updateUI();
                progressBar.setVisibility(View.GONE);

            }

                public void onCancelled(DatabaseError databaseError) {
                    Log.i(TAG, "loadPost:onCancelled", databaseError.toException());
                }

            });


        }

    @Override
    protected void onResume() {
        super.onResume();
        Picasso.get().load(Myuser.PhotoURL)
                .error(R.drawable.loginim)
                .placeholder(R.drawable.loginim)
                .transform(new CropCircleTransformation())
                .into(imageLogin);

    }


    private void updateUI() {

        feedsList = new ArrayList<>();
        feedsListnew = new ArrayList<>();
        feedsListrec = new ArrayList<>();

        parseResult(events);
        adapter = new MyRecyclerViewAdapter(MainActivity.this, feedsList);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(FeedItem item) {
                Myuser.myevent =events.get(item.getNumber());
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                intent.putExtra("number", item.getNumber());
                startActivity(intent);
            }
        });

        adapternew = new MyRecyclerViewAdapter(MainActivity.this, feedsListnew);
        adapterrec = new MyRecyclerViewAdapter(MainActivity.this, feedsListrec);
        adapterrec.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(FeedItem item) {
                Myuser.myevent =events.get(item.getNumber());
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                intent.putExtra("number", item.getNumber());
                startActivity(intent);
            }
        });
        adapternew.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(FeedItem item) {
                Myuser.myevent =events.get(item.getNumber());
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                intent.putExtra("number", item.getNumber());
                startActivity(intent);
            }
        });
        mRecyclerViewnew.setAdapter(adapternew);
        mRecyclerViewrec.setAdapter(adapterrec);


    }

    private void parseResult(List<Event> events1) {
        for (int i = 0; i < events1.size(); i++) {



            FeedItem item = new FeedItem();
            item.setTitle(events1.get(i).getTitleEvent());
            String s;
            try {
                s=events1.get(i).getimageURL();
                if (s.isEmpty()) {
                    s="R.drawable.placeholder";
                }
            } catch (Exception excepcion) {
                Log.e(TAG, "parseResult: ",excepcion );
                s=("R.drawable.placeholder");
            }
            item.setThumbnail(s);
            item.setNumber(i);
            feedsList.add(item);
            if (events1.get(i).isRecommended()) feedsListrec.add(item);
            if ((d - events1.get(i).getCreatedate())<=86400000) feedsListnew.add(item);

        }
       // textView4.setText(demo1);


    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        if (Myuser.isorg==false) {
            menu.getItem(1).setVisible(false);
        }
        else menu.getItem(1).setVisible(true);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            // пункты меню для tvColor
            case R.id.m_logout:
                mAuth.signOut();
                Myuser.signout();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.profile:

                startActivity(new Intent(this, ProfileActivity.class));
                break;

            case R.id.gotocreate:

                startActivity(new Intent(this, Event_create_Activity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }


}
