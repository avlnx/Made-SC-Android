package com.made.madesc;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.made.madesc.model.MainViewModel;

public class MainActivity extends AppCompatActivity {

//    private FirebaseUser mCurrentUser;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private Button mButtonLogin;
    private MainViewModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextEmail = findViewById(R.id.et_email);
        mEditTextPassword = findViewById(R.id.et_password);
        mButtonLogin = findViewById(R.id.bt_login);

        mModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mModel.getCurrentUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable FirebaseUser currentUser) {
                if (currentUser != null) {
                    goToStoreList();
                }
            }
        });
    }

    private void goToStoreList() {
        Intent intent = new Intent(this, StoreListActivity.class);
        startActivity(intent);
    }

    public void onLoginButtonPress(View v) {
        // Disable login button
        mButtonLogin.setEnabled(false);

        String email = mEditTextEmail.getText().toString();
        String password = mEditTextPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, R.string.login_form_empty,
                    Toast.LENGTH_SHORT).show();
            // Restore login button
            mButtonLogin.setEnabled(true);
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MainActivity", "signInWithEmail:success");
                            mModel.setCurrentUser(FirebaseAuth.getInstance().getCurrentUser());
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
    }
}
