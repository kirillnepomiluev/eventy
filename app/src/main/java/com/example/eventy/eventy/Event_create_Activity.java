package com.example.eventy.eventy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Event_create_Activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mydatabase;
    private FirebaseStorage storage;
    private StorageReference eventsref;
    private StorageReference eventspicsref;
    private Button EditPreviewButton;
    private Button AddEventButton;
    private EditText EventNameEdittext;
    private EditText EventUrlEdittext;
    private TextView LoginOrgtextView;
    private TextInputEditText EventInfoEdittext;
    private String orgname;
    private String orgUid;
    private final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 911;
    private String imageURL;
    private ImageView imgview;
    static final int GALLERY_REQUEST = 1;
    private String key;
    boolean ispreview;
    boolean ispicupload = true;
    private StorageReference picref;
    private final static int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        mAuth = FirebaseAuth.getInstance();
        mydatabase=FirebaseDatabase.getInstance().getReference();
        storage= FirebaseStorage.getInstance();
        eventsref=storage.getReference();
        eventspicsref=eventsref.child("eventspics");


        EditPreviewButton = (Button) findViewById(R.id.edit_preview_button);
        AddEventButton = (Button) findViewById(R.id.addeventbutton);
        EventNameEdittext = (EditText) findViewById(R.id.eventname);
        EventUrlEdittext = (EditText) findViewById(R.id.eventUrl);
        LoginOrgtextView = (TextView) findViewById(R.id.loginorg_ac);
        EventInfoEdittext = (TextInputEditText) findViewById(R.id.event_info_edit);
        imgview = (ImageView) findViewById(R.id.previewev);
        ispreview = false;




        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name
            orgname = user.getDisplayName();
            orgUid = user.getUid();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
        }

        LoginOrgtextView.setText(orgname);



        AddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addevent();
            }
        });

        EditPreviewButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int permissionStatus = ContextCompat.checkSelfPermission(Event_create_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser();
                } else {
                    ActivityCompat.requestPermissions(Event_create_Activity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
                }


            }
        });

    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser();
                }
                break;
        }
    }

    private void addevent() {
        String TitleEvent = EventNameEdittext.getText().toString();
        String EventUrl = EventUrlEdittext.getText().toString();
        String EventInfo = EventInfoEdittext.getText().toString();
        key = mydatabase.child("events").push().getKey();



        Event newEvent = new Event(key,orgname,orgUid,TitleEvent,EventInfo,EventUrl);
        Map<String, Object> neweventValues = newEvent.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/events/" + key, neweventValues);
        childUpdates.put("/users/" + orgUid + "/userevents/" + key, neweventValues);

        mydatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                // ...
                Toast.makeText(Event_create_Activity.this, "Успешно добавленно новое мероприятие!",
                        Toast.LENGTH_SHORT).show();
                if (ispreview)
                uploadeventpics(key);


                mydatabase.child("/events/"+key).child("ispicupload").setValue(ispicupload);
                mydatabase.child("/events/"+key).child("imageURL").setValue(imageURL);


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                        Toast.makeText(Event_create_Activity.this, "ошибка.мероприятие не создано",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadeventpics(String key) {

         picref= eventspicsref.child(key+".jpg");

        // Get the data from an ImageView as bytes
        imgview.setDrawingCacheEnabled(true);
        imgview.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgview.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();



        UploadTask uploadTask = picref.putBytes(data);

     /*   uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                imgview.setVisibility(View.INVISIBLE);
                EditPreviewButton.setVisibility(View.VISIBLE);
                Toast.makeText(Event_create_Activity.this, "ошибка! Изображение  не обновленно!",
                        Toast.LENGTH_SHORT).show();
                ispicupload = false;     // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Event_create_Activity.this, "Изображение для мероприятия обновленно!",
                        Toast.LENGTH_SHORT).show();
                imgview.setVisibility(View.INVISIBLE);
                EditPreviewButton.setVisibility(View.VISIBLE);
                ispicupload = true;

                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });   */


            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();

                    }

                    // Continue with the task to get the download URL
                    return picref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        imageURL =task.getResult().toString();
                        getimageURL(imageURL);


                    } else {

                        imgview.setVisibility(View.INVISIBLE);
                        EditPreviewButton.setVisibility(View.VISIBLE);
                        Toast.makeText(Event_create_Activity.this, "ошибка! Изображение  не обновленно!",
                                Toast.LENGTH_SHORT).show();
                        ispicupload = false;
                        // Handle failures
                        // ...
                    }
                }
            });









    }

    private void getimageURL (String url)  {
        mydatabase.child("/events/"+key).child("imageURL").setValue(url);
        Toast.makeText(Event_create_Activity.this, "Изображение для мероприятия обновленно!",
                Toast.LENGTH_SHORT).show();
        imgview.setVisibility(View.INVISIBLE);
        EditPreviewButton.setVisibility(View.VISIBLE);
        ispicupload = true;
        mydatabase.child("/events/"+key).child("ispicupload").setValue(ispicupload);

    }



        private void showFileChooser() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {

                    Uri selectedImageUri = imageReturnedIntent.getData();

                    Picasso.get()
                            .load(selectedImageUri)
                            .error(R.drawable.placeholder)
                            .resize(180, 120)
                            .centerCrop()
                            .into(imgview, new Callback() {
                        @Override
                        public void onSuccess() {
                            imgview.setVisibility(View.VISIBLE);
                            EditPreviewButton.setVisibility(View.INVISIBLE);
                            ispreview=true;
                            ispicupload=false;



                        }

                                @Override
                                public void onError(Exception e) {
                                    Toast.makeText(Event_create_Activity.this, "ошибка.изображение не загруженно",
                                            Toast.LENGTH_SHORT).show();
                                    ispreview=false;
                                    ispicupload=false;

                                }


                    });




                }
        }
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.getItem(1).setVisible(false);
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
