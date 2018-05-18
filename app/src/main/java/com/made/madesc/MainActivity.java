package com.made.madesc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private Button mButtonLogin;
    public static final String USER_ID = "com.made.madesc.USER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mEditTextEmail = findViewById(R.id.et_email);
        mEditTextPassword = findViewById(R.id.et_password);
        mButtonLogin = findViewById(R.id.bt_login);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Checks if user is signed in and moves to appropriate activity if so
        FirebaseUser currentUser = mAuth.getCurrentUser();

        goToStoreList(currentUser);

    }

    private void goToStoreList(FirebaseUser currentUser) {
        if (currentUser == null) {
            return;
        }
        Intent intent = new Intent(this, StoreListActivity.class);
        startActivity(intent);
    }

    public void onLoginButtonPress(View v) {
        // Disable login button
        mButtonLogin.setEnabled(false);

        String email = mEditTextEmail.getText().toString();
        String password = mEditTextPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MainActivity", "signInWithEmail:success");
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            goToStoreList(currentUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MainActivity", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, R.string.login_failed_message,
                                    Toast.LENGTH_SHORT).show();
                            // Restore login button
                            mButtonLogin.setEnabled(true);
                        }
                    }
                });

        goToStoreList(null);
    }
}
