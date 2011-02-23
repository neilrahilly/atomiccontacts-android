/* Copyright 2011 Atomic Contacts */

package com.atomiccontacts.android.authenticator;

import com.atomiccontacts.android.Constants;
import com.atomiccontacts.android.R;
import com.atomiccontacts.android.client.NetworkUtilities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Activity which displays login screen to the user.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    private static final String TAG = "AuthenticatorActivity";

    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";

    public static final String PARAM_PASSWORD = "password";

    public static final String PARAM_USERNAME = "username";

    public static final String PARAM_AUTH_TOKEN_TYPE = "authTokenType";

    private EditText mUsernameEdit;

    private EditText mPasswordEdit;

    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle icicle) {
        Log.i(TAG, "loading data from Intent");
        super.onCreate(icicle);
        setContentView(R.layout.login_activity);
        mAccountManager = AccountManager.get(this);
        mUsernameEdit = (EditText) findViewById(R.id.username_edit);
        mPasswordEdit = (EditText) findViewById(R.id.password_edit);
    }

    /**
     * Handles onClick event on the submit button. Sends username/password to
     * the server for authentication.
     * 
     * @param view The submit button for which this method is invoked
     */
    public void handleLogin(View view) {
        Log.i(TAG, "handleLogin() called");
        String username = mUsernameEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();
        if (NetworkUtilities.authenticate(username, password)) {
            Account account = new Account(username, Constants.ACCOUNT_TYPE);
            if (mAccountManager.addAccountExplicitly(account, password, null)) {
                Log.i(TAG, "Account created: " + username);
                ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true);
                Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
                result.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                setAccountAuthenticatorResult(result);
                finish();
            } else {
                // Fails on attempt to create duplicate account
                Log.i(TAG, "Account creation failed: " + username);
            }
        }
    }

}
