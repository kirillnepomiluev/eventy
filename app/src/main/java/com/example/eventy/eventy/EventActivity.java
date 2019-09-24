package com.example.eventy.eventy;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class EventActivity extends AppCompatActivity {
    private int number;
    private TextView orgname;
    private TextView eventTitle;
    private ImageView imageevent;
    private TextView eventinfo;
    private Button buyButton;
    private String imageUrl;
    private Uri eventUri;
    private FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        number = intent.getIntExtra("number",-1);

        mAuth = FirebaseAuth.getInstance();


        orgname = (TextView) findViewById(R.id.loginorg_ac);
        eventTitle=(TextView) findViewById(R.id.eventname);
        imageevent=(ImageView) findViewById(R.id.previewev);
        eventinfo=(TextView) findViewById(R.id.infoevent);
        buyButton=(Button) findViewById(R.id.buyeventbutton);

        orgname.setText(Myuser.myevent.getAuthor());
        eventTitle.setText(Myuser.myevent.getTitleEvent());
        eventinfo.setText(Myuser.myevent.getInfoEvent());

        try {
            imageUrl = Myuser.myevent.getimageURL();
            if (imageUrl.isEmpty()) imageUrl="R.drawable.placeholder";
        } catch (Exception e) {
            imageUrl="R.drawable.placeholder";
        }

        Picasso.get().load(imageUrl)
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(imageevent);
        String su;

        String s = Myuser.myevent.getEventUrl();
        String s1 = s.substring(0,4);
        if (s1.equals("http"))
        su=s;
        else su="http://"+s;


        eventUri = Uri.parse(su);


        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openlink = new Intent(Intent.ACTION_VIEW).setData(getEventUri());
                startActivity(Intent.createChooser(openlink, "Browser"));
            }
        });





    }
    private Uri getEventUri() {
        return eventUri;
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
