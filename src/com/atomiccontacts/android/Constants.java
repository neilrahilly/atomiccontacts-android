/* Copyright 2010 Atomic Contacts */

package com.atomiccontacts.android;

public class Constants {

    /**
     * Account type string. IMPORTANT: This must be the same as the
     * android:accountType attribute on the account-authenticator element in
     * authenicator.xml. Otherwise, AccountManagerService will throw an error:
     * 'caller uid XXXX is different than the authenticator's uid'. See
     * http://loganandandy.tumblr.com/post/613041897/caller-uid-is-different.
     */
    public static final String ACCOUNT_TYPE = "com.atomiccontacts.android.account";

    /**
     * Auth token type string.
     */
    public static final String AUTH_TOKEN_TYPE = "com.atomiccontacts.android.token";

    /**
     * Profile type string. IMPORTANT: Should be the same as specified in contacts.xml. 
     */
    public static final String PROFILE_TYPE = "vnd.android.cursor.item/vnd.com.atomiccontacts.android.profile";

}
