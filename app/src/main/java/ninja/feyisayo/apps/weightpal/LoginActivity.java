package ninja.feyisayo.apps.weightpal;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.firebase.client.*;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Firebase ref = new Firebase("https://weight-pal.firebaseio.com");

    private static final int REQUEST_SIGNUP = 0;
    private static final String TAG = "LoginActivity";

    AlertDialog alertDialog;
    Intent intent;

    SharedPreferences userInfo;
    SharedPreferences.Editor editor;

    @InjectView(R.id.button_login) Button loginButton;
    @InjectView(R.id.input_email) EditText emailText;
    @InjectView(R.id.input_password) EditText passwordText;
    @InjectView(R.id.signup_link) TextView signupLink;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Inject butterknife after the activity has been created
        ButterKnife.inject(this);


        userInfo = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = userInfo.edit();

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        // Launch SignUpActivity when the textview link is clicked
        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup Activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);


            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        // Check if the user is authenticated before showing the MainActivity
        if(checkUserAuthState()) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void login() {
        Log.d(TAG, "Login");

        if(!validate()){
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();


        new android.os.Handler().postDelayed(new Runnable() {

            public void run() {
                ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {

                    @Override
                    public void onAuthenticated(AuthData authData) {

                        // Store our user's data for later use with sharedPrefs
                        ref.child("users").child(authData.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                UserSchema user = snapshot.getValue(UserSchema.class);

                                // We store our user's name and email here in our editor
                                editor.putString("name", user.getName());
                                editor.putString("email", user.getEmail());
                                editor.apply();

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                        progressDialog.dismiss();

                        // Authenticate our user then store the user data in
                        // /users/<uid> where /users can be any arbitrary path tp store data
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("provider", authData.getProvider());

                        // email address data for when user is authenticated
                        if(authData.getProviderData().containsKey("email")){
                            map.put("email", authData.getProviderData().get("email").toString());
                        }

                        // Store the user's auth state in users/auth/<uid>
                        ref.child("users/auth").child(authData.getUid()).setValue(map);

                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        progressDialog.dismiss();
                        showDialog(error);
                        loginButton.setEnabled(true);
                    }
                });
            }
        }, 3000);

    }


    // Shows the user an error dialog if the credentials are incorrect
    public void showDialog(FirebaseError error){
        alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Different error scenarios...
        switch (error.getCode()){
            case FirebaseError.USER_DOES_NOT_EXIST:
                alertDialog.setMessage("User does not exist");
                break;
            case FirebaseError.INVALID_PASSWORD:
                alertDialog.setMessage("Invalid password for user account");
                break;
            case FirebaseError.NETWORK_ERROR:
                alertDialog.setMessage("Unable to connect with the authentication server. Is your internet down?");
                break;
            case FirebaseError.INVALID_CREDENTIALS:
                alertDialog.setMessage("Wrong email/password combination");
                break;
            default:
                alertDialog.setMessage("An unknown error occurred.");
        }

        // Show the dialog after we have set the error scenarios
        alertDialog.show();
    }



    /**
     * @return userLoggedIn, returns true if the user is logged in
     */
    public boolean checkUserAuthState(){
        boolean userLoggedIn;
        AuthData authData = ref.getAuth();

        userLoggedIn = authData != null;

        return userLoggedIn;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_SIGNUP){
            if(requestCode == RESULT_OK){

                // TODO: Implement successful logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();

            }
        }

    }

    @Override
    public void onBackPressed(){
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(){
        loginButton.setEnabled(true);
        //finish();
    }

    public void onLoginFailed(){
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate(){
        boolean valid =  true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Enter a valid email address");
            valid = false;
        }
        else {
            emailText.setError(null);
        }

        if(password.isEmpty() || password.length() < 4  || password.length() > 10){
            passwordText.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        }
        else {
            passwordText.setError(null);
        }

        return valid;
    }


}

