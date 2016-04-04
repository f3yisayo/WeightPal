package ninja.feyisayo.apps.weightpal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;


public class SignUpActivity extends AppCompatActivity {

    Firebase ref = new Firebase("https://weight-pal.firebaseio.com");
    Firebase saveUserData;

    private static final String TAG = "SignupActivity";

    @InjectView(R.id.button_signup)
    Button signupButton;
    @InjectView(R.id.input_email)
    EditText emailText;
    @InjectView(R.id.input_name)
    EditText nameText;
    @InjectView(R.id.input_password)
    EditText passwordText;
    @InjectView(R.id.login_link)
    TextView loginText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity.
                finish();
            }
        });

    }


    private void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this, R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = nameText.getText().toString();
        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();


        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        // On complete, call either onSuccess or onSignupFailed depending on success
                        // We implement firebase's email/password authentication system here and create the user

                        ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                            @Override
                            public void onSuccess(Map<String, Object> result) {

                                // Save the name of the user to the database by storing it in the users child node
                                // Also cast the uid object to a string so Firebase understands
                                saveUserData = ref.child("users").child( result.get("uid").toString() );

                                saveUserData.child("name").setValue(name);
                                saveUserData.child("email").setValue(email);
                                saveUserData.child("password").setValue(password);

                                progressDialog.dismiss();

                                // True for success dialog
                                showDialogs(true, result);

                                signupButton.setEnabled(true);
                                setResult(RESULT_OK, null);
                            }

                            @Override
                            public void onError(FirebaseError firebaseError) {
                                signupButton.setEnabled(true);

                                // Dismiss the progress dialog since there was an error.
                                progressDialog.dismiss();

                                // False for error Dialog
                                showDialogs(false);
                            }
                        });

                    }
                }, 3000);
    }

    // On success: Show a dialog to the user confirming success of account creation
    // On Failure: Show a dialog to the user confirming failure of the account creation
    public void showDialogs(boolean dialog, Map<String, Object> result) {
        AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
        if (dialog) {
            // TODO: Remove the uid from the message in the dialog
            alertDialog.setMessage("Account created successfully with \n UID: " + result.get("uid") + "\nYou may login with your account now.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // Take the user back to the calling Login Activity for login.
                            finish();
                        }
                    });

            alertDialog.show();
        }

    }

    public void showDialogs(boolean dialog) {
        AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
        if (!dialog) {
            alertDialog.setMessage("Wrong email/password combination");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.show();
        }
    }

    public void onSignupFailed() {
        // create a toast telling the user what just happened
        Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_LONG).show();

        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        // name field
        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 characters");
            valid = false;
        } else
            nameText.setError(null);

        // email field
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 5) {
            passwordText.setError("At least 5 alphanumeric characters");
            valid = false;
        } else
            passwordText.setError(null);


        return valid;

    }
}
