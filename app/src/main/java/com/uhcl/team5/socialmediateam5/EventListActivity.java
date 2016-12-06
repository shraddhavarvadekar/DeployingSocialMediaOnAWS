package com.uhcl.team5.socialmediateam5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ABILASH on 11/26/2016.
 */

public class EventListActivity extends AppCompatActivity {

    private ListView eventList;
    private String phoneNumber;
    ArrayAdapter<String> adapter;
    Map<String,String> eventsMap = new HashMap<String,String>();
    List<String> eventnameList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_events);
        eventList = (ListView) findViewById(R.id.event_list);
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone");
        getEvents();
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                String selectedFromList =(String) (eventList.getItemAtPosition(myItemInt));
                Intent intent = new Intent(EventListActivity.this,DisplayEventDetails.class);
                String eventId = null;
                Log.i("Selected :: ",selectedFromList);
                for(Map.Entry entry: eventsMap.entrySet()){
                    if(selectedFromList.equals(entry.getValue())){
                        eventId = (String)entry.getKey();
                        break; //breaking because its one to one map
                    }
                }
                intent.putExtra("phone",phoneNumber);
                intent.putExtra("eventID",eventId);
                startActivity(intent);
            }
        });
    }


    public void getEvents(){

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
                String admin = item.get("Admin").getS();
                String members = item.get("Members").getS();
                String eventName = item.get("Event_Name").getS();
                if(admin.equalsIgnoreCase(phoneNumber)) {
                    eventsMap.put(item.get("ID").getS(),eventName);
                }
                String[] memberlist = members.split(",");
                for(int i=0;i<memberlist.length;i++){
                    if(memberlist[i].equalsIgnoreCase(phoneNumber)) {
                        eventsMap.put(item.get("ID").getS(), eventName);
                        break;
                    }
                }
            }

            Set<String> eventIDs = eventsMap.keySet();
            List<String> contactslist = new ArrayList<String>();
            for(String str:eventIDs){
                eventnameList.add(eventsMap.get(str));
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventnameList);
            eventList.setAdapter(adapter);

        }catch(Exception e) {
            e.printStackTrace();
            Log.i("Exception ","");
        }
    }



}