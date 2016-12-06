package com.uhcl.team5.socialmediateam5;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
//import andriod.net.*;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;

import com.amazonaws.regions.Region;
//import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
//import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;


import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by ABILASH on 9/11/2016.
 */
public class
ToolsActivity extends AppCompatActivity {

    TextView emailId;
    ImageButton addNumberButton;
    ImageButton addEmailButton;
    Button register;
    static int editTextCount=0;
    static int editMailCount=0;
    EditText toolphonenumber;
    EditText toolphonenumber1;
    EditText toolphonenumber2;
    EditText altemail;
    EditText editText;
    EditText editText1;
    Intent intent;
    String confiruser;
    private AlertDialog userDialog;

    final Context context = this;
    TextView textView1;
    ImageButton instaButton;
    ImageButton twitButton;
    ImageButton pinButton;
    ImageButton fbutton;



    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;
    private String imagePath;


    private DBHelper dbHelper = new DBHelper(context);
    UserDetails details = new UserDetails();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
       // transferUtility = A.getTransferUtility(this);
        transferUtility = AppHelper.getTransferUtility(this);


        init();


        //FaceBook Page Redirect on Popup box
        fbutton = (ImageButton) findViewById(R.id.facebookButton);


        fbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Redirect");
                alertDialogBuilder.setMessage("Enter your social media user id");

                final EditText userInput = new EditText(context);
                alertDialogBuilder.setView(userInput);

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userid = userInput.getEditableText().toString();

                        //create the object and add the values in it (problem to pass)
                        details.setFid(userid);

                        Toast.makeText(context, userid, Toast.LENGTH_LONG).show();
                        String path = "https://facebook.com/" + userid;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        // i.setData(Uri.parse(path));
                        //  startActivity(i);
                    }
                });

                alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.cancel();
                    }
                }); //End of alert.setNegativeButton
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        //Instagram Page redirect after popup
        instaButton = (ImageButton) findViewById(R.id.instagramButton);

        instaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Redirect");
                alertDialogBuilder.setMessage("Enter your social media user id");

                final EditText userInput = new EditText(context);
                alertDialogBuilder.setView(userInput);

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userid = userInput.getEditableText().toString();

                        //set the instagram id : dynamodb pass value
                        details.setInstaid(userid);

                        Toast.makeText(context, userid, Toast.LENGTH_LONG).show();
                        String path = "https://instagram.com/" + userid;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        //i.setData(Uri.parse(path));
                        //startActivity(i);
                    }
                });

                alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.cancel();
                    }
                }); //End of alert.setNegativeButton
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        //Twitter page redirect on popup
        twitButton = (ImageButton) findViewById(R.id.twitterButton);

        twitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Redirect");
                alertDialogBuilder.setMessage("Enter your social media user id");

                final EditText userInput = new EditText(context);
                alertDialogBuilder.setView(userInput);

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userid = userInput.getEditableText().toString();

                        //set twitter id : dynamodb pass value
                        details.setInstaid(userid);

                        Toast.makeText(context, userid, Toast.LENGTH_LONG).show();
                        String path = "https://instagram.com/" + userid;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        // i.setData(Uri.parse(path));
                        //startActivity(i);
                    }
                });

                alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.cancel();
                    }
                }); //End of alert.setNegativeButton
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        //Pinterest Page redirect on popup
        pinButton = (ImageButton) findViewById(R.id.pButton);
        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Redirect");
                alertDialogBuilder.setMessage("Enter your social media user id");

                final EditText userInput = new EditText(context);
                alertDialogBuilder.setView(userInput);

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userid = userInput.getEditableText().toString();

                        //set pintrest id : dynamodb pass value
                        details.setPinid(userid);

                        Toast.makeText(context, userid, Toast.LENGTH_LONG).show();
                        String path = "https://instagram.com/" + userid;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        // i.setData(Uri.parse(path));
                        //startActivity(i);
                    }
                });

                alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.cancel();
                    }
                }); //End of alert.setNegativeButton
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    public void init() {

        emailId = (TextView) findViewById(R.id.toolemail);
        addNumberButton = (ImageButton) findViewById(R.id.imageButton);
        addEmailButton = (ImageButton) findViewById(R.id.displayEmailImage);
        register = (Button) findViewById(R.id.toolNext);

        toolphonenumber = (EditText) findViewById(R.id.editTextRegPhone);
        toolphonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewRegPhoneLabel);
                    label.setText(" country code and no seperators");
                    toolphonenumber.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewRegPhoneMessage);
                label.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewRegPhoneLabel);
                    label.setText("");
                }
            }
        });


        intent = getIntent();
        final String email = intent.getStringExtra("email");
        final String userName = intent.getStringExtra("username");
        confiruser = intent.getStringExtra("username");
        final String password = intent.getStringExtra("password");
        imagePath = intent.getStringExtra("imagePath");
        emailId.setText(email);


        addNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTextCount < 2) {
                    if (editTextCount == 0)
                    {
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.addNumlinearlayout);
                        editText = new EditText(ToolsActivity.this);
                        editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                        editText.setHint("Enter Home Phone number");
                        editText.setGravity(Gravity.CENTER);
                        editText.setId(R.id.Home_phonenumber);
                        linearLayout.addView(editText);
                   }
                    else
                    {
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.addNumlinearlayout);
                        editText1 = new EditText(ToolsActivity.this);
                        editText1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        editText1.setHint("Enter Work Phone number");
                        editText1.setGravity(Gravity.CENTER);
                        editText1.setId(R.id.Work_phonenumber);
                        linearLayout.addView(editText1);
                    }

                    editTextCount++;

                }


            }
        });

        addEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editMailCount < 1)
                {
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.addEmaillayout);
                        editText = new EditText(ToolsActivity.this);
                        editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        editText.setGravity(Gravity.CENTER);
                        editText.setId(R.id.Alt_email);
                        linearLayout.addView(editText);
                    editMailCount++;

                  }
               // editMailcount++;
        }});

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toolphonenumber1 = (EditText) findViewById(R.id.Home_phonenumber);
                toolphonenumber2 = (EditText) findViewById(R.id.Work_phonenumber);
                altemail = (EditText) findViewById(R.id.Alt_email);

                final String phonenumber = toolphonenumber.getText().toString();

                if (isValidMobile(phonenumber)) {
                    final Context context = v.getContext().getApplicationContext();
                    // context.getApplicationContext()
                    ClientConfiguration clientConfiguration = new ClientConfiguration();
                    CognitoUserPool userPool = new CognitoUserPool(context, "us-west-2_pIbJesMDx", "7c1s6iqotm4q1vebmd42pm75fk", "807us18ln1v7oemepcjcaf5bn1s0ikj4o1oeakff4mkbkdakh79", Regions.DEFAULT_REGION);
                    CognitoUserAttributes userAttributes = new CognitoUserAttributes();

                    // Add the user attributes. Attributes are added as key-value pairs
                    // Adding user's given name.
                    // Note that the key is "given_name" which is the OIDC claim for given name
                    userAttributes.addAttribute("name", userName);

                    // Adding user's phone number
                    userAttributes.addAttribute("phone_number", "+1" + phonenumber);

                    // Adding user's email address
                    userAttributes.addAttribute("email", email);

                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try  {

                                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                                        getApplicationContext(),
                                        "us-west-2:22a12db0-c9fa-4dfb-b535-41257f688aaf", // Identity Pool ID
                                        Regions.US_WEST_2 // Region
                                );
                                LambdaInvokerFactory factory = new LambdaInvokerFactory(getApplicationContext(),
                                        Regions.US_EAST_1,
                                        credentialsProvider);
                                AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                                ddbClient.setRegion(Region.getRegion(Regions.US_WEST_2));
                                DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

                                details.setPhonenumber("+1"+phonenumber);
                                if(toolphonenumber1 !=null && toolphonenumber1.getText() !=null) {
                                    details.setPhonenumber1("+1" + toolphonenumber1.getText().toString());
                                }
                                if(toolphonenumber2 !=null && toolphonenumber2.getText() !=null) {
                                    details.setPhonenumber2("+1" + toolphonenumber2.getText().toString());
                                }
                                if(altemail !=null && altemail.getText() !=null) {
                                    details.setAlternateEmail(altemail.getText().toString());
                                }
                                details.setUsername(userName);
                                details.setEmail(email);
                                details.setPassword(password);
                                details.setStatus("NotVerified");
                                mapper.save(details);
                                // addToDB(phonenumber,email,password,userName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();

                    userPool.signUpInBackground(phonenumber, password, userAttributes, null, signUpHandler);
                } else {
                    Toast.makeText(ToolsActivity.this, "please enter valid phone number", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    SignUpHandler signUpHandler = new SignUpHandler() {

        @Override
        public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            if (userConfirmed) {
                showDialogMessage("Sign up successful!", confiruser + " has been Confirmed", true);
            } else {
                // The user has already been confirmed
                confirmSignUp(cognitoUserCodeDeliveryDetails);
            }
            beginUpload(imagePath);
        }

        @Override
        public void onFailure(Exception exception) {
            showDialogMessage("Sign up failed!", confiruser, true);
        }
    };

    private void confirmSignUp(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
        Intent intent = new Intent(ToolsActivity.this, SignUpConfirmActivity.class);
        intent.putExtra("name", confiruser);
        intent.putExtra("phonenumber",toolphonenumber.getText().toString());
        startActivity(intent);
    }





    private boolean isValidMobile(String phone)
    {
        boolean check=false;
        if(phone.length() != 10)
        {
            check = false;
        }
        else
        {
            check = true;
        }
        return check;
    }

    private void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if(exit) {
                        exit(confiruser);
                    }
                } catch (Exception e) {
                    if(exit) {
                        exit(confiruser);
                    }
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    /*
* Begins to upload the file specified by the file path.
*/
    private void beginUpload(String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            TransferObserver observer = transferUtility.upload("uhcl.socialmedia.app", "+1"+toolphonenumber.getText().toString()+".jpg",
                    file);
        /*/file.getName()
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */
            // observer.setTransferListener(new UploadListener());
        }
    }

    private void exit(String uname) {
        exit(uname);
    }

}