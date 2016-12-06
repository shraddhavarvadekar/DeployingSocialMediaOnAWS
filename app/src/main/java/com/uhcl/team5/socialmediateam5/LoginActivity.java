package com.uhcl.team5.socialmediateam5;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;

import java.util.Locale;
import java.util.Map;

/**
 * Created by SasiKanth on 9/23/2016.
 */
public class LoginActivity extends AppCompatActivity {

    Button buttonLogin;
    private EditText inUsername;
    private EditText inPassword;
    private TextView sign;

    // User Details
    private  String username;
    private String password;

    private AlertDialog userDialog;
    private ForgotPasswordContinuation forgotPasswordContinuation;
    private TextView ForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        inUsername = (EditText) findViewById(R.id.editTextUserId);
        inPassword = (EditText) findViewById(R.id.editTextUserPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogIn);
        sign=(TextView) findViewById(R.id.textViewUserSignUp);
        ForgotPassword = (TextView)findViewById(R.id.textViewUserForgotPassword);
       // username = inUsername.getText().toString();
        AppHelper.init(getApplicationContext());
        initApp();
       // findCurrent();

        sign.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                signUpNewUser();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });

        ForgotPassword.setOnClickListener(new View.OnClickListener(){

            // Forgot password processing
            @Override
            public void onClick(View v) {
                forgotpasswordUser();
            }
        });
    }

    private void signInUser() {
        username = inUsername.getText().toString();
        if (username == null || username.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
            label.setText(inUsername.getHint() + " cannot be empty");
            inUsername.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }
        AppHelper.setUser(username);
        password = inPassword.getText().toString();
        if(password == null || password.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
            label.setText(inPassword.getHint()+" cannot be empty");
            inPassword.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        AppHelper.getPool().getUser(username).getSessionInBackground(authenticationHandler);
    }

    /*public void signUp(View view) {
        signUpNewUser();
    }*/

    private void signUpNewUser() {
        Intent registerActivity = new Intent(this, RegisterActivity.class);
        startActivityForResult(registerActivity, 1);
    }

    private void initApp() {
        inUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.loginuserid);
                    label.setText(R.string.Username);
                    inUsername.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
                label.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.loginuserid);
                    label.setText("");
                }
            }
        });

        inPassword = (EditText) findViewById(R.id.editTextUserPassword);
        inPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewUserPasswordLabel);
                    label.setText(R.string.Password);
                    inPassword.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
                label.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewUserPasswordLabel);
                    label.setText("");
                }
            }
        });
    }

    private void findCurrent() {
        CognitoUser user = AppHelper.getPool().getCurrentUser();
        username = user.getUserId();
        if(username != null) {
            AppHelper.setUser(username);
            inUsername.setText(user.getUserId());
            user.getSessionInBackground(authenticationHandler);
        }
    }

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        if(username != null) {
            this.username = username;
            AppHelper.setUser(username);
        }
        if(this.password == null) {
            inUsername.setText(username);
            password = inPassword.getText().toString();
            if(password == null) {
                TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
                label.setText(inPassword.getHint()+" enter password");
                inPassword.setBackground(getDrawable(R.drawable.text_border_error));
                return;
            }

            if(password.length() < 1) {
                TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
                label.setText(inPassword.getHint()+" enter password");
                inPassword.setBackground(getDrawable(R.drawable.text_border_error));
                return;
            }
        }
     //   username = "+1"+username;
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(this.username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    //
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {

            AppHelper.getPool().getUser(username).getDetailsInBackground(getDetailsHandler);

        }

        GetDetailsHandler getDetailsHandler = new GetDetailsHandler() {
            @Override
            public void onSuccess(CognitoUserDetails cognitoUserDetails) {

                final Map<String, String> userDetails = cognitoUserDetails.getAttributes().getAttributes();
                Intent intent = new Intent(LoginActivity.this,DisplayUserDetails.class);
                intent.putExtra("email",userDetails.get("email"));
                intent.putExtra("name",userDetails.get("name"));
                intent.putExtra("phone",userDetails.get("phone_number"));
                intent.putExtra("user",username);
                startActivity(intent);
                //}
                // });
            }

            @Override
            public void onFailure(Exception exception) {
                exception.printStackTrace();
                  Log.i("onFailure","Exception");
            }
        };

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            Locale.setDefault(Locale.US);
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            //mfaAuth(multiFactorAuthenticationContinuation);
        }

        @Override
        public void onFailure(Exception e) {
            TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
            label.setText("Sign-in failed");
            inPassword.setBackground(getDrawable(R.drawable.text_border_error));

            label = (TextView) findViewById(R.id.textViewUserIdMessage);
            label.setText("Sign-in failed");
            inUsername.setBackground(getDrawable(R.drawable.text_border_error));

            showDialogMessage("Sign-in failed", AppHelper.formatException(e));
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            // TODO change the place holder
        }
    };






    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }


    private void forgotpasswordUser() {
        username = inUsername.getText().toString();
        if(username == null) {
            TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
            label.setText(inUsername.getHint()+" cannot be empty");
            inUsername.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        if(username.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
            label.setText(inUsername.getHint()+" cannot be empty");
            inUsername.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        AppHelper.getPool().getUser(username).forgotPasswordInBackground(forgotPasswordHandler);
    }

    private void getForgotPasswordCode(ForgotPasswordContinuation forgotPasswordContinuation) {
        this.forgotPasswordContinuation = forgotPasswordContinuation;
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        intent.putExtra("destination",forgotPasswordContinuation.getParameters().getDestination());
        intent.putExtra("deliveryMed",forgotPasswordContinuation.getParameters().getDeliveryMedium());
        startActivityForResult(intent, 3);
    }

    ForgotPasswordHandler forgotPasswordHandler = new ForgotPasswordHandler() {
        @Override
        public void onSuccess(){
            showDialogMessage("Password successfully changed!","");
            inPassword.setText("");
            inPassword.requestFocus();
        }

        @Override
        public void getResetCode(ForgotPasswordContinuation forgotPasswordContinuation) {
            getForgotPasswordCode(forgotPasswordContinuation);
        }

        @Override
        public void onFailure(Exception e) {
            showDialogMessage("Forgot password failed",AppHelper.formatException(e));
        }
    };

}
