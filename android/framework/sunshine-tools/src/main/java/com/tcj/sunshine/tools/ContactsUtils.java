package com.tcj.sunshine.tools;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lx on 2018/9/19.
 */

public class ContactsUtils {

    public static final int REQUEST_PERMISSION_CODE = 1897;
    public static final int REQUEST_CONTACKS = 1898;


    public static void contacts(Activity activity){
        //请求权限
        /**
         * <uses-permission android:name="android.permission.READ_CONTACTS" />
         <uses-permission android:name="android.permission.READ_PHONE_STATE" />
         */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsList = new ArrayList<>();
            if ((activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.READ_CONTACTS);
            if ((activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if (permissionsList.size() != 0) {
                ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_PERMISSION_CODE);
                return;
            }
        }

        Uri uri = Uri.parse("content://contacts/people");
        Intent intent = new Intent(Intent.ACTION_PICK, uri);
        activity.startActivityForResult(intent, REQUEST_CONTACKS);
    }


    public static Contacts getPhoneContacts(Context context, Uri uri) {
        Cursor cursor1 = null;
        Cursor cursor2 = null;
        Contacts contacts = null;

        try {
            ContentResolver cesolver = context.getContentResolver();
            //取得电话本中开始一项的光标
            cursor1 = cesolver.query(uri, null, null, null, null);

            if (cursor1 != null) {
                cursor1.moveToFirst();

                contacts = new Contacts();
                //取得联系人姓名
                int nameFieldColumnIndex = cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

                contacts.setName(cursor1.getString(nameFieldColumnIndex));
                //取得电话号码
                String contactId = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));

                String where = ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID + " = ?";

                cursor2 = cesolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, where, new String[]{contactId}, null);

                List<String> phoneList = new ArrayList<>();
                if (cursor2 != null) {
                    while (cursor2.moveToNext()) {
                        String number = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(!TextUtils.isEmpty(number)) {
                            StringBuilder sbuilder = new StringBuilder(number);
                            if(number.startsWith("86")) {
                                sbuilder.replace(0, 2, "");
                            }else if(number.startsWith("-86")){
                                sbuilder.replace(0, 3, "");
                            }else if(number.startsWith("+86")) {
                                sbuilder.replace(0, 3, "");
                            }

                            number = sbuilder.toString();
                            phoneList.add(number);
                        }
                    }
                }else {
                     ToastUtils.show("无法获取该联系人号码，请打开读取联系人信息权限", Toast.LENGTH_LONG);
                }

                contacts.setPhoneList(phoneList);
            }
        } catch (Exception e) {
            return contacts;
        } finally {

            if(cursor1 != null) {
                cursor1.close();
            }

            if(cursor2 != null) {
                cursor2.close();
            }

            return contacts;
        }
    }


    public static class Contacts {
        private String name;
        private List<String> phoneList;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getPhoneList() {
            return phoneList;
        }

        public void setPhoneList(List<String> phoneList) {
            this.phoneList = phoneList;
        }
    }
}
