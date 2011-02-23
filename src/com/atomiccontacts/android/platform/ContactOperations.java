/* Copyright 2011 Atomic Contacts */

package com.atomiccontacts.android.platform;

import com.atomiccontacts.android.Constants;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.text.TextUtils;
import android.util.Log;

/**
 * Helper class for storing data in the platform content providers.
 */
public class ContactOperations {

    private static final String TAG = "ContactOperations";

    private ContentValues mValues;

    private ContentProviderOperation.Builder mBuilder;

    private BatchOperation mBatchOperation;

    private Context mContext;

    private boolean mYield;

    private long mRawContactId;

    private int mBackReference;

    private boolean mIsNewContact;

    /**
     * Returns an instance of ContactOperations instance for adding new a
     * contact to the platform contacts provider.
     */
    public static ContactOperations createNewContact(Context context, String atomicContactId,
            String accountName, BatchOperation batchOperation) {
        return new ContactOperations(context, atomicContactId, accountName, batchOperation);
    }

    /**
     * Returns an instance of ContactOperations for updating existing contact in
     * the platform contacts provider.
     */
    public static ContactOperations updateExistingContact(Context context, long rawContactId,
            BatchOperation batchOperation) {
        return new ContactOperations(context, rawContactId, batchOperation);
    }

    public ContactOperations(Context context, BatchOperation batchOperation) {
        mValues = new ContentValues();
        mYield = true;
        mContext = context;
        mBatchOperation = batchOperation;
    }

