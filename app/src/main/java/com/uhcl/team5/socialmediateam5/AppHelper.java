package com.uhcl.team5.socialmediateam5;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Ashok on 9/24/2016.
 */
public class AppHelper {

    private static AppHelper appHelper;
    private static String user;
    private static CognitoUserPool userPool;

    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;

    private static final String userPoolId = "us-west-2_pIbJesMDx";
    private static final String clientId = "7c1s6iqotm4q1vebmd42pm75fk";
    private static final String clientSecret = "807us18ln1v7oemepcjcaf5bn1s0ikj4o1oeakff4mkbkdakh79";
    private static final Regions cognitoRegion = Regions.DEFAULT_REGION;
    public static void init(Context context) {

        if (appHelper != null && userPool != null) {
            return;
        }

        if (appHelper == null) {
            appHelper = new AppHelper();
        }

        if (userPool == null) {

            // Create a user pool with default ClientConfiguration
            userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegion);
        }

    }

    public static void setUser(String newUser) {
        user = newUser;
    }
    public static CognitoUserPool getPool() {
        return userPool;
    }

    public static String formatException(Exception exception) {
        String formattedString = "Internal Error";
        Log.e("App Error",exception.toString());
        Log.getStackTraceString(exception);

        String temp = exception.getMessage();

        if(temp != null && temp.length() > 0) {
            formattedString = temp.split("\\(")[0];
            if(temp != null && temp.length() > 0) {
                return formattedString;
            }
        }

        return  formattedString;
    }


    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     *
     * @param context
     * @return a TransferUtility instance
     */
    public static TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getS3Client(context.getApplicationContext()),
                    context.getApplicationContext());
        }
      return sTransferUtility;
    }

    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
    public static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
        }
        return sS3Client;
    }

    /**
     * Gets an instance of CognitoCachingCredentialsProvider which is
     * constructed using the given Context.
     *
     * @param context An Context instance.
     * @return A default credential provider.
     */
    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        if (sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    "us-west-2:22a12db0-c9fa-4dfb-b535-41257f688aaf",
                    Regions.US_WEST_2);
        }
        return sCredProvider;
    }
}
