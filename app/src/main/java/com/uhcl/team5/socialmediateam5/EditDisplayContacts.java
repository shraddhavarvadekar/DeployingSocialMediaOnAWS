package com.uhcl.team5.socialmediateam5;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shrad on 11/6/2016.
 */

public class EditDisplayContacts extends AppCompatActivity{

    private List<String> dynamoDbPhoneNums = new ArrayList<String>();
    private TextView getnumber=null;
    private EditText getname=null, getemail=null,getnumber1=null,getnumber2=null,getemail1=null;
    ImageButton getfb,getinsta,getpin,gettwit;
    private Context ctx = this;
    String fb,twit,inst,pin,phone,user,email,tempphone;
    String changefid=" ",changetwitid=" ",changeinstaid=" ",changepinid=" ",chanegemail=" ",changename=" ",changephone1=" ",changephone2=" ",chanegalternateemail=" ";
    UserDetails details;

    //DBHelper
    private DBHelper dbHelper = new DBHelper(ctx);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcontact);
        Bundle b = getIntent().getExtras();
        final String phone = b.getString("phone");
        Log.i("ContactDetails :: ",phone);
        details = getUserDetails(phone);


        getemail = (EditText) findViewById(R.id.tvView);
        getemail1 = (EditText) findViewById(R.id.emailView);
        getname = (EditText) findViewById(R.id.nameView);
        getnumber = (TextView) findViewById(R.id.numberView);

        getnumber1 = (EditText) findViewById(R.id.numberView1);
        getnumber2 = (EditText) findViewById(R.id.numberView2);

        getfb = (ImageButton) findViewById(R.id.fbId);
        getinsta = (ImageButton) findViewById(R.id.instaId);
        getpin = (ImageButton) findViewById(R.id.pinId);
        gettwit = (ImageButton) findViewById(R.id.twitId);

        if(b!=null) {
            email = b.getString("email");
            getemail.setText(Html.fromHtml(email));
        }

        if(details.getAlternateEmail() == null){
            getemail1.setVisibility(View.INVISIBLE);
        }
        else{
            getemail1.setText(Html.fromHtml(details.getAlternateEmail()));
        }

        getnumber.setText(Html.fromHtml("<b>" + phone + "</b>"));

        if(details.getPhonenumber1() == null) {
            getnumber1.setVisibility(View.INVISIBLE);
        }
        else{
            getnumber1.setText(Html.fromHtml(details.getPhonenumber1()));
        }
        if(details.getPhonenumber2() == null) {
            getnumber2.setVisibility(View.INVISIBLE);
        }
        else{
            getnumber2.setText(Html.fromHtml(details.getPhonenumber2()));
        }

        getname.setText(Html.fromHtml(details.getUsername()));

        fb = b.getString("face");
        if(fb!=null) {
            getfb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                    alertDialogBuilder.setTitle("Redirect");
                    alertDialogBuilder.setMessage("Enter your social media user id");

                    final EditText userInput = new EditText(ctx);
                    alertDialogBuilder.setView(userInput);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            changefid = userInput.getEditableText().toString();
                            String path = "https://facebook.com/" + fb;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            //i.setData(Uri.parse(path));
                            //startActivity(i);
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        }
        else{
            getfb.setVisibility(View.INVISIBLE);
        }

        twit = b.getString("twit");
        if(twit!=null) {
            gettwit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                    alertDialogBuilder.setTitle("Redirect");
                    alertDialogBuilder.setMessage("Enter your social media user id");

                    final EditText userInput = new EditText(ctx);
                    alertDialogBuilder.setView(userInput);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            changetwitid = userInput.getEditableText().toString();
                            String path = "https://twitter.com/" + twit;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            //i.setData(Uri.parse(path));
                            //startActivity(i);
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        }
        else{
            gettwit.setVisibility(View.INVISIBLE);
        }

        inst = b.getString("insta");
        if(inst!=null) {
            getinsta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                    alertDialogBuilder.setTitle("Redirect");
                    alertDialogBuilder.setMessage("Enter your social media user id");

                    final EditText userInput = new EditText(ctx);
                    alertDialogBuilder.setView(userInput);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            changeinstaid = userInput.getEditableText().toString();
                            String path = "https://instagram.com/" + inst;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            //i.setData(Uri.parse(path));
                            //startActivity(i);
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        }
        else{
            getinsta.setVisibility(View.INVISIBLE);
        }

        pin = b.getString("pin");
        if(pin!=null) {
            getpin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                    alertDialogBuilder.setTitle("Redirect");
                    alertDialogBuilder.setMessage("Enter your social media user id");

                    final EditText userInput = new EditText(ctx);
                    alertDialogBuilder.setView(userInput);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            changepinid = userInput.getEditableText().toString();
                            String path = "https://pinterest.com/" + pin;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            //i.setData(Uri.parse(path));
                            //startActivity(i);
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        }
        else{
            getpin.setVisibility(View.INVISIBLE);
        }
