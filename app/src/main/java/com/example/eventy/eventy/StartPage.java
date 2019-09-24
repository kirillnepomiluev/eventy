package com.example.eventy.eventy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class StartPage extends AppCompatActivity {

     ConstraintLayout clayout;
    private FirebaseAuth mAuth;
    private DatabaseReference mydatabase;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mydatabase=FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_start_page);





        mAuth = FirebaseAuth.getInstance();

        clayout = (ConstraintLayout) findViewById(R.id.itemList);
        clayout.setOnTouchListener(new SwipeTouchListener(this));
    }

    protected  void GoToNext() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            Myuser.name=currentUser.getDisplayName();
            Myuser.Uid=currentUser.getUid();
            mydatabase.child("users").child(Myuser.Uid).child("org").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Myuser.isorg= (boolean) dataSnapshot.getValue();
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            try {
                Myuser.PhotoURL = mAuth.getCurrentUser().getPhotoUrl().toString();
                if (Myuser.PhotoURL=="")
                    Myuser.PhotoURL="R.drawable.loginim";
            } catch (Exception ex) {
                Myuser.PhotoURL="R.drawable.loginim";
            }


            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }

    public  class SwipeTouchListener implements View.OnTouchListener {

        private static final String LOG_TAG = "SwipeTouchListener";
        private Activity activity;
        private  int MIN_DISTANCE;
        private float downX;
        private float downY;




        public SwipeTouchListener(Activity _activity) {
            activity = _activity;
            DisplayMetrics dm = activity.getResources().getDisplayMetrics();
            MIN_DISTANCE = (int) (30.0f * dm.densityDpi / 160.0f + 0.5);

        }

        private void onRightToLeftSwipe() {
            Log.i(LOG_TAG, "Справа налево!");
            GoToNext();
        }

        private void onLeftToRightSwipe() {
            Log.i(LOG_TAG, "Слева направо!");
            // удаляем файл или делаем любое действие с активити
            GoToNext();
        }

        private void onTopToBottomSwipe() {
            Log.i(LOG_TAG, "Сверху вниз!");
            GoToNext();
        }

        private void onBottomToTopSwipe() {
            Log.i(LOG_TAG, "Снизу вверх!");
            GoToNext();

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    return true;
                }
                case MotionEvent.ACTION_MOVE: {
                    float mdeltaX = event.getX() -downX ;
                    float mdeltaY = event.getY()-downY;
                        v.setTranslationX(mdeltaX);
                        v.setTranslationY(mdeltaY);

                        return true;


                }

                case MotionEvent.ACTION_UP: {
                    float upX = event.getX();
                    float upY = event.getY();

                    float deltaX = downX - upX;
                    float deltaY = downY - upY;

                    // горизонтальный свайп
                    if (Math.abs(deltaX) > MIN_DISTANCE) { // если дистанция не меньше минимальной
                        // слева направо
                        if (deltaX < 0) {
                            v.animate()
                                    //.x(v.getWidth())
                                    .translationX(-deltaX*6)
                                    .translationY(-deltaY*8)
                                    .setDuration(600)
                                    .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    onLeftToRightSwipe();


                                }
                            });

                            return true;







                        }
                        //справа налево
                        if (deltaX > 0) {
                            v.animate()
                                    //.x(-v.getWidth())
                                    .translationX(-deltaX*6)
                                    .translationY(-deltaY*8)
                                    .setDuration(600)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            onRightToLeftSwipe();

                                        }
                                    });
                            return true;



                        }
                    }

                    // вертикальный свайп
                    if (Math.abs(deltaY) > MIN_DISTANCE) { //если дистанция не меньше минимальной
                        // сверху вниз
                        if (deltaY < 0) {
                            v.animate()
                                    //.y(v.getHeight())
                                    .translationX(-deltaX*6)
                                    .translationY(-deltaY*8)
                                    .setDuration(600)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            onTopToBottomSwipe();

                                        }
                                    });
                            return true;


                        }
                        // снизу вверх
                        if (deltaY > 0) {
                            v.animate()
                                    //.y(-v.getHeight())
                                    .translationX(-deltaX*6)
                                    .translationY(-deltaY*8)
                                    .setDuration(600)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            onBottomToTopSwipe();

                                        }
                                    });
                            return true;


                        }
                    }

                    return false;
                }
            }
            return false;
        }
    }
}
