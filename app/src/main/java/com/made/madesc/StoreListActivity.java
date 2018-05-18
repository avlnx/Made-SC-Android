package com.made.madesc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StoreListActivity extends AppCompatActivity {

    FirebaseUser mCurrentUser;
    Button mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        mLogoutButton = findViewById(R.id.bt_log_out);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String userEmail = "";
        try {
            userEmail = mCurrentUser.getEmail();
        }
        catch (NullPointerException e) {
            // User is logged out
            goToLoginScreen();
        }
        TextView textViewUserId = findViewById(R.id.tv_user_id);
        textViewUserId.setText(getResources().getString(R.string.hello_text, userEmail));

    }

    private void goToLoginScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void onLogoutButtonPress(View v) {
        // disable logout button
        mLogoutButton.setEnabled(false);

        FirebaseAuth.getInstance().signOut();

        // Go to login activity
        goToLoginScreen();
    }
}
