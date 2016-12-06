package com.uhcl.team5.socialmediateam5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.Map;

/**
 * Created by ABILASH on 11/26/2016.
 */

public class CreateEvent extends AppCompatActivity {

    private String phoneNumber,selectNames,selectedNumbers,name;
    private TextView frndListView= null;
    private EditText eventName=null , eventDesc = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone");
        selectNames = intent.getStringExtra("selectedNames");
        selectedNumbers = intent.getStringExtra("selectedNumbers");
        name = intent.getStringExtra("name");
        frndListView = (TextView) findViewById(R.id.create_event_friendlist);
        eventName = (EditText) findViewById(R.id.create_event_name);
        eventDesc = (EditText) findViewById(R.id.create_event_desc);
        frndListView.setText(selectNames);
        Button button = (Button) findViewById(R.id.create_event_create);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEventDetails();
                Intent intent = new Intent(CreateEvent.this, DisplayUserDetails.class);
                intent.putExtra("phone", phoneNumber);
                intent.putExtra("name", name);
                startActivity(intent);
            }

        });


    }

    public void saveEventDetails() {

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
            EventDetails eventDetails = new EventDetails();
            int id = getMaxEventID();
            eventDetails.setId(String.valueOf(id+1));
            eventDetails.setAdmin(phoneNumber);
            eventDetails.setEventName(eventName.getEditableText().toString());
            eventDetails.setEventDesc(eventDesc.getEditableText().toString());
            eventDetails.setMembers(selectedNumbers);
            mapper.save(eventDetails);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getMaxEventID(){

        int max = 0;
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
                    .withTableName("Events");
            ScanResult result = ddbClient.scan(scanRequest);
            for (Map<String, AttributeValue> item : result.getItems()){
                String ID = item.get("ID").getS();
                if (Integer.valueOf(ID) > max){
                    max = Integer.valueOf(ID);
                }

            }

        }catch(Exception e) {
            e.printStackTrace();
            Log.i("Exception ","");
        }
        return max;
    }



}
