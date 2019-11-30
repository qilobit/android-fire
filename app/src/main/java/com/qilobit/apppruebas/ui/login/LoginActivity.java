package com.qilobit.apppruebas.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.qilobit.apppruebas.MainActivity;
import com.qilobit.apppruebas.R;


public class LoginActivity extends AppCompatActivity {

    private Context context;
    private static String TAG = "======= LOGIN ACTIVITY ========";
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar;
    private Intent activityIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        checkAuthState();
    }
    private void checkAuthState(){
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getUid() != null){
                    activityIntent = new Intent(context, MainActivity.class);
                    startActivity(activityIntent);
                    finish();
                }
            }
        });
    }
    private void signIn(){
        String user = usernameEditText.getText().toString();
        String pass = passwordEditText.getText().toString();
        if(user.isEmpty() || pass.isEmpty()){
            Toast.makeText(context, "Name and password required", Toast.LENGTH_SHORT).show();
        }else{
            loadingProgressBar.setVisibility(View.VISIBLE);
            FirebaseAuth
                    .getInstance()
                    .signInWithEmailAndPassword(user, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Welcome", Toast.LENGTH_LONG).show();
                                activityIntent = new Intent(context, MainActivity.class);
                                startActivity(activityIntent);
                                finish();
                            } else {
                                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }
    }
}
