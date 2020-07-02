package transferdata.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import transferdata.adapter.item;
import transferdata.encode.encryptAES;
import transferdata.home.R;
import transferdata.transfer.client;

import static android.content.Context.ACCOUNT_SERVICE;

public class getContact {
    public static Activity context;
    public static ArrayList<item> listItem;
    private Account[] liAccounts;
    public static encryptAES encrypt;
    private static int countContact = 0;

    public getContact(Activity contex) {
        this.context = contex;
        getAccount();
        listItem = new ArrayList<>();
        for (Account account : liAccounts) {
            if (account.type.equalsIgnoreCase("com.google")) {
                listItem.add(new item(true, R.drawable.ic_google, account.name, account.name, false));
            }
        }
        listItem.add(new item(true, R.drawable.ic_sim_card, "Sim card", "sim", false));
        listItem.add(new item(true, R.drawable.ic_phone_android, "Device", "device", false));
    }

    public void getAccount() {
        AccountManager manager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        liAccounts = manager.getAccounts();
    }

    public static String backupContacts() {
        countContact = 0;
        ContentResolver contentResolver = context.getContentResolver();
        String selectContact = null;
        ArrayList<String> type = new ArrayList<>();
        for (item it : listItem) {
            if (it.isChecked()) {
                type.add(it.getInfo());
            }
        }
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            try {
                // create file
                CSVWriter csvWriter = null;
                String vfile = "contacts.csv";
                File file = new File(context.getExternalFilesDir(null), "contacts");
                if (!file.exists()) {
                    file.mkdir();
                }
                File fileContacts = new File(file, vfile);
                if (!fileContacts.exists()) fileContacts.createNewFile();
                if (fileContacts.exists()) {
                    FileWriter fileWriter = new FileWriter(fileContacts);
                    csvWriter = new CSVWriter(fileWriter, ',',
                            CSVWriter.NO_QUOTE_CHARACTER,
                            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                            CSVWriter.DEFAULT_LINE_END);
                    while (cursor != null && cursor.moveToNext()) {
                        if (type.size() > 0) {
                            if (cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE) != -1) {

                                String typeContact = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE));
                                if (type.contains("device") && typeContact == null) {
                                    getInfoContact(cursor, contentResolver, csvWriter);
                                }
                                if (typeContact != null) {
                                    for (String s : type) {
                                        if (s.indexOf("@") == -1) {
                                            if (typeContact.indexOf(s) > -1) {
                                                getInfoContact(cursor, contentResolver, csvWriter);
                                            }
                                        } else {
                                            String accountName = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME));
                                            if (typeContact.indexOf("google") > -1) {
                                                if (accountName.equals(s)) {
                                                    getInfoContact(cursor, contentResolver, csvWriter);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else getInfoContact(cursor, contentResolver, csvWriter);
                        } else {
                            getInfoContact(cursor, contentResolver, csvWriter);
                        }
                    }
                    csvWriter.close();
                }

                // get size all contact
                float sizeKb = (float) fileContacts.length() / 1024;
                float sizeMb = (float) (sizeKb * 0.001);
                client.SIZE_ALL_ITEM[0] = (int) sizeMb;
                String result;
                if (sizeMb < 1) {
                    result = "Selected : " + countContact + " item " + " - " + String.format("%.02f", sizeKb) + " KB";
                } else {
                    result = "Selected : " + countContact + " item " + " - " + String.format("%.02f", sizeMb) + " MB";
                }

                // encrypy file backup
                encrypt = new encryptAES();
                encrypt.encrypt(fileContacts);

                return String.valueOf(result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return "Selected : 0 item - 0 MB";
    }

    public static void getInfoContact(Cursor cursor, ContentResolver contentResolver, CSVWriter csvWriter) {
        countContact++;
        String[] data;
        data = new String[25];
        for (int i = 0; i < 25; i++) {
            data[i] = "";
        }
        String id;
        //id
        data[0] = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        id = data[0];
        //phone
        int countPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        if (countPhone > 0) {
            Cursor csPhone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
            while (csPhone.moveToNext()) {
                String p = csPhone.getString(csPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String t = csPhone.getString(csPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                if (p != null) data[2] += p + ";";
                if (t != null) data[3] += t + ";";
            }
            csPhone.close();
        }
        //email
        Cursor csEmail = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
        while (csEmail.moveToNext()) {
            String e = csEmail.getString(csEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            String t = csEmail.getString(csEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
            if (e != null) data[4] += e + ";";
            if (t != null) data[5] += t + ";";
        }
        csEmail.close();
        // group
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String where = String.format(
                "%s = ? AND %s = ?",
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID);
        String[] whereParams = new String[]{
                ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE,
                id,
        };
        Cursor csGroup = contentResolver.query(
                uri,
                null,
                where,
                whereParams,
                null);
        if (csGroup.moveToFirst()) {
            data[6] = csGroup.getString(csGroup.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
            ));
        }
        csGroup.close();

        //company
        where = String.format(
                "%s = ? AND %s = ?",
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Organization.CONTACT_ID);
        whereParams = new String[]{
                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
                id,
        };
        Cursor csCompany = contentResolver.query(
                uri,
                null,
                where,
                whereParams,
                null
        );
        if (csCompany.moveToFirst()) {
            data[7] = csCompany.getString(csCompany.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
            data[8] = csCompany.getString(csCompany.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DEPARTMENT));
            data[9] = csCompany.getString(csCompany.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
        }
        csCompany.close();

        //address
        String street = "", city = "", country = "", typeAddress = "", region = "", postzip = "";
        Cursor csAddress = contentResolver.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ?",
                new String[]{id},
                null);
        while (csAddress.moveToNext()) {
            String _street = csAddress.getString(csAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
            String _city = csAddress.getString(csAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
            String _country = csAddress.getString(csAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
            String _typeAddress = csAddress.getString(csAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
            String _region = csAddress.getString(csAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
            String _postzip = csAddress.getString(csAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
            if (_street != null) data[10] += _street + ";";
            if (_city != null) data[11] += _city + ";";
            if (_country != null) data[12] += _country + ";";
            if (_typeAddress != null) data[13] += _typeAddress + ";";
            if (_region != null) data[14] += _region + ";";
            if (_postzip != null) data[15] += _postzip + ";";
        }
        csAddress.close();

        //nickname
        String familyName = "", givenName = "", middleName = "", prefix = "", suffix = "";
        where = String.format(
                "%s = ? AND %s = ?",
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID);
        whereParams = new String[]{
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                id,
        };
        Cursor csNickname = contentResolver.query(
                uri,
                null,
                where,
                whereParams,
                null
        );
        while (csNickname.moveToNext()) {
            String _familyName = csNickname.getString(csNickname.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
            String _givenName = csNickname.getString(csNickname.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
            String _middleName = csNickname.getString(csNickname.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));
            String _prefix = csNickname.getString(csNickname.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PREFIX));
            String _suffix = csNickname.getString(csNickname.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.SUFFIX));
            if (_familyName != null) data[16] = _familyName;
            if (_givenName != null) data[17] = _givenName;
            if (_middleName != null) data[18] = _middleName;
            if (_prefix != null) data[19] = _prefix;
            if (_suffix != null) data[20] = _suffix;
        }
        csNickname.close();

        //relation
        String relation = "", typeRelation = "";
        where = String.format(
                "%s = ? AND %s = ?",
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Relation.CONTACT_ID);
        whereParams = new String[]{
                ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE,
                id,
        };
        Cursor csRelation = contentResolver.query(
                uri,
                null,
                where,
                whereParams,
                null
        );
        while (csRelation.moveToNext()) {
            String _relation = csRelation.getString(csRelation.getColumnIndex(ContactsContract.CommonDataKinds.Relation.NAME));
            String _type = csRelation.getString(csRelation.getColumnIndex(ContactsContract.CommonDataKinds.Relation.TYPE));
            if (_relation != null) data[21] += _relation + ";";
            if (_type != null) data[22] += _type + ";";
        }
        csRelation.close();

        //note
        String note = "";
        where = String.format(
                "%s = ? AND %s = ?",
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Note.CONTACT_ID);
        whereParams = new String[]{
                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE,
                id,
        };
        Cursor csNote = contentResolver.query(
                uri,
                null,
                where,
                whereParams,
                null
        );
        if (csNote.moveToFirst()) {
            String _note = csNote.getString(csNote.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
            if (_note != null) data[23] = _note;
        }
        csNote.close();

        //website
        String website = "";
        where = String.format(
                "%s = ? AND %s = ?",
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Website.CONTACT_ID);
        whereParams = new String[]{
                ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE,
                id,
        };
        Cursor csWeb = contentResolver.query(
                uri,
                null,
                where,
                whereParams,
                null
        );
        while (csWeb.moveToNext()) {
            String _website = csWeb.getString(csWeb.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
            if (_website != null) data[24] += _website + ";";
        }
        csWeb.close();
        csvWriter.writeNext(data);
    }

    public void restoreContacts() {
        String path = context.getExternalFilesDir(null) + "/contacts/contacts.csv";
        encrypt = new encryptAES();
        File file = encrypt.decrypt(new File(path));
        if (file.exists()) {
            try {
                FileReader fileReader = new FileReader(file.getPath());
                CSVReader reader = new CSVReader(fileReader);
                String[] row;
                int id = 0;
                while ((row = reader.readNext()) != null) {
                    ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                    ops.add(ContentProviderOperation.newInsert(
                            ContactsContract.RawContacts.CONTENT_URI)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                            .build()
                    );
                    //name
                    String displayName = row[17] + " " + row[18] + " " + row[16];
                    if (!row[20].equals("")) {
                        displayName += ", " + row[20];
                    }
                    if (!row[19].equals("")) {
                        displayName = row[19] + " " + displayName;
                    }
                    ops.add(setNicknameContact(id, displayName, row[16], row[17], row[18], row[19], row[20]));
                    //email
                    String[] sEmail = row[4].split(";");
                    String[] tEmail = row[5].split(";");
                    if (!row[5].equals(""))
                        for (int i = 0; i < sEmail.length; i++) {
                            String[] value = new String[2];
                            value[0] = tEmail[i];
                            value[1] = sEmail[i];
                            ops.add(setEmailContact(id, value));
                        }

//                        //address
                    String[] sStreet = row[10].split(";");
                    String[] sCity = row[11].split(";");
                    String[] sCountry = row[12].split(";");
                    String[] tAddress = row[13].split(";");
                    String[] sRegion = row[14].split(";");
                    String[] sPostzip = row[15].split(";");
                    if (!row[13].equals(""))
                        for (int i = 0; i < tAddress.length; i++) {
                            String[] value = new String[6];
                            value[0] = tAddress[i];
                            if (sStreet.length > 0) value[1] = sStreet[i];
                            if (sCountry.length > 0) value[2] = sCountry[i];
                            if (sCountry.length > 0) value[3] = sCountry[i];
                            if (sRegion.length > 0) value[4] = sRegion[i];
                            if (sPostzip.length > 0) value[5] = sPostzip[i];
                            ops.add(setAddressContact(id, value));
                        }

//                        //group
                    if (!row[6].equals("")) ops.add(setGroupContact(id, row[6]));
//
//                        //phone
                    String[] sPhone = row[2].split(";");
                    String[] tPhone = row[3].split(";");
                    if (!row[3].equals(""))
                        for (int i = 0; i < sPhone.length; i++) {
                            String[] value = new String[2];
                            value[0] = tPhone[i];
                            value[1] = sPhone[i];
                            ops.add(setPhoneContact(id, value));
                        }
//
//                        //set company
                    if (!row[7].equals("") || !row[8].equals("") || !row[9].equals("")) {
                        ops.add(setCompanyContact(id, row[7], row[8], row[9]));
                    }

                    //relation
                    String[] sRelation = row[21].split(";");
                    String[] tRelation = row[22].split(";");
                    if (!row[22].equals(""))
                        for (int i = 0; i < tRelation.length; i++) {
                            String[] value = new String[2];
                            value[0] = tRelation[i];
                            value[1] = sRelation[i];
                            ops.add(setRelationContact(id, value));
                        }

                    //note
                    if (!row[23].equals("")) setNoteContact(id, row[23]);

                    //website
                    String[] sWebsite = row[24].split(";");
                    if (!row[24].equals(""))
                        for (int i = 0; i < sWebsite.length; i++) {
                            ops.add(setWebsiteContact(id, sWebsite[i]));
                        }

                    context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
        }
        file.delete();
    }

    public ContentProviderOperation setPhoneContact(int id, String[] value) {
        return ContentProviderOperation.
                newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, id)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, value[1])
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        value[0])
                .build();
    }

    public ContentProviderOperation setGroupContact(int id, String value) {
        return ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, id)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
                        value)
                .build();
    }

    public ContentProviderOperation setAddressContact(int id, String[] value) {
        return ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, id)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, value[0])
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, value[1])
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, value[2])
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, value[3])
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, value[4])
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, value[5])
                .build();
    }

    public ContentProviderOperation setEmailContact(int id, String[] value) {
        return ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, id)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, value[0])
                .withValue(
                        ContactsContract.CommonDataKinds.Email.DATA,
                        value[1])
                .build();
    }

    public ContentProviderOperation setNoteContact(int id, String value) {
        return ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, id)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.Note.NOTE, value)
                .build();
    }

    public ContentProviderOperation setWebsiteContact(int id, String value) {
        return ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, id)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.Website.URL,
                        value)
                .build();
    }

    public ContentProviderOperation setRelationContact(int id, String[] value) {
        return ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, id)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Relation.NAME, value[1])
                .withValue(
                        ContactsContract.CommonDataKinds.Relation.TYPE,
                        value[0])
                .build();
    }

    public ContentProviderOperation setNicknameContact(int id, String displayName, String
            familyName, String givenName, String middleName, String prefix, String suffix) {
        return ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, id)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName
                )
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, familyName
                )
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, givenName
                )
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, middleName
                )
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.PREFIX, prefix
                )
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.SUFFIX, suffix
                )
                .build();
    }

    public ContentProviderOperation setCompanyContact(int id, String company, String
            department, String title) {
        return ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, id)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.Organization.COMPANY,
                        company)
                .withValue(
                        ContactsContract.CommonDataKinds.Organization.DEPARTMENT,
                        department)
                .withValue(
                        ContactsContract.CommonDataKinds.Organization.TITLE,
                        title)
                .build();
    }

}
