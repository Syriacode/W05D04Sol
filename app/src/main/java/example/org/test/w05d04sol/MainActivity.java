package example.org.test.w05d04sol;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LoginButton loginButton;
    CallbackManager callbackManager;
    public static final String WELCOME_MESSAGE = "Welcome! Your email is %s";

    // Views
    TextView tvDisplay;
    EditText etUserEmail;
    EditText etUserPassword;

    //firebase authentication
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.btnFacebookLogging);

        // bind the views
        tvDisplay = findViewById(R.id.tvDisplay);
        etUserEmail = findViewById(R.id.etUserEmail);
        etUserPassword = findViewById(R.id.etUserPassword);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                tvDisplay.setText("Login Sucess \n"+
                loginResult.getAccessToken().getUserId()+ "\n" + loginResult.getAccessToken().getToken());


            }

            @Override
            public void onCancel() {
                tvDisplay.setText("Login Cancelled");

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        //initialize Firebase authentication
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(
                    "0",
                    "message",
                    NotificationManager.IMPORTANCE_HIGH);
            //send notification to user using this channel
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void onClick(View view) {
        String email = etUserEmail.getText().toString();
        String password = etUserPassword.getText().toString();
        if(!email.isEmpty() && !password.isEmpty()) {
            signOnFirebaseUser(email, password);
        }

        }
    private void signOnFirebaseUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            firebaseUser = firebaseAuth.getCurrentUser();
                            displayWelcomeMessage();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }
    private void displayWelcomeMessage() {
        tvDisplay.setText(String.format(Locale.US, WELCOME_MESSAGE, firebaseUser.getEmail()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
