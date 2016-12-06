package com.uhcl.team5.socialmediateam5;

//import com.amazonaws.mobileconnectors.dynamodbv2.datamodeling.*;import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;


/**
 * Created by ABILASH on 10/18/2016.
 */


@DynamoDBTable(tableName = "users")
public class UserDetails {

    private String phonenumber;
    private String phonenumber1;
    private String phonenumber2;
    private String username;
    private String email;
    private String alternateemail;
    private String password;
    private String status;
    private String fid;
    private String instaid;
    private String twitid;
    private String pinid;

    public static final String TABLE = "users";

  /*  // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_name = "name";
    public static final String KEY_email = "email";
    public static final String KEY_alt_email = "Alternate_email";
    public static final String KEY_age = "age";
    public static final String KEY_phone = "phonenumber";
    public static final String KEY_password = "password";
    public static final String KEY_status = "status";
    public static final String VALID = "valid";
    public static final String NOTVALID = "notvalid";*/

    @DynamoDBHashKey(attributeName = "phonenumber")
    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    @DynamoDBAttribute(attributeName = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDBAttribute(attributeName = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute(attributeName = "Alternate_Email")
    public String getAlternateEmail() {
        return alternateemail;
    }

    public void setAlternateEmail(String alternateemail) {
        this.alternateemail = alternateemail;
    }

    @DynamoDBAttribute(attributeName = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @DynamoDBAttribute(attributeName = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDBAttribute(attributeName = "fid")
    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    @DynamoDBAttribute(attributeName = "instaid")
    public String getInstaid() {
        return instaid;
    }

    public void setInstaid(String instaid) {
        this.instaid = instaid;
    }

    @DynamoDBAttribute(attributeName = "twitid")
    public String getTwitid() { return twitid; }

    public void setTwitid(String twitid) {
        this.twitid = twitid;
    }

    @DynamoDBAttribute(attributeName = "pinid")
    public String getPinid() {
        return pinid;
    }

    public void setPinid(String pinid) {
        this.pinid = pinid;
    }

    @DynamoDBAttribute(attributeName = "phonenumber1")
    public String getPhonenumber1() {
        return phonenumber1;
    }

    public void setPhonenumber1(String phonenumber1) {
        this.phonenumber1 = phonenumber1;
    }
    @DynamoDBAttribute(attributeName = "phonenumber2")
    public String getPhonenumber2() {
        return phonenumber2;
    }

    public void setPhonenumber2(String phonenumber2) {
        this.phonenumber2 = phonenumber2;
    }

}