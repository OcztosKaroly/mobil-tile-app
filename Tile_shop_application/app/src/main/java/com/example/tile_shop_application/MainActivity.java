package com.example.tile_shop_application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();

    EditText emailET;
    EditText passwordET;

    private FirebaseAuth mAuth;

    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mView = findViewById(R.id.main);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_upward);
        mView.startAnimation(animation);

        emailET = findViewById(R.id.editTextEmail);
        passwordET = findViewById(R.id.editTextPassword);

        mAuth = FirebaseAuth.getInstance();
    }

    public void performLogin(View view) {

        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        Log.i(LOG_TAG, "Bejelentkezett: " + email + ", jelszó: " + password);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d(LOG_TAG, "Sikeres bejelentkezés");
                    Toast.makeText(MainActivity.this, "Sikeres bejelentkezés", Toast.LENGTH_SHORT).show();
                    startShopping();
                } else {
                    Log.d(LOG_TAG, "Sikertelen bejelentkezés");
                    Toast.makeText(MainActivity.this, "Sikertelen bejelentkezés: " + Objects.requireNonNull(task.getException()).getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startShopping() {
        Intent intent = new Intent(this, ShopActivity.class);
        startActivity(intent);
    }

    public void redirectToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);

        startActivity(intent);
    }

//    public void loginAnonim(View view) {
//        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful()) {
//                    Log.d(LOG_TAG, "Sikeres anoním bejelentkezés");
//                    startShopping();
//                } else {
//                    Log.d(LOG_TAG, "Sikertelen anoním bejelentkezés");
//                    Toast.makeText(MainActivity.this, "Sikertelen anoním bejelentkezés: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
}
