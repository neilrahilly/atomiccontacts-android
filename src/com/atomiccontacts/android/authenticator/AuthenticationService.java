/* Copyright 2011 Atomic Contacts */

package com.atomiccontacts.android.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Service to handle Account authentication. It instantiates the authenticator
 * and returns its IBinder.
 */
public class AuthenticationService extends Service {

    private static final String TAG = "AuthenticationService";

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        Log.i(TAG, "Atomic Contacts authentication service started.");
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind()...  returning the AccountAuthenticator binder for intent " + intent);
        return mAuthenticator.getIBinder();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Atomic Contacts authentication service stopped.");
    }
}
