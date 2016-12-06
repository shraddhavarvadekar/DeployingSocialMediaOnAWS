package com.uhcl.team5.socialmediateam5;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by shrad on 9/28/2016.
 */
public class DisplayUserDetails extends AppCompatActivity {
    // The ListView
    private ListView lstNames;
    private TextView username;
    ArrayAdapter<String> adapter;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static List<String> dynamoDbPhoneNums = new ArrayList<String>();
    private Map<String,String> contactsMap = new HashMap<String,String>();
    private Map<String,String> displaycontactsMap = new HashMap<String,String>();
    private String name;
    private TransferUtility transferUtility;
    private String phoneNumber;

    private Button button,button1;
    CognitoUser coguser;

    private void init() {
        // Get the user name
/*Intent i = getIntent();
        String u = i.getStringExtra("user");
        AppHelper.getPool().getUser(u);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user);
        long lStartTime = System.currentTimeMillis();
        transferUtility = AppHelper.getTransferUtility(this);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        this.lstNames = (ListView) findViewById(R.id.list);

        // Read and show the contacts
        // Find the list view
        this.lstNames = (ListView) findViewById(R.id.list);
        username = (TextView) findViewById(R.id.welcome_username);

        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone");
        String email = intent.getStringExtra("email");
        name = intent.getStringExtra("name");
        username.setText(Html.fromHtml("<b> Welcome " +name+ "</b>"));
        showContacts();
        long lEndTime = System.currentTimeMillis();
        long difference = lEndTime - lStartTime;
        Log.i("Time taken to load contacts :",String.valueOf(difference));
        lstNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                String selectedFromList =(String) (lstNames.getItemAtPosition(myItemInt));
                Intent intent = new Intent(DisplayUserDetails.this,ContactDetails.class);
                Log.i("Selected :: ",selectedFromList);
                String phoneNumber = displaycontactsMap.get(selectedFromList);
                intent.putExtra("phone",phoneNumber);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }
    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                resfresh();
                Toast.makeText(getApplicationContext(),"Refreshed Successfully" +
                        "",Toast.LENGTH_LONG).show();
                return true;
            case R.id.item2:
                editYourDetails();
                return true;
            case R.id.item3:
                signOut();
                return true;
            case R.id.item4:
                Intent intent = new Intent(DisplayUserDetails.this,SelectMulipleContacts.class);
                intent.putExtra("phone",phoneNumber);
                intent.putExtra("name",name);
                startActivity(intent);
                return true;
            case R.id.item5:
                displayEvents();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void resfresh(){
        getUserDetailFromDynamoDb();
        getContacts();
        updateFriendList();
        showContacts();
    }


    private void displayEvents(){
        Intent eventIntent = new Intent(DisplayUserDetails.this,EventListActivity.class);
        eventIntent.putExtra("phone",phoneNumber);
        startActivity(eventIntent);
    }


   private void editYourDetails() {
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

       UserDetails details = mapper.load(UserDetails.class,phoneNumber);

       Intent intent = new Intent(DisplayUserDetails.this,EditContactDetails.class);
       intent.putExtra("email",details.getEmail());
       intent.putExtra("phone",details.getPhonenumber());
       intent.putExtra("insta",details.getInstaid());
       intent.putExtra("twit",details.getTwitid());
       intent.putExtra("face",details.getFid());
       intent.putExtra("name",details.getUsername());
       intent.putExtra("pin",details.getPinid());
       startActivity(intent);
   }





    private void signOut() {
        Intent i = getIntent();
        String u = i.getStringExtra("user");
        AppHelper.getPool().getUser(u).signOut();
       // coguser.signOut();
        Log.i("Logout:signout()","logout");
        exit();
    }
    private void exit () {
        Intent intent = new Intent(DisplayUserDetails.this,LoginActivity.class);
        if(phoneNumber == null)
            phoneNumber = "";
             startActivity(intent);
    }

    private void getContacts() {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
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
                   //     Log.i("getContactNames:: Name & Number ",name+":"+num);
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
      //  Log.i("Time taken : ", String.valueOf(difference));

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
              //      Log.i("getUserDetailFromDynamoDb::phonenumber",item.get("phonenumber").getS());
                }
            }

        }catch(Exception e) {
            e.printStackTrace();
            Log.i("Exception ","");
        }
    }

    public void updateFriendList() {

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
            FriendDetails frndDetails = mapper.load(FriendDetails.class, phoneNumber);
            Log.i("saveFriendList::","saveFriendList");
            StringBuffer friendList = new StringBuffer();
            for (Map.Entry<String, String> entry : contactsMap.entrySet()) {
            //    Log.i("saveFriendList::",entry.getKey()+":"+entry.getValue());
                friendList.append(entry.getKey()+":"+entry.getValue());
                friendList.append(",");
                beginDownload(entry.getValue());

            }
          //  FriendDetails frndDetails = new FriendDetails();
         //   frndDetails.setPhonenumber(phoneNumber);
            frndDetails.setFriendList(friendList.toString());
            mapper.save(frndDetails);
            beginDownload(phoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void beginDownload(String key) {
        // Location to download files from S3 to. You can choose any accessible

        try {
            // file.
            // key = key.substring(2);
            File file = new File(getFilesDir().toString() + "/" + key+".jpg");
          //  Log.i("beginDownload ",key+":"+getFilesDir().toString()+"/"+key+"");            // Initiate the download
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




    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
           String frndlist =  getFriendListFromDynamoDb();
         Log.i("showContacts", frndlist);
          String[] contactlist = frndlist.split(",");
        Log.i("showContacts: length", String.valueOf(contactlist.length));
         for(int i =0; i <contactlist.length ; i++)
         {
             Log.i("showContacts:: for loop", contactlist[i]);
             String[] contact = contactlist[i].split(":");
             displaycontactsMap.put(contact[0],contact[1]);

         }
            Set<String> contacts = displaycontactsMap.keySet();
            List<String> contactslist = new ArrayList<String>();
            for(String str:contacts){
                //Log.i("showContacts:: contact lsit", str);
                contactslist.add(str);
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactslist);
            lstNames.setAdapter(adapter);
        }



    public String getFriendListFromDynamoDb(){
        String res = "";

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
                    .withTableName("Friend_list");
            ScanResult result = ddbClient.scan(scanRequest);
            for (Map<String, AttributeValue> item : result.getItems()){
               //Log.i("getFriendListFromDynamoDb :: ",item.get("phone_number").getS());
                if (item.get("phone_number").getS().equalsIgnoreCase(phoneNumber)) {
                    //Log.i("getFriendListFromDynamoDb :: ",item.get("friend_list").getS());
                    res = item.get("friend_list").getS();
                    return res;
                   }
               // Log.i("Item",item.get("phonenumber").getS());
            }

            }catch(Exception e) {
             e.printStackTrace();
            Log.i("Exception ","");
        }
        return res;
        }

}
