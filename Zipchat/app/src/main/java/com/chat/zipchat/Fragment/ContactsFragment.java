package com.chat.zipchat.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.zipchat.Adapter.ContactListAdapter;
import com.chat.zipchat.Common.App;
import com.chat.zipchat.Common.BaseClass;
import com.chat.zipchat.Model.Contact.ContactItemRequest;
import com.chat.zipchat.Model.Contact.ContactRequest;
import com.chat.zipchat.Model.Contact.ContactResponse;
import com.chat.zipchat.Model.Contact.ResultItem;
import com.chat.zipchat.Model.Contact.ResultItemDao;
import com.chat.zipchat.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.Common.BaseClass.RequestPermissionCode;
import static com.chat.zipchat.Common.BaseClass.UserId;
import static com.chat.zipchat.Common.BaseClass.apiInterface;
import static com.chat.zipchat.Common.BaseClass.isOnline;
import static com.chat.zipchat.Common.BaseClass.myLog;
import static com.chat.zipchat.Common.BaseClass.myToast;
import static com.chat.zipchat.Common.BaseClass.snackbar;


@SuppressLint("ValidFragment")
public class ContactsFragment extends Fragment implements Filterable {

    Context mContext;
    ContactListAdapter contactAdapter;
    RecyclerView mRecyclerContact;
    RelativeLayout Rl_contact;
    ArrayList<ContactItemRequest> mListContact = new ArrayList<>();
    List<ResultItem> contactResponseList;

    EditText mSearchContact;
    TextView mTxtNoContact;

    public ContactsFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        mTxtNoContact = view.findViewById(R.id.mTxtNoContact);

        Rl_contact = view.findViewById(R.id.Rl_contact);
        mRecyclerContact = view.findViewById(R.id.mRecyclerContact);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerContact.setLayoutManager(mLayoutManager);
        mRecyclerContact.setItemAnimator(new DefaultItemAnimator());
        mRecyclerContact.setHasFixedSize(true);

        contactResponseList = App.getmInstance().resultItemDao.queryBuilder().where(ResultItemDao.Properties.IsFromContact.eq("1")).list();

        contactAdapter = new ContactListAdapter(mContext, contactResponseList);
        mRecyclerContact.setAdapter(contactAdapter);

        if (contactResponseList.size() > 0) {

            mRecyclerContact.setVisibility(View.VISIBLE);
            mTxtNoContact.setVisibility(View.GONE);

        } else {

            mRecyclerContact.setVisibility(View.GONE);
            mTxtNoContact.setVisibility(View.VISIBLE);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EnableRuntimePermission();
        } else {
            GetContactsIntoArrayList();
        }

        mSearchContact = view.findViewById(R.id.mSearchContact);

        mSearchContact.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (contactResponseList.size() > 0) {
                    contactAdapter.getFilter().filter(s.toString());
                }
            }
        });

        return view;
    }

    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
            GetContactsIntoArrayList();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
            GetContactsIntoArrayList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    GetContactsIntoArrayList();
                } else {
                    myToast(mContext, getResources().getString(R.string.permission_canceled));
                }
                break;
        }
    }

    public void GetContactsIntoArrayList() {

        myLog("GetContactsIntoArrayList", "");

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                //plus any other properties you wish to query
        };

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        } catch (SecurityException e) {
            //SecurityException can be thrown if we don't have the right permissions
        }


        if (cursor != null) {
            try {
                HashSet<String> normalizedNumbersAlreadyFound = new HashSet<>();
                int indexOfNormalizedNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                int indexOfDisplayName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexOfDisplayNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                mListContact.clear();
                while (cursor.moveToNext()) {

                    String normalizedNumber = cursor.getString(indexOfNormalizedNumber);

                    if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {

                        String displayName = cursor.getString(indexOfDisplayName);
                        String displayNumber = cursor.getString(indexOfDisplayNumber);
                        String newDisplayNumber = displayNumber.replaceAll("[^0-9]", "");

                        myLog("display Name & Number : ", displayName + "\t" + displayNumber);
                        myLog("newDisplayNumber: ", newDisplayNumber);

                        ContactItemRequest contactItem = new ContactItemRequest();
                        contactItem.setName(displayName);
                        contactItem.setMobileNumber(newDisplayNumber);
                        mListContact.add(contactItem);

                        //haven't seen this number yet: do something with this contact!
                    } else {
                        //don't do anything with this contact because we've already found this number
                    }
                }
                SyncContact();

            } finally {
                cursor.close();
            }
        }
    }

    private void SyncContact() {

        if (isOnline(mContext)) {

            ContactRequest contactRequest = new ContactRequest();
            contactRequest.setContact(mListContact);
            contactRequest.setDeviceType("ANDROID");

            Call<ContactResponse> contactResponseCall = apiInterface.contactDetails(contactRequest);
            contactResponseCall.enqueue(new Callback<ContactResponse>() {
                @Override
                public void onResponse(Call<ContactResponse> call, final Response<ContactResponse> response) {

                    if (response.isSuccessful()) {

                        contactResponseList.clear();
                        App.getmInstance().contactResponseDao.deleteAll();
                        App.getmInstance().resultItemDao.deleteAll();

                        if (response.body() != null) {
                            ContactResponse contactResponse = response.body();
                            contactResponse.contact_id = 1L;
                            App.getmInstance().contactResponseDao.insertOrReplace(contactResponse);

                            if (response.body().getResult().size() > 0) {

                                for (ResultItem con : contactResponse.getResult()) {
                                    if (!con.getId().equalsIgnoreCase(UserId(mContext))) {
                                        con.setContact_id(contactResponse.contact_id);
                                        con.setIsFromContact("1");
                                        App.getmInstance().resultItemDao.insertOrReplace(con);
                                    }

                                }

                            }

                            contactResponseList = App.getmInstance().resultItemDao.queryBuilder().list();

                            if (contactResponseList.size() > 0) {

                                mRecyclerContact.setVisibility(View.VISIBLE);
                                mTxtNoContact.setVisibility(View.GONE);

                                contactAdapter.updateContacttList(contactResponseList);

                            } else {

                                mRecyclerContact.setVisibility(View.GONE);
                                mTxtNoContact.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ContactResponse> call, Throwable t) {
                    myLog("onFailure: ", t.toString());
                }
            });

        } else {
            snackbar(mContext, mRecyclerContact, BaseClass.NO_INTERNET);
        }

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
               /* FilterResults results = new FilterResults();
                ArrayList<String> lists = new ArrayList<>();
                if (!TextUtils.isEmpty(charSequence)) {

                    for (String temp : searchTipsArray) {

                        if (temp.toLowerCase().contains(charSequence)) {
                            lists.add(temp);
                        }
                    }
                    newList = lists;
                } else {
                    // newList = searchTipsArray;
                }
                results.values = newList;
                results.count = newList.size();*/

                return null;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults != null) {
                }
            }
        };
    }

}
