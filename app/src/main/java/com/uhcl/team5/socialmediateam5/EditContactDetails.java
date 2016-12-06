package com.uhcl.team5.socialmediateam5;

/**
 * Created by ABILASH on 11/24/2016.
 */
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditContactDetails  extends AppCompatActivity {
    private List<String> dynamoDbPhoneNums = new ArrayList<String>();
    private TextView getnumber=null;
    private EditText getname=null, getemail1=null,getnumber1=null,getnumber2=null,getemail2=null;
    ImageButton getfb,getinsta,getpin,gettwit,proficPic,addphone,myImage;
    private Context ctx = this;
    String fb,twit,inst,pin,phone,user,email,tempphone;
    String changefid=" ",changetwitid=" ",changeinstaid=" ",changepinid=" ",chanegemail=" ",changename=" ",changephone1=" ",changephone2=" ",chanegalternateemail=" ";
    UserDetails details;
   // private String phone;
    private String imagePath;
    private TransferUtility transferUtility;
    private DBHelper dbHelper = new DBHelper(ctx);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        Bundle b = getIntent().getExtras();
        transferUtility = AppHelper.getTransferUtility(this);
        phone = b.getString("phone");
        Log.i("ContactDetails :: ",phone);
        details = getUserDetails(phone);

        getemail1 = (EditText) findViewById(R.id.edit_contact_mail1);
        getemail2 = (EditText) findViewById(R.id.edit_contact_mail2);
        getname = (EditText) findViewById(R.id.edit_contact_nameView);
        getnumber = (TextView) findViewById(R.id.edit_contact_phone1);

        getnumber1 = (EditText) findViewById(R.id.edit_contact_phone2);
        getnumber2 = (EditText) findViewById(R.id.edit_contact_phone3);

        getfb = (ImageButton) findViewById(R.id.edit_contact_facebookButton);
        getinsta = (ImageButton) findViewById(R.id.edit_contact_instagramButton);
        getpin = (ImageButton) findViewById(R.id.edit_contact_pButton);
        gettwit = (ImageButton) findViewById(R.id.edit_contact_twitterButton);

        addphone = (ImageButton) findViewById(R.id.edit_contact_phone_img);
       // proficPic = (ImageButton) findViewById(R.id.edit_contact_imageView);
        myImage = (ImageButton) findViewById(R.id.edit_contact_imageView);

        File imgFile = new  File("/data/user/0/com.uhcl.team5.socialmediateam5/files/"+phone+".jpg");

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
          //  ImageButton myImage = (ImageButton) findViewById(R.id.edit_contact_imageView);
            myImage.setImageBitmap(myBitmap);
        }

        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Cantact Image"),1);
            }
        });


        if(b!=null) {
            email = b.getString("email");
            getemail1.setText(Html.fromHtml(email));
        }

        getemail2.setText(details.getAlternateEmail());

        getnumber.setText(Html.fromHtml("<b>" + phone + "</b>"));


        getnumber1.setText(details.getPhonenumber1());
        getnumber2.setText(details.getPhonenumber2());
        getname.setText(Html.fromHtml(details.getUsername()));

        fb = b.getString("face");
       // if(fb!=null) {
            getfb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                    alertDialogBuilder.setTitle("Redirect");
                    alertDialogBuilder.setMessage("Enter your social media user id");

                    final EditText userInput = new EditText(ctx);
                    alertDialogBuilder.setView(userInput);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            changefid = userInput.getEditableText().toString();
                            String path = "https://facebook.com/" + fb;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            //i.setData(Uri.parse(path));
                            //startActivity(i);
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
     //   }
       // else{
          //  getfb.setVisibility(View.INVISIBLE);
        //}

        twit = b.getString("twit");
      //  if(twit!=null) {
            gettwit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                    alertDialogBuilder.setTitle("Redirect");
                    alertDialogBuilder.setMessage("Enter your social media user id");

                    final EditText userInput = new EditText(ctx);
                    alertDialogBuilder.setView(userInput);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            changetwitid = userInput.getEditableText().toString();
                            String path = "https://twitter.com/" + twit;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            //i.setData(Uri.parse(path));
                            //startActivity(i);
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
  //      }
     /*   else{
            gettwit.setVisibility(View.INVISIBLE);
        }
*/
        inst = b.getString("insta");
    //    if(inst!=null) {
            getinsta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                    alertDialogBuilder.setTitle("Redirect");
                    alertDialogBuilder.setMessage("Enter your social media user id");

                    final EditText userInput = new EditText(ctx);
                    alertDialogBuilder.setView(userInput);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            changeinstaid = userInput.getEditableText().toString();
                            String path = "https://instagram.com/" + inst;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            //i.setData(Uri.parse(path));
                            //startActivity(i);
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
      /*  }
        else{
            getinsta.setVisibility(View.INVISIBLE);
        }
*/
        pin = b.getString("pin");
  //      if(pin!=null) {
            getpin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                    alertDialogBuilder.setTitle("Redirect");
                    alertDialogBuilder.setMessage("Enter your social media user id");

                    final EditText userInput = new EditText(ctx);
                    alertDialogBuilder.setView(userInput);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            changepinid = userInput.getEditableText().toString();
                            String path = "https://pinterest.com/" + pin;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            //i.setData(Uri.parse(path));
                            //startActivity(i);
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
    /*    }
        else{
            getpin.setVisibility(View.INVISIBLE);
        }
    */    tempphone = phone;
        Button button = (Button) findViewById(R.id.edit_contact_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserDetailFromDynamoDb(tempphone);
                beginUpload(imagePath);
                beginDownload(phone);
                Intent intent = new Intent(EditContactDetails.this, DisplayUserDetails.class);
                intent.putExtra("phone", phone);
                intent.putExtra("name", details.getUsername());

                startActivity(intent);
            }

        });


    }

    public void onActivityResult(int reqCode, int resCode,Intent data) {
        if (resCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                imagePath = getPath(uri);
                //beginUpload(path);
            } catch (URISyntaxException e) {
                Toast.makeText(this,
                        "Unable to get the file from the given URI.  See error log for details",
                        Toast.LENGTH_LONG).show();
                //   Log.i("Unable to upload file from the given uri", e);
            }
            if (reqCode ==1 ) {
                myImage.setImageURI(data.getData());
            }

        }
    }

    private void beginUpload(String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            TransferObserver observer = transferUtility.upload("uhcl.socialmedia.app",phone+".jpg",
                    file);
        }
    }

    private void beginDownload(String key) {
        // Location to download files from S3 to. You can choose any accessible

        try {
            // file.
            // key = key.substring(2);
            File file = new File(getFilesDir().toString() + "/" + key+".jpg");
            Log.i("beginDownload ",key+":"+getFilesDir().toString()+"/"+key+"");            // Initiate the download
            TransferObserver observer = transferUtility.download("uhcl.socialmedia.app", key+".jpg", file);
        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */
            // observer.setTransferListener(new DownloadListener());
        }catch(Exception e){
            e.printStackTrace();
        }
    }



    /*
  * Gets the file path of the given Uri.
  */
    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[] {
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
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

    public void setUserDetailFromDynamoDb(String phone){

        try {

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
            UserDetails details = mapper.load(UserDetails.class,phone);
            if(getemail1!=null && getemail1.getEditableText()!=null){
                details.setEmail(getemail1.getEditableText().toString());
            }
            if(getemail2!=null && getemail2.getEditableText()!=null){
                details.setAlternateEmail(getemail2.getEditableText().toString());
            }
            if(getname!=null && getname.getEditableText()!=null){
                details.setUsername(getname.getEditableText().toString());
            }
            if(getnumber1!=null && getnumber1.getEditableText()!=null){
                details.setPhonenumber1(getnumber1.getEditableText().toString());
            }
            if(getnumber2!=null && getnumber2.getEditableText()!=null){
                details.setPhonenumber2(getnumber2.getEditableText().toString());
            }
            if(changefid!=null && changefid.trim()!=null){
                details.setFid(changefid);
            }
            if(changepinid!=null && changepinid.trim()!=null){
                details.setPinid(changepinid);
            }
            if(changetwitid!=null && changetwitid.trim()!=null){
                details.setTwitid(changetwitid);
            }
            if(changeinstaid!=null && changeinstaid.trim()!=null){
                details.setInstaid(changeinstaid);
            }
            mapper.save(details);

        }catch(Exception e) {
            e.printStackTrace();
            Log.i("Exception ","");
        }
    }


}