    public ContactOperations(Context context, String userId, String accountName,
            BatchOperation batchOperation) {
        this(context, batchOperation);
        mBackReference = mBatchOperation.size();
        mIsNewContact = true;
        mValues.put(RawContacts.SOURCE_ID, userId);
        mValues.put(RawContacts.ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
        mValues.put(RawContacts.ACCOUNT_NAME, accountName);
        mBuilder = newInsertCpo(RawContacts.CONTENT_URI, true).withValues(mValues);
        mBatchOperation.add(mBuilder.build());
    }

    public ContactOperations(Context context, long rawContactId, BatchOperation batchOperation) {
        this(context, batchOperation);
        mIsNewContact = false;
        mRawContactId = rawContactId;
    }

    /** Adds a contact name. */
    public ContactOperations addName(String firstName, String lastName) {
        String msg = "add name (firstName=" + firstName + ", lastName=" + lastName + "): ";
        mValues.clear();
        if (!TextUtils.isEmpty(firstName)) {
            mValues.put(StructuredName.GIVEN_NAME, firstName);
            mValues.put(StructuredName.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        }
        if (!TextUtils.isEmpty(lastName)) {
            mValues.put(StructuredName.FAMILY_NAME, lastName);
            mValues.put(StructuredName.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        }
        if (mValues.size() > 0) {
            addInsertOp();
            Log.v(TAG, msg + "done");
        } else {
            Log.v(TAG, msg + "ignored");
        }
        return this;
    }

    /** Adds a contact organization. */
    public ContactOperations addOrganization(String organization, String title) {
        String msg = "add organization (organization=" + organization + ", title=" + title + "): ";
        mValues.clear();
        if (!TextUtils.isEmpty(organization)) {
            mValues.put(Organization.COMPANY, organization);
            mValues.put(Organization.MIMETYPE, Organization.CONTENT_ITEM_TYPE);
        }
        if (!TextUtils.isEmpty(title)) {
            mValues.put(Organization.TITLE, title);
            mValues.put(Organization.MIMETYPE, Organization.CONTENT_ITEM_TYPE);
        }
        if (mValues.size() > 0) {
            addInsertOp();
            Log.v(TAG, msg + "done");
        } else {
            Log.v(TAG, msg + "ignored");
        }
        return this;
    }

    /** Adds a contact email. */
    public ContactOperations addEmail(String email, int type) {
        String msg = "add email (email=" + email + ", type=" + type + "): ";
        mValues.clear();
        if (!TextUtils.isEmpty(email)) {
            mValues.put(Email.DATA, email);
            mValues.put(Email.TYPE, type);
            mValues.put(Email.MIMETYPE, Email.CONTENT_ITEM_TYPE);
            addInsertOp();
            Log.v(TAG, msg + "done");
        } else {
            Log.v(TAG, msg + "ignored");
        }
        return this;
    }

    /** Adds a contact phone number. */
    public ContactOperations addPhone(String phone, int type) {
        String msg = "add phone (phone=" + phone + ", type=" + type + "): ";
        mValues.clear();
        if (!TextUtils.isEmpty(phone)) {
            mValues.put(Phone.NUMBER, phone);
            mValues.put(Phone.TYPE, type);
            mValues.put(Phone.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
            addInsertOp();
            Log.v(TAG, msg + "done");
        } else {
            Log.v(TAG, msg + "ignored");
        }
        return this;
    }

    /** Adds a contact postal address. */
    public ContactOperations addAddress(String street, String street2, String locality,
            String region, String country, String postalCode, int type) {
        String msg = "add address (street=" + street + ", street2=" + street2 + ", locality="
                + locality + ", region=" + region + ", country=" + country + ", postalCode="
                + postalCode + ", type=" + type + "): ";
        mValues.clear();
        String combinedStreet = combineStreetFields(street, street2);
        if (!TextUtils.isEmpty(combinedStreet)) {
            mValues.put(StructuredPostal.STREET, combinedStreet);
        }
        if (!TextUtils.isEmpty(locality)) {
            mValues.put(StructuredPostal.CITY, locality);
        }
        if (!TextUtils.isEmpty(region)) {
            mValues.put(StructuredPostal.REGION, region);
        }
        if (!TextUtils.isEmpty(country)) {
            mValues.put(StructuredPostal.COUNTRY, country);
        }
        if (!TextUtils.isEmpty(postalCode)) {
            mValues.put(StructuredPostal.POSTCODE, postalCode);
        }
        if (mValues.size() > 0) {
            mValues.put(StructuredPostal.TYPE, type);
            mValues.put(StructuredPostal.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
            addInsertOp();
            Log.v(TAG, msg + "done");
        } else {
            Log.v(TAG, msg + "ignored");
        }
        return this;
    }

    /** Updates contact data structured name row. */
    public ContactOperations updateName(Uri uri, String existingFirstName, String existingLastName,
            String firstName, String lastName) {
        String msg = "update name (uri=" + uri + ", existingFirstName=" + existingFirstName
                + ", existingLastName=" + existingLastName + ", firstName=" + firstName
                + ", lastName=" + lastName + "): ";
        mValues.clear();
        if (!TextUtils.equals(existingFirstName, firstName)) {
            mValues.put(StructuredName.GIVEN_NAME, firstName);
        }
        if (!TextUtils.equals(existingLastName, lastName)) {
            mValues.put(StructuredName.FAMILY_NAME, lastName);
        }
        if (mValues.size() > 0) {
            addUpdateOp(uri);
            Log.v(TAG, msg + "done");
        } else {
            Log.v(TAG, msg + "ignored");
        }
        return this;
    }

    /** Updates contact data organization row. */
    public ContactOperations updateOrganization(Uri uri, String existingOrganization,
            String existingTitle, String organization, String title) {
        String msg = "update organization (uri=" + uri + ", existingOrganization="
                + existingOrganization + ", existingTitle=" + existingTitle + ", organizatio="
                + organization + ", title=" + title + "): ";
        mValues.clear();
        if (!TextUtils.equals(existingOrganization, organization)) {
            mValues.put(Organization.COMPANY, organization);
        }
        if (!TextUtils.equals(existingTitle, title)) {
            mValues.put(Organization.TITLE, title);
        }
        if (mValues.size() > 0) {
            addUpdateOp(uri);
            Log.v(TAG, msg + "done");
        } else {
            Log.v(TAG, msg + "ignored");
        }
        return this;
    }

    /** Updates contact data email row. */
    public ContactOperations updateEmail(Uri uri, String existingEmail, String email) {
        String msg = "update email (uri=" + uri + ", existingEmail=" + existingEmail + ", email="
                + email + "): ";
        if (!TextUtils.equals(existingEmail, email)) {
            mValues.clear();
            mValues.put(Email.DATA, email);
            addUpdateOp(uri);
            Log.v(TAG, msg + "done");
        } else {
            Log.v(TAG, msg + "ignored");
        }
        return this;
    }

    /** Updates contact data phone row. */
    public ContactOperations updatePhone(Uri uri, String existingPhone, String phone) {
        String msg = "update phone (uri=" + uri + ", existingPhone=" + existingPhone + ", phone="
                + phone + "): ";
        if (!TextUtils.equals(phone, existingPhone)) {
            mValues.clear();
            mValues.put(Phone.NUMBER, phone);
            addUpdateOp(uri);
            Log.v(TAG, msg + "done");
        } else {
            Log.v(TAG, msg + "ignored");
        }
        return this;
    }

    /** Updates contact data postal address row. */
    public ContactOperations updateAddress(Uri uri, String existingStreet, String existingLocality,
            String existingRegion, String existingCountry, String existingPostalCode,
            String street, String street2, String locality, String region, String country,
            String postalCode) {
        String msg = "update address (uri=" + uri + ", existingStreet=" + existingStreet
                + ", existingLocality=" + existingLocality + ", existingRegion=" + existingRegion
                + ", existingCountry=" + existingCountry + ", existingPostalCode="
                + existingPostalCode + ", street=" + street + ", street2=" + street2
                + ", locality=" + locality + ", region=" + region + ", country=" + country
                + ", postalCode" + postalCode + "): ";
        mValues.clear();
        String combinedStreet = combineStreetFields(street, street2);
        if (!TextUtils.equals(existingStreet, combinedStreet)) {
            mValues.put(StructuredPostal.STREET, combinedStreet);
        }
        if (!TextUtils.equals(existingLocality, locality)) {
            mValues.put(StructuredPostal.CITY, locality);
        }
        if (!TextUtils.equals(existingRegion, region)) {
            mValues.put(StructuredPostal.REGION, region);
        }
        if (!TextUtils.equals(existingCountry, country)) {
            mValues.put(StructuredPostal.COUNTRY, country);
        }
        if (!TextUtils.equals(existingPostalCode, postalCode)) {
            mValues.put(StructuredPostal.POSTCODE, postalCode);
        }
        if (mValues.size() > 0) {
            addUpdateOp(uri);
            Log.v(TAG, msg + "done");
        } else {
            Log.v(TAG, msg + "ignored");
        }
        return this;
    }

    /** Adds an insert operation into the batch. */
    private void addInsertOp() {
        if (!mIsNewContact) {
            mValues.put(Data.RAW_CONTACT_ID, mRawContactId);
        }
        mBuilder = newInsertCpo(addCallerIsSyncAdapterParameter(Data.CONTENT_URI), mYield);
        mBuilder.withValues(mValues);
        if (mIsNewContact) {
            mBuilder.withValueBackReference(Data.RAW_CONTACT_ID, mBackReference);
        }
        mYield = false;
        mBatchOperation.add(mBuilder.build());
    }

    /** Adds an update operation into the batch. */
    private void addUpdateOp(Uri uri) {
        mBuilder = newUpdateCpo(uri, mYield).withValues(mValues);
        mYield = false;
        mBatchOperation.add(mBuilder.build());
    }

    public static ContentProviderOperation.Builder newInsertCpo(Uri uri, boolean yield) {
        return ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(uri))
                .withYieldAllowed(yield);
    }

    public static ContentProviderOperation.Builder newUpdateCpo(Uri uri, boolean yield) {
        return ContentProviderOperation.newUpdate(addCallerIsSyncAdapterParameter(uri))
                .withYieldAllowed(yield);
    }

    public static ContentProviderOperation.Builder newDeleteCpo(Uri uri, boolean yield) {
        return ContentProviderOperation.newDelete(addCallerIsSyncAdapterParameter(uri))
                .withYieldAllowed(yield);

    }

    private static Uri addCallerIsSyncAdapterParameter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true")
                .build();
    }

    private static String combineStreetFields(String street, String street2) {
        String s = "";
        if (!TextUtils.isEmpty(street)) {
            s += street;
            if (!TextUtils.isEmpty(street2)) {
                s += "\n" + street2;
            }
        } else {
            if (!TextUtils.isEmpty(street2)) {
                s += street2;
            }
        }
        return s;
    }

}
