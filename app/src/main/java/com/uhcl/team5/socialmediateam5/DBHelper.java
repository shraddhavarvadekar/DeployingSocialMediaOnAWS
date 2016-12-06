package com.uhcl.team5.socialmediateam5;

/**
 * Created by shrad on 10/20/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper  extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "USerDetails.db";

    public DBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}/*{
        //All necessary tables you like to create will create here

        String CREATE_TABLE_STUDENT = "CREATE TABLE " + UserDetails.TABLE  + "("
                + UserDetails.KEY_name + " TEXT, "
                + UserDetails.KEY_password + " TEXT, "
                + UserDetails.KEY_phone + " TEXT, "
                + UserDetails.KEY_status + " TEXT, "
                + UserDetails.KEY_email + " TEXT )";

        db.execSQL(CREATE_TABLE_STUDENT);

    }*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){} /*{
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + UserDetails.TABLE);

        // Create tables again
        onCreate(db);

    }*/

    public void insert(UserDetails user){} /*{

        //Open connection to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //  values.put(Student.KEY_age, student.age);
        values.put(UserDetails.KEY_email,user.getEmail());
        values.put(UserDetails.KEY_name, user.getUsername());
        values.put(UserDetails.KEY_password,user.getPassword());
        values.put(UserDetails.KEY_phone,user.getPhonenumber());
        values.put(UserDetails.KEY_status,user.VALID);

        // Inserting Row
        db.insert(UserDetails.TABLE, null, values);
        db.close(); // Closing database connection
    }*/

    /*  public UserDetails getStudentById(int Id){
          SQLiteDatabase db = this.getReadableDatabase();
          String selectQuery =  "SELECT  " +
                  UserDetails.KEY_ID + "," +
                  UserDetails.KEY_name + "," +
                  UserDetails.KEY_email + "," +
                  UserDetails.KEY_age +
                  " FROM " + UserDetails.TABLE
                  + " WHERE " +
                  UserDetails.KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

          int iCount =0;
          UserDetails user = new UserDetails();

          Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(Id) } );

          if (cursor.moveToFirst()) {
              do {
                  user.user =cursor.getInt(cursor.getColumnIndex(UserDetails.KEY_ID));
                  user.name =cursor.getString(cursor.getColumnIndex(UserDetails.KEY_name));
                  user.email  =cursor.getString(cursor.getColumnIndex(UserDetails.KEY_email));
                  user.age =cursor.getInt(cursor.getColumnIndex(UserDetails.KEY_age));

              } while (cursor.moveToNext());
          }

          cursor.close();
          db.close();
          return student;
      }
  */

}