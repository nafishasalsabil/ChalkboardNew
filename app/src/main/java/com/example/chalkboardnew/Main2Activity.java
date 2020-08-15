package com.example.chalkboardnew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;


public  class Main2Activity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private Button signin;
    private TextView username;
    private EditText email;
    private EditText password;
    private TextView signup;
    private TextView forgot;
    private Button google;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 1;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    private GoogleApiClient googleApiClient;
SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        forgot = findViewById(R.id.forgotpass);
        forgot.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

// Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signin = (Button)findViewById(R.id.signin);
        email = (EditText)findViewById(R.id.email);
        password =(EditText) findViewById(R.id.password);
        signup = (TextView) findViewById(R.id.signup);
        progressBar = findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();
  //      firestore = FirebaseFirestore.getInstance();

        google = findViewById(R.id.google);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                 String mail = email.getText().toString().trim();
                String pass = password.getText().toString().trim();


                if (TextUtils.isEmpty(mail)) {
                    email.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    password.setError("Password is required");
                    return;
                }

                if(pass.length()<6)
                {
                    password.setError("Password must be of more than 6 characters");
                    return;
                }
                mAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            if(mAuth.getCurrentUser().isEmailVerified())
                            {
                                sharedPreferences = getSharedPreferences("selected", Context.MODE_PRIVATE);
                                boolean bool= sharedPreferences.getBoolean("lockedState", true);
                                if(bool == true)
                                {
                                    System.out.println("helloooooooooooooooooooooooo");
                                    startActivity(new Intent(getApplicationContext(), Features.class));

                                }
                                else
                                {
                                    startActivity(new Intent(getApplicationContext(), ChoiceActivity.class));
                                    Toast.makeText(Main2Activity.this, "Sign in successful!", Toast.LENGTH_SHORT).show();

                                }


                            }
                            else
                            {
                                Toast.makeText(Main2Activity.this, "Please verify your email", Toast.LENGTH_SHORT).show();

                            }

                        }
                        else {
                            Toast.makeText(Main2Activity.this,  task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });


    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            gotoProfile();
        }else{
            Toast.makeText(getApplicationContext(),"Sign in cancel",Toast.LENGTH_LONG).show();
        }
    }

    private void gotoProfile() {
        Intent intent=new Intent(Main2Activity.this,Features.class);
        startActivity(intent);

    }



    @Override
    public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(),SignupActivity.class));
    }

    public void forgotpassword(View view) {
        startActivity(new Intent(getApplicationContext(),ForgotPassActivity.class));

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
