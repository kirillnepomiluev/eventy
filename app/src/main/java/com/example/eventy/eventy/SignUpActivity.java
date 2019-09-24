package com.example.eventy.eventy;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SignUpActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth mAuth;
    private Switch SwitchOrg;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private String name;
    private final static int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE =1;


    private ImageView imageLogin;
    static final int GALLERY_REQUEST = 1;
    private FirebaseStorage storage;
    private StorageReference usersref;
    private StorageReference picref;
    boolean ispreview;
    boolean ispicupload;
    private String imageUrl;


    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null)  {
            showProgress(false);
            return;
        }
        boolean organizator = SwitchOrg.isChecked();
        String uid = currentUser.getUid();
        myRef = database.getReference();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();



        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "New user created!",
                                    Toast.LENGTH_SHORT).show();


                        }else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

        myRef.child("users").child(uid).child("org").setValue(organizator);
        Myuser.isorg=organizator;
        Myuser.name= name;
        Myuser.Uid= uid;

        if (ispreview) {
            uploadeventpics(uid);
        }


        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        imageLogin = (ImageView) findViewById(R.id.LoginIm);
        ispicupload=false;
        ispreview=false;



        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        usersref = storage.getReference();

        mAuth = FirebaseAuth.getInstance();
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email_sign_up);
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.user_name_edit);

        mPasswordView = (EditText) findViewById(R.id.password_sign_up);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptSignUp();
                    return true;
                }
                return false;
            }
        });

        SwitchOrg = (Switch) findViewById(R.id.switch_organizator);


        Button mEmailSignUpButton = (Button) findViewById(R.id.button_sign_up);
        mEmailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });

        mLoginFormView = findViewById(R.id.login_form_sign_up);
        mProgressView = findViewById(R.id.login_progress_sign_up);


        imageLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionStatus = ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    showimagechoser();
                }
                else {
                    ActivityCompat.requestPermissions(SignUpActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
                }
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





                                }

                                @Override
                                public void onError(Exception e) {
                                    Toast.makeText(SignUpActivity.this, "ошибка.изображение не загруженно",
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

                     imageUrl =task.getResult().toString();
                    getimageURL(imageUrl);

                    ispicupload=true;


                } else {


                    Toast.makeText(SignUpActivity.this, "ошибка! Изображение  не обновленно!",
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

        mAuth.getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "изображение обновленно",
                                    Toast.LENGTH_SHORT).show();

                            Myuser.PhotoURL= imageUrl;
                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "ошибка! изображение не обновленно",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }




    private void attemptSignUp() {


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mUserNameView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        name = mUserNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(name)) {
            mUserNameView.setError("Введите имя");
            focusView = mUserNameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);// Sign in success, update UI with the signed-in user's information



                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(SignUpActivity.this, "create new user  failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // ...
                        }
                    });



        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }





}
