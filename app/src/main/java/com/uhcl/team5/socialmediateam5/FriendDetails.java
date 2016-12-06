package com.uhcl.team5.socialmediateam5;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by ABILASH on 11/13/2016.
 */
@DynamoDBTable(tableName = "Friend_list")
public class FriendDetails {

    private String phonenumber;
    private String friendList;

    @DynamoDBHashKey(attributeName = "phone_number")
    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    @DynamoDBAttribute(attributeName = "friend_list")
    public String getFriendList() {
        return friendList;
    }

    public void setFriendList(String friendList) {
        this.friendList = friendList;
    }
}
