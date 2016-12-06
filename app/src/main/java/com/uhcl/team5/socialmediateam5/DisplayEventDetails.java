package com.uhcl.team5.socialmediateam5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

/**
 * Created by ABILASH on 11/27/2016.
 */

public class DisplayEventDetails extends AppCompatActivity {

    TextView eventName,admin,eventDesc,eventMembers;
    String eventId,phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_event);

        eventName = (TextView) findViewById(R.id.display_event_name_value);
        admin = (TextView) findViewById(R.id.display_event_admin_value);
        eventDesc = (TextView) findViewById(R.id.display_event_desc_value);
        eventMembers = (TextView) findViewById(R.id.display_event_members_value);

        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone");
        eventId = intent.getStringExtra("eventID");
       // getEvents();
        EventDetails eventDetails = getEventDetails(eventId);
        displayEventDetails(eventDetails);
    }



    public void displayEventDetails(EventDetails details){

        eventName.setText(details.getEventName());
        admin.setText(getUserDetails(details.getAdmin()).getUsername());
        eventDesc.setText(details.getEventDesc());

        String members = details.getMembers();
        String[] memberslist = members.split(",");
        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<memberslist.length;i++){
            buffer.append(getUserDetails(memberslist[i]).getUsername()+"\n");
        }
        eventMembers.setText(buffer.toString());

    }


    public EventDetails getEventDetails(String eventId){

        EventDetails details;
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

        details = mapper.load(EventDetails.class,eventId);
        return details;

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
