/* Copyright 2011 Atomic Contacts */

package com.atomiccontacts.android.client;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.util.Base64;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NetworkUtilities {

    private static final String TAG = "NetworkUtilities";

    private static final String URI_BASE = "http://10.0.2.2/1/";

    private static final String URI_YOU = URI_BASE + "you";

    private static final String URI_CONTACTS = URI_BASE + "contacts";

    private static final DefaultHttpClient mHttpClient = new DefaultHttpClient();

    public static String encodeHttpBasicAuthCredentials(String username, String password) {
        return Base64.encodeToString((username + ":" + password).getBytes(), 0);
    }

    private NetworkUtilities() {
        // Static class
    }

    public static boolean authenticate(String username, String password) {
        HttpGet request = new HttpGet(URI_YOU);
        String authToken = encodeHttpBasicAuthCredentials(username, password);
        request.addHeader("Authorization", "Basic " + authToken);
        try {
            HttpResponse response = mHttpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Log.i(TAG, "Authentication success: " + username + ":" + password);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error authenticating with server: " + e);
        }
        Log.i(TAG, "Authentication failed: " + username + ":" + password);
        return false;
    }

    public static List<User> fetchContacts(String authToken, Date lastUpdated) {
        String url = URI_CONTACTS;
        if (lastUpdated != null) {
            url += Long.toString(lastUpdated.getTime() / 1000);
        }
        HttpGet request = new HttpGet(url);
        request.addHeader("Authorization", "Basic " + authToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = mHttpClient.execute(request, responseHandler);
            JSONArray array = new JSONArray(responseBody);
            List<User> users = new ArrayList<User>();
            for (int i = 0; i < array.length(); i++) {
                User user = User.valueOf(array.getJSONObject(i));
                Log.d(TAG, "Fetched contact: " + user);
                users.add(user);
            }
            return users;
        } catch (Exception e) {
            Log.e(TAG, "Error fetching contacts: " + e);
        }
        return null;
    }

}
