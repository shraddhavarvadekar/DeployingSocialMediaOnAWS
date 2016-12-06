package com.uhcl.team5.socialmediateam5;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ABILASH on 9/19/2016.
 */
public class SignUpConfirmActivity extends AppCompatActivity {

    private EditText username;
    private EditText confCode;
    private Button confirm;
    private TextView reqCode;
    private String userName;
    private String phonenumber;
    private AlertDialog userDialog;

    private List<String> dynamoDbPhoneNums = new ArrayList<String>();
    private Map<String,String> contactsMap = new HashMap<String,String>();
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    final Context context = this;
    private DBHelper dbHelper = new DBHelper(context);

    private TransferUtility transferUtility;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_sign_up_confirm);
        //emailId = (TextView)findViewById(R.id.toolemail);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        username = (EditText) findViewById(R.id.editTextConfirmUserId);
        transferUtility = AppHelper.getTransferUtility(this);
        init();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                getUserDetailFromDynamoDb();
                getContacts();
                saveFriendList();
            }
        });

        thread.start();


    }

    public void init() {

        Intent intent = getIntent();
        userName = intent.getStringExtra("name");
        username.setText(userName);
        phonenumber = intent.getStringExtra("phonenumber");
        username.setText(phonenumber);


        confCode = (EditText) findViewById(R.id.editTextConfirmCode);
        confCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewConfirmCodeLabel);
                    label.setText(confCode.getHint());
                    confCode.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewConfirmCodeMessage);
                label.setText(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewConfirmCodeLabel);
                    label.setText("");
                }
            }
        });

        confirm = (Button) findViewById(R.id.confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendConfCode();
            }
        });

        reqCode = (TextView) findViewById(R.id.resend_confirm_req);
        reqCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqConfCode();
            }
        });


    }

    private void getContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            Map<String,String> contacts = getContactNames();
        }
        }


    private Map<String,String> getContactNames() {
        long lStartTime = System.currentTimeMillis();
        List<String> contacts = new ArrayList<>();
        // Get the ContentResolver
        ContentResolver cr = getContentResolver();
        // Get the Cursor of all the contacts
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // Move the cursor to first. Also check whether the cursor is empty or not.
        if (cursor.moveToFirst()) {
            // Iterate through the cursor
            do {
                // Get the contacts name
                String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                // Log.i("Name",name);
                Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contact_id}, null);
                if(cur.getCount() > 0)
                {
                    while(cur.moveToNext()) {

                        String num = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                     //   Log.i("getContactNames:: Name & Number ",name+":"+num);
                        if(dynamoDbPhoneNums.contains(num)) {
                          //  Log.i("getContactNames:: Name & Number ",name+":"+num);
                            contactsMap.put(name,num);
                        }
                    }

                }
                cur.close();
            } while (cursor.moveToNext());
        }
        // Close the curosor
        cursor.close();

        long lEndTime = System.currentTimeMillis();
        long difference = lEndTime - lStartTime;
       // Log.i("Time taken : ", String.valueOf(difference));

        return contactsMap;
    }

    public void getUserDetailFromDynamoDb(){

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

            ScanRequest scanRequest = new ScanRequest()
                    .withTableName("users");
            ScanResult result = ddbClient.scan(scanRequest);
            for (Map<String, AttributeValue> item : result.getItems()){
                String status = item.get("status").getS();
                if(status.equalsIgnoreCase("Verified")) {
                    dynamoDbPhoneNums.add(item.get("phonenumber").getS());
                  //  Log.i("getUserDetailFromDynamoDb::phonenumber",item.get("phonenumber").getS());
                }
            }

        }catch(Exception e) {
            e.printStackTrace();
            Log.i("Exception ","");
        }
    }


    public void saveFriendList() {

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
         //   Log.i("saveFriendList::","saveFriendList");
            StringBuffer friendList = new StringBuffer();
            for (Map.Entry<String, String> entry : contactsMap.entrySet()) {
           //     Log.i("saveFriendList::",entry.getKey()+":"+entry.getValue());
                friendList.append(entry.getKey()+":"+entry.getValue());
                friendList.append(",");
                beginDownload(entry.getValue());

            }
            FriendDetails frndDetails = new FriendDetails();
            frndDetails.setPhonenumber("+1"+phonenumber);
            frndDetails.setFriendList(friendList.toString());
            mapper.save(frndDetails);
            beginDownload("+1"+phonenumber);
            // addToDB(phonenumber,email,password,userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendConfCode() {
        userName = username.getText().toString();
        String confirmCode = confCode.getText().toString();

        if(confirmCode == null || confirmCode.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewConfirmCodeMessage);
            label.setText(confCode.getHint()+" cannot be empty");
            confCode.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        AppHelper.getPool().getUser(userName).confirmSignUpInBackground(confirmCode, true, confHandler);
    }

    private void reqConfCode() {
        userName = username.getText().toString();
        if(userName == null || userName.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
            label.setText(username.getHint()+" cannot be empty");
            username.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }
        AppHelper.getPool().getUser(userName).resendConfirmationCodeInBackground(resendConfCodeHandler);

    }


    GenericHandler confHandler = new GenericHandler() {
        @Override
        public void onSuccess() {


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
                        UserDetails details = mapper.load(UserDetails.class, "+1"+phonenumber);
                        details.setStatus("Verified");
                        mapper.save(details);
                      //  addToDB(phonenumber,userName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

            showDialogMessage("Success!",userName+" has been confirmed!");
        }


        @Override
        public void onFailure(Exception exception) {
            TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
            label.setText("Confirmation failed!");
            username.setBackground(getDrawable(R.drawable.text_border_error));
            label = (TextView) findViewById(R.id.textViewConfirmCodeMessage);
            label.setText("Confirmation failed!");
            confCode.setBackground(getDrawable(R.drawable.text_border_error));
            showDialogMessage("Confirmation failed", AppHelper.formatException(exception));
        }
    };

    VerificationHandler resendConfCodeHandler = new VerificationHandler() {
        @Override
        public void onSuccess(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            TextView mainTitle = (TextView) findViewById(R.id.textViewConfirmTitle);
            mainTitle.setText("Confirm your account");
            confCode = (EditText) findViewById(R.id.editTextConfirmCode);
            confCode.requestFocus();
            String message = "Code sent to "+cognitoUserCodeDeliveryDetails.getDestination()+" via "+cognitoUserCodeDeliveryDetails.getDeliveryMedium();
            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
           // showDialogMessage("Confirmation code sent.","Code sent to "+cognitoUserCodeDeliveryDetails.getDestination()+" via "+cognitoUserCodeDeliveryDetails.getDeliveryMedium()+".");
        }

        @Override
        public void onFailure(Exception exception) {
            TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
            label.setText("Confirmation code resend failed");
            username.setBackground(getDrawable(R.drawable.text_border_error));
            showDialogMessage("Confirmation code request has failed", AppHelper.formatException(exception));
        }
    };


    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    exit();
                } catch (Exception e) {
                    exit();
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void exit() {
     /*   Intent intent = new Intent();
        if(userName == null)
            userName = "";
        intent.putExtra("name",userName);
        setResult(RESULT_OK, intent);
        finish();*/
        Intent intent = new Intent(SignUpConfirmActivity.this, LoginActivity.class);
        intent.putExtra("name", userName);
        startActivity(intent);

    }


    private void addToDB(String phonenumber, String userName){
        //Adding details in the SQLite database
        UserDetails details = new UserDetails();
        details.setUsername(userName);
        //student.setEmail(emaildId);
        //student.setPassword(password);
        details.setPhonenumber(phonenumber);
        dbHelper.insert(details);
    }

    private void beginDownload(String key) {
        // Location to download files from S3 to. You can choose any accessible

        try {
            // file.
           // key = key.substring(2);
            File file = new File(getFilesDir().toString() + "/" + key+".jpg");
            Log.i("beginDownload ",key+":"+getFilesDir().toString()+"/"+key+"");            // Initiate the download
            TransferObserver observer = transferUtility.download("uhcl.socialmedia.app", key+".jpg", file);
        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */
            // observer.setTransferListener(new DownloadListener());
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}