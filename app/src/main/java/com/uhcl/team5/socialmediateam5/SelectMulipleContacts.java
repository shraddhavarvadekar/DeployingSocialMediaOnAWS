package com.uhcl.team5.socialmediateam5;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
 * Created by ABILASH on 11/25/2016.
 */

public class SelectMulipleContacts extends AppCompatActivity {
    private List<String> dynamoDbPhoneNums = new ArrayList<String>();
    private Map<String,String> contactsMap = new HashMap<String,String>();
    private ListView listNames;
    private String phoneNumber,name;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_select);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        listNames = (ListView) findViewById(R.id.multi_select_list);
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone");
        name = intent.getStringExtra("name");
        showContacts();
        Button button = (Button) findViewById(R.id.multi_select_next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer names = new StringBuffer();
                StringBuffer numbers = new StringBuffer();
                SparseBooleanArray checked = listNames.getCheckedItemPositions();
                ArrayList<String> selectedItems = new ArrayList<String>();
                for (int i = 0; i < checked.size(); i++) {
                    int position = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i))
                        selectedItems.add(adapter.getItem(position));
                }
                String[] outputStrArr = new String[selectedItems.size()];
                for (int i = 0; i < selectedItems.size(); i++) {
                    outputStrArr[i] = selectedItems.get(i);
                    names.append(outputStrArr[i]+",");
                    numbers.append(contactsMap.get(outputStrArr[i])+",");
                }
                Intent intent = new Intent(SelectMulipleContacts.this,
                        CreateEvent.class);
                intent.putExtra("phone",phoneNumber);
                intent.putExtra("selectedNames",names.toString());
                intent.putExtra("selectedNumbers",numbers.toString());
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });


    }

    private void showContacts() {
        String frndlist =  getFriendListFromDynamoDb();
        Log.i("showContacts", frndlist);
        String[] contactlist = frndlist.split(",");
        Log.i("showContacts: length", String.valueOf(contactlist.length));
        for(int i =0; i <contactlist.length ; i++)
        {
            Log.i("showContacts:: for loop", contactlist[i]);
            String[] contact = contactlist[i].split(":");
            contactsMap.put(contact[0],contact[1]);
        }
        Set<String> contacts = contactsMap.keySet();
        List<String> contactslist = new ArrayList<String>();
        for(String str:contacts){
            contactslist.add(str);
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, contactslist);
        listNames.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listNames.setAdapter(adapter);
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
                if (item.get("phone_number").getS().equalsIgnoreCase(phoneNumber)) {
                    res = item.get("friend_list").getS();
                    return res;
                }
            }

        }catch(Exception e) {
            e.printStackTrace();
            Log.i("Exception ","");
        }
        return res;
    }

}
