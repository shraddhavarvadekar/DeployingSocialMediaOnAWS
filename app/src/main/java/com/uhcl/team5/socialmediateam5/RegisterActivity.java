package com.uhcl.team5.socialmediateam5;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.net.Uri;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.net.URISyntaxException;

public class RegisterActivity extends AppCompatActivity {

    EditText registerName, registerEmail, registerPassword;
    Button registernext;
    ImageView registerImageView;
    private TransferUtility transferUtility;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerName = (EditText) findViewById(R.id.registercontactname);
        registerEmail = (EditText) findViewById(R.id.registeremail);
        registerPassword = (EditText) findViewById(R.id.registerpassword);
        registernext = (Button) findViewById(R.id.registerNext);
        registerImageView = (ImageView) findViewById(R.id.registerImage);
      //  transferUtility = AppHelper.getTransferUtility(this);

        registernext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {



               if(registerName.getText().length() > 0 && registerEmail.getText().length() > 0 &&
                       registerPassword.getText().length() > 0) {
                   String fullName = registerName.getText().toString();
                   String[] checkName = fullName.split(" ");
                   if(checkName.length<2){
                       Toast.makeText(RegisterActivity.this, "Enter first name and last name with space",Toast.LENGTH_LONG).show();
                   }  else {


                       String emaildId = registerEmail.getText().toString();
                       if (isValidEmail(emaildId)) {
                           String name = registerName.getText().toString();
                           String password = registerPassword.getText().toString();
                           Intent intent = new Intent(RegisterActivity.this, ToolsActivity.class);
                           intent.putExtra("email", emaildId);
                           intent.putExtra("username", name);
                           intent.putExtra("password", password);
                           intent.putExtra("imagePath", imagePath);
                           startActivity(intent);

                       } else {
                           registerPassword.setText("");
                           Toast.makeText(RegisterActivity.this, "wrong email format", Toast.LENGTH_LONG).show();
                       }
                   }
               }else {
                   registerPassword.setText("");
                   Toast.makeText(RegisterActivity.this, "please enter all required values",Toast.LENGTH_LONG).show();
               }
            }
        });


        registerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Cantact Image"),1);
            }
        });

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
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
                registerImageView.setImageURI(data.getData());
            }

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

    /*
  * Begins to upload the file specified by the file path.
  */
    private void beginUpload(String filePath) {
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);
        TransferObserver observer = transferUtility.upload("uhcl.socialmedia.app", file.getName(),
                file);
        /*/file.getName()
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */
        // observer.setTransferListener(new UploadListener());
    }

    /*
 * A TransferListener class that can listen to a upload task and be notified
 * when the status changes.
 */
    private class UploadListener implements TransferListener {

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
           // Log.e(TAG, "Error during upload: " + id, e);
            Log.i("Error :: "," error");
          //  updateList();
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.i("onProgressStateChanged: "," changed");
          //  updateList();
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.i("onStateChanged: "," changed");
            //updateList();
        }
    }

}
