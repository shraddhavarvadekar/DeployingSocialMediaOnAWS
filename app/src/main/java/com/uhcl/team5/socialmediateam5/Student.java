package com.uhcl.team5.socialmediateam5;

/**
 * Created by ABILASH on 10/23/2016.
 */

import java.io.Serializable;

/**
 * Created by shrad on 10/21/2016.
 */
public class Student implements Serializable{
    // Labels table name
    public static final String TABLE = "Student";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_name = "name";
    public static final String KEY_email = "email";
    public static final String KEY_age = "age";
    public static final String KEY_phone = "phonenumber";
    public static final String KEY_password = "password";
    public static final String KEY_status = "status";
    public static final String VALID = "valid";
    public static final String NOTVALID = "notvalid";

    // property help us to keep data
    public int student_ID;
    public String name;
    public String email;
    public int age;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String status;

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String phonenumber;
    public String password;

    public int getStudent_ID() {
        return student_ID;
    }

    public void setStudent_ID(int student_ID) {
        this.student_ID = student_ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}