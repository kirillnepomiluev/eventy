package com.example.eventy.eventy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser user;
    private TextView UserName;
    private String name;
    private TextView EditProfilepic;
    private ImageView imageLogin;
    static final int GALLERY_REQUEST = 1;
    private FirebaseStorage storage;
    private StorageReference usersref;
    private StorageReference picref;
    boolean ispreview;
    boolean ispicupload;
    String key;
    String userimageurl;
    Uri photoUrl;
    private final static int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE =1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageLogin = (ImageView) findViewById(R.id.LoginIm);
        ispicupload=false;
        ispreview=false;

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            name = user.getDisplayName();
            photoUrl = user.getPhotoUrl();
            key = user.getUid();


            // Check if user's email is verified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
             }

        try {
            userimageurl = photoUrl.toString();
        } catch (Exception ex) {
            userimageurl="R.drawable.loginim";
        }

        storage = FirebaseStorage.getInstance();
        usersref = storage.getReference();

        StorageReference picref;

        UserName = (TextView) findViewById(R.id.user_name);
        UserName.setText(name);


        Picasso.get().load(userimageurl)
                .error(R.drawable.loginim)
                .placeholder(R.drawable.loginim)
                .transform(new CropCircleTransformation())
                .into(imageLogin);

        EditProfilepic = (TextView) findViewById(R.id.edit_profile_pic);
        EditProfilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null){
                    int permissionStatus = ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    showimagechoser();
                    }
                    else {
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
                     }
                }
                else Toast.makeText(ProfileActivity.this, "сначала авторизуйтесь",
                        Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showimagechoser();
                }
                break;
        }
    }


    private void showimagechoser() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {

                    Uri selectedImageUri = imageReturnedIntent.getData();

                    Picasso.get()
                            .load(selectedImageUri)
                            .error(R.drawable.loginim)
                            .resize(180, 180)
                            .centerCrop()
                            .transform(new CropCircleTransformation())
                            .into(imageLogin, new Callback() {
                                @Override
                                public void onSuccess() {
                                    imageLogin.setVisibility(View.VISIBLE);
                                    ispreview=true;
                                    ispicupload=false;
                                    uploadeventpics(key);




                                }

                                @Override
                                public void onError(Exception e) {
                                    Toast.makeText(ProfileActivity.this, "ошибка.изображение не загруженно",
                                            Toast.LENGTH_SHORT).show();
                                    ispreview=false;
                                    ispicupload=false;

                                }


                            });




                }
        }
    }


    private void uploadeventpics(String key) {

        picref= usersref.child("userspics").child(key+".jpg");

        // Get the data from an ImageView as bytes
        imageLogin.setDrawingCacheEnabled(true);
        imageLogin.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageLogin.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();



        UploadTask uploadTask = picref.putBytes(data);

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

                    userimageurl =task.getResult().toString();
                    getimageURL(userimageurl);
                    Toast.makeText(ProfileActivity.this, "Изображение обновленно!",
                            Toast.LENGTH_SHORT).show();
                    ispicupload=true;


                } else {


                    Toast.makeText(ProfileActivity.this, "ошибка! Изображение  не обновленно!",
                            Toast.LENGTH_SHORT).show();
                    ispicupload = false;
                    // Handle failures
                    // ...
                }
            }
        });









    }

    private void getimageURL (String url)  {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(url))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "изображение обновленно",
                                    Toast.LENGTH_SHORT).show();
                            ispicupload = true;
                            Myuser.PhotoURL=userimageurl;
                        }
                    }
                });

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
