package com.uhcl.team5.socialmediateam5;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by ABILASH on 11/26/2016.
 */
@DynamoDBTable(tableName = "Events")
public class EventDetails {

    private String Id;
    private String admin;
    private String eventName;
    private String eventDesc;
    private String cretedDate;
    private String members;


    @DynamoDBHashKey(attributeName = "ID")
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @DynamoDBAttribute(attributeName = "Admin")
    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    @DynamoDBAttribute(attributeName = "Event_Name")
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @DynamoDBAttribute(attributeName = "Event_Desc")
    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    @DynamoDBAttribute(attributeName = "Members")
    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    @DynamoDBAttribute(attributeName = "Created_Date")
    public String getCretedDate() {
        return cretedDate;
    }

    public void setCretedDate(String cretedDate) {
        this.cretedDate = cretedDate;
    }

}
