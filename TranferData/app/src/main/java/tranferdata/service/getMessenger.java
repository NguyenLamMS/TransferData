package tranferdata.service;

import android.app.Activity;
import android.app.role.RoleManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.Telephony;
import android.util.Log;
import androidx.annotation.RequiresApi;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import tranferdata.adapter.item;
import tranferdata.encode.encryptRSA;
import tranferdata.socket.clientSocket;
import tranferdata.tranfer.client;

public class getMessenger{
    private static final int CODE_RESTORE_SMS = 121;
    public static Activity context;
    public static ArrayList<item>listItem;
    public getMessenger(Activity contex){
        this.context = contex;
        listItem = new ArrayList<>();
        listItem.add(new item(true,-1, "One day ago", "1", false));
        listItem.add(new item(true,-1, "One week ago", "2", false));
        listItem.add(new item(true,-1, "One month ago", "3", false));
        listItem.add(new item(true,-1, "One year ago", "4", false));
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String backupMessages() {
        String selection = null;
        ArrayList<String>type = new ArrayList<>();
        for(item it : listItem){
            if(it.isChecked()){
                type.add(it.getInfo());
            }
        }
        if(type.size() > 0) {
            Calendar calendar = Calendar.getInstance();
            Date date = new Date();
            long maxDate = date.getTime();
            long minDate = maxDate;
            for (String s : type) {
                int integer = Integer.parseInt(s);
                if(integer == 1) {
                    calendar.add(Calendar.HOUR_OF_DAY, -24);
                    minDate = calendar.getTimeInMillis();
                }
                if(integer == 2) {
                    calendar.add(Calendar.DAY_OF_WEEK, -7);
                    minDate = calendar.getTimeInMillis();
                }
                if(integer == 3) {
                    calendar.add(Calendar.DAY_OF_MONTH, -31);
                    minDate = calendar.getTimeInMillis();

                }
                if(integer == 4) {
                    calendar.add(Calendar.DAY_OF_YEAR, -365);
                    minDate = calendar.getTimeInMillis();
                }
            }
            selection = Telephony.Sms.DATE + " >= " + minDate +" AND " + Telephony.Sms.DATE + " <= " + maxDate;
        }
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(Telephony.Sms.CONTENT_URI,
                new String[]{Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.READ, Telephony.Sms.TYPE, Telephony.Sms.DATE, Telephony.Sms.DATE_SENT}
                , selection,
                null,
                null);
        if (cursor.getCount() > 0) {
            // create file
            try {
                String vfile = "messages.xml";
                File file = new File(context.getExternalFilesDir(null), "messages");
                if (!file.exists()) {
                    file.mkdir();
                }
                File fileMessages = new File(file, vfile);
                if (!fileMessages.exists()) fileMessages.createNewFile();
                if (fileMessages.exists()) {
                    Document dom;
                    Element e = null;
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    dom = db.newDocument();
                    Element rootEle = dom.createElement("messages");
                    while (cursor.moveToNext()) {
                        Element messEle = dom.createElement("message");
                        rootEle.appendChild(messEle);
                        for (int i = 0; i < cursor.getColumnCount(); i++) {
                            if (cursor.getString(i) != null) {
                                Log.d("mess: ",cursor.getColumnName(i) + ": " + cursor.getString(i));
                                e = dom.createElement(cursor.getColumnName(i));
                                e.appendChild(dom.createTextNode(cursor.getString(i)));
                                messEle.appendChild(e);
                            }
                        }
                    }
                    dom.appendChild(rootEle);
                    Transformer tr = TransformerFactory.newInstance().newTransformer();
                    tr.setOutputProperty(OutputKeys.INDENT, "yes");
                    tr.setOutputProperty(OutputKeys.METHOD, "xml");
                    tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                    tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "messages.dtd");
                    tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                    tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(fileMessages)));

                    // get size all messenger
                    float size = (float) fileMessages.length()/1024;
                    size = (float) (size * 0.001);
                    client.SIZE_ALL_ITEM[2] = (int) size;
                    String result = "Selected : " + getLengMessenger() + " item "+ " - " + String.format("%.03f", size) + " MB";
                    return String.valueOf(result);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
        return "Selected : 0 item - 0 MB";
    }


    public static int getLengMessenger(){
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(Telephony.Sms.Conversations.CONTENT_URI, null,null, null, null);
        return cursor.getCount();
    }
    public void restoreMessages() {
        if (context.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(context))) {
            executeRestore();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                RoleManager roleManager = context.getSystemService(RoleManager.class);
                // check if the app is having permission to be as default SMS app
                boolean isRoleAvailable = roleManager.isRoleAvailable(RoleManager.ROLE_SMS);
                if (isRoleAvailable) {
                    // check whether your app is already holding the default SMS app role.
                    boolean isRoleHeld = roleManager.isRoleHeld(RoleManager.ROLE_SMS);
                    if (!isRoleHeld) {
                        Intent roleRequestIntent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS);
                        context.startActivityForResult(roleRequestIntent, CODE_RESTORE_SMS);
                    }
                }
            } else {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
                context.startActivityForResult(intent, CODE_RESTORE_SMS);
            }
    }
    }
    public void executeRestore(){
        String path = context.getExternalFilesDir(null) + "/messages/messages.xml";
        File file = new File(path);
        Log.d("messenger", "executeRestore: " + path);
        if (file.exists()) {
            Log.d("messenger", "restoreMessages: ");
            try {
                Document dom;
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = null;
                db = dbf.newDocumentBuilder();
                dom = db.parse(file);
                NodeList nl = dom.getElementsByTagName("message");
                for (int i = 0; i < nl.getLength(); i++) {
                    ContentValues contentValues = new ContentValues();
                    NodeList childNode = nl.item(i).getChildNodes();
                    for (int j = 0; j < childNode.getLength(); j++) {
                        Node node = childNode.item(j);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            contentValues.put(node.getNodeName(), node.getTextContent());
                            Log.d("test:", node.getNodeName() + ": " + node.getTextContent());
                        }

                    }
                    context.getContentResolver().insert(Telephony.Sms.CONTENT_URI, contentValues);
                }

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ;
        }else{
            Log.d("messenger", "restoreMessages: not" );
        }
    }
}