tempphone = phone;
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserDetailFromDynamoDb(tempphone);
            }
        });
    }


    public UserDetails getUserDetails(String phoneNumber){

        UserDetails details;
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

        details = mapper.load(UserDetails.class,phoneNumber);
        return details;

    }


    public void setUserDetailFromDynamoDb(String phone){

        try {

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

            ScanRequest scanRequest = new ScanRequest()
                    .withTableName("users");
            ScanResult result = ddbClient.scan(scanRequest);

            for (Map<String, AttributeValue> item : result.getItems()){
                String p = item.get("phonenumber").getS();
                if(phone.equalsIgnoreCase(p)) {

                    //email
                    if(chanegemail != null){
                        chanegemail = getemail.getEditableText().toString();
                        item.put("email", new AttributeValue().withS(chanegemail));
                    }
                    else {
                        item.put("email", new AttributeValue().withS(email));
                    }

                    //alternate email
                    if(chanegalternateemail != null){
                        chanegalternateemail = getemail1.getEditableText().toString();
                        item.put("Alternate_Email", new AttributeValue().withS(chanegalternateemail));
                    }
                    else {
                        item.put("Alternate_Email", new AttributeValue().withS(" "));
                    }

                    //name
                    if(changename != null){
                        changename = getname.getEditableText().toString();
                        item.put("name", new AttributeValue().withS(changename));
                    }
                    else{
                        item.put("name", new AttributeValue().withS(details.getUsername()));
                    }

                    //phone number 1
                    if(details.getPhonenumber1()==null){
                        item.put("phonenumber1", new AttributeValue().withS(" "));
                    }
                    else{
                        changephone1 = getnumber1.getEditableText().toString();
                        item.put("phonenumber1", new AttributeValue().withS(changephone1));
                    }

                    //phone number 2
                    if(details.getPhonenumber2()==null){
                        item.put("phonenumber2", new AttributeValue().withS(" "));
                    }
                    else{
                        changephone2 = getnumber2.getEditableText().toString();
                        item.put("phonenumber2", new AttributeValue().withS(changephone2));
                    }

                    //facebook
                    if(details.getFid()==null){
                        item.put("fid", new AttributeValue().withS(" "));
                    }
                    else {
                        item.put("fid", new AttributeValue().withS(changefid));
                    }

                    //pinterest
                    if(details.getPinid() == null){
                        item.put("pinid", new AttributeValue().withS(" "));
                    }
                    else {
                        item.put("pinid", new AttributeValue().withS(changepinid));
                    }

                    //twitter
                    if(details.getTwitid() == null){
                        item.put("twitid", new AttributeValue().withS(" "));
                    }
                    else {
                        item.put("twitid", new AttributeValue().withS(changetwitid));
                    }

                    //instagram
                    if(details.getInstaid() == null){
                        item.put("instaid", new AttributeValue().withS(" "));
                    }
                    else {
                        item.put("instaid", new AttributeValue().withS(changeinstaid));
                    }
                }
                PutItemRequest putItemRequest = new PutItemRequest("users", item);

                PutItemResult putItemResult = ddbClient.putItem(putItemRequest);
            }

        }catch(Exception e) {
            e.printStackTrace();
            Log.i("Exception ","");
        }
    }
}
