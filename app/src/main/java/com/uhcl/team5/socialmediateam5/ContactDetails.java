package com.uhcl.team5.socialmediateam5;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.File;

/**
 * Created by shrad on 11/6/2016.
 */

public class ContactDetails extends AppCompatActivity{

    private TextView getname=null, getemail=null, getnumber=null,getnumber1=null,getnumber2=null,getemail1=null;
    ImageButton getfb,getinsta,getpin,gettwit;
    ImageView imageView;
    private Context ctx = this;
    String fb,twit,inst,pin,phone,user,email;

    //DBHelper
    private DBHelper dbHelper = new DBHelper(ctx);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Bundle b = getIntent().getExtras();
        String phone = b.getString("phone");
        Log.i("ContactDetails :: ",phone);
        UserDetails details = getUserDetails(phone);


        getemail = (TextView) findViewById(R.id.emailView);
        getemail1 = (TextView) findViewById(R.id.tvView);
        getname = (TextView) findViewById(R.id.nameView);
        getnumber = (TextView) findViewById(R.id.numberView);

        getnumber1 = (TextView) findViewById(R.id.numberView1);
        getnumber2 = (TextView) findViewById(R.id.numberView2);

        getfb = (ImageButton) findViewById(R.id.fbId);
        getinsta = (ImageButton) findViewById(R.id.instaId);
        getpin = (ImageButton) findViewById(R.id.pinId);
        gettwit = (ImageButton) findViewById(R.id.twitId);

        File imgFile = new  File("/data/user/0/com.uhcl.team5.socialmediateam5/files/"+phone+".jpg");

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.imageView3);

            myImage.setImageBitmap(myBitmap);

        }

        getname.setText(details.getUsername());
        getemail.setText(details.getEmail());

        if(details.getAlternateEmail() == null){
            String tempemail = getemail1.getText().toString();
            tempemail.trim();
            getemail1.setText(tempemail);
        }
        else {
            getemail1.setText(details.getAlternateEmail());
        }
        getnumber.setText(phone);

        if(details.getPhonenumber1() == null){
            String tempphone1 = getnumber1.getText().toString();
            tempphone1.trim();
          //  getnumber1.setText(Html.fromHtml("<b>" + tempphone1 + "</b>"));
            getnumber1.setText(tempphone1);
        }
        else {
            getnumber1.setText(details.getPhonenumber1());
        }

        if(details.getPhonenumber2() == null){
            String tempphone2 = getnumber2.getText().toString();
            tempphone2.trim();
            getnumber2.setText(tempphone2);
        }
        else {
            getnumber2.setText(details.getPhonenumber2());
        }

        fb = details.getFid();
        if(fb!=null) {
            //fb = b.getString("face");
            ImageButton label = (ImageButton) findViewById(R.id.fbId);
            //label.setText(Html.fromHtml("<b>" + details.getFid() + "</b>"));
            label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = "https://facebook.com/" + fb;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(path));
                    startActivity(i);
                }
            });
        }
        else{
            getfb.setVisibility(View.INVISIBLE);
            //getfb.setText(Html.fromHtml("<b>Not Connected </b>"));
        }

        twit = details.getTwitid();
        if(twit!=null) {
            //twit = b.getString("twit");
           // gettwit.setText(Html.fromHtml("<b>" + details.getTwitid() + "</b>"));
            gettwit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = "https://twitter.com/" + twit;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(path));
                    startActivity(i);
                }
            });
        }
        else{
            gettwit.setVisibility(View.INVISIBLE);
            //gettwit.setText(Html.fromHtml("<b>Not Connected </b>"));
        }

        inst = details.getInstaid();
        if(inst!=null) {
            //inst = b.getString("insta");
            //getinsta.setText(Html.fromHtml("<b>" + details.getInstaid() + "</b>"));
            getinsta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = "https://instagram.com/" + inst;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(path));
                    startActivity(i);
                }
            });
        }
        else{
            getinsta.setVisibility(View.INVISIBLE);
            //getinsta.setText(Html.fromHtml("<b>Not Connected </b>"));
        }

        pin = details.getPinid();
        if(pin!=null) {
            //pin = b.getString("pin");
           // getpin.setText(Html.fromHtml("<b>" + details.getPinid() + "</b>"));
            getpin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = "https://pinterest.com/" + pin;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(path));
                    startActivity(i);
                }
            });
        }
        else{
            getpin.setVisibility(View.INVISIBLE);
            //getpin.setText(Html.fromHtml("<b>Not Connected </b>"));
        }

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
}
