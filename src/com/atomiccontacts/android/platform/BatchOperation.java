/* Copyright 2011 Atomic Contacts */

package com.atomiccontacts.android.platform;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

/**
 * Handles execution of batch operations on contacts provider.
 */
public class BatchOperation {
    private final String TAG = "BatchOperation";

    private final ContentResolver mResolver;

    // List for storing the batch mOperations
    ArrayList<ContentProviderOperation> mOperations;

    public BatchOperation(Context context, ContentResolver resolver) {
        mResolver = resolver;
        mOperations = new ArrayList<ContentProviderOperation>();
    }

    public int size() {
        return mOperations.size();
    }

    public void add(ContentProviderOperation cpo) {
        mOperations.add(cpo);
    }

    public void execute() {
        if (mOperations.size() == 0) {
            return;
        }
        // Apply the mOperations to the content provider
        try {
            mResolver.applyBatch(ContactsContract.AUTHORITY, mOperations);
        } catch (OperationApplicationException e) {
            Log.e(TAG, "Storing contact data failed:", e);
        } catch (RemoteException e) {
            Log.e(TAG, "Storing contact data failed:", e);
        }
        mOperations.clear();
    }

}
