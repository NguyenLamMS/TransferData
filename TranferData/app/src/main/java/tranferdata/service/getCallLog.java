package tranferdata.service;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

import tranferdata.tranfer.client;

public class getCallLog {
    Activity context;
    private String[] smsHeader;
    public getCallLog(Activity context){
        this.context = context;
    }
    public String backupCallogs() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return "Selected : 0 item - 0 MB";
        }
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.DURATION, CallLog.Calls.DATE, CallLog.Calls.TYPE, CallLog.Calls.NUMBER, CallLog.Calls.FEATURES,CallLog.Calls.GEOCODED_LOCATION, CallLog.Calls.IS_READ}
                ,null,
                null,
                null);
        if (cursor.getCount() > 0) {
            // create file
            try {
                String vfile = "callogs.xml";
                File file = new File(context.getExternalFilesDir(null), "callogs");
                if (!file.exists()) {
                    file.mkdir();
                }
                File fileCallLog = new File(file, vfile);
                if (!fileCallLog.exists()) fileCallLog.createNewFile();
                if (fileCallLog.exists()) {
                    Document dom;
                    Element e = null;
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    dom = db.newDocument();
                    Element rootEle = dom.createElement("callogs");
                    while (cursor.moveToNext()) {
                        Element callEle = dom.createElement("callog");
                        rootEle.appendChild(callEle);
                        smsHeader = new String[cursor.getColumnCount()];
                        for (int i = 0; i < cursor.getColumnCount(); i++) {
                            if (cursor.getString(i) != null) {
                                e = dom.createElement(cursor.getColumnName(i));
                                e.appendChild(dom.createTextNode(cursor.getString(i)));
                                callEle.appendChild(e);
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

                    tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(fileCallLog)));

                    // get size all call log
                    float size = (float) fileCallLog.length()/1024;
                    size = (float) (size * 0.001);
                    client.SIZE_ALL_ITEM[1] = (int) size;
                    String result = "Selected : " + cursor.getCount() + " item "+ " - " + String.format("%.03f", size) + " MB";
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

    public void restoreCallogs() {
        String path = context.getExternalFilesDir(null) + "/callogs/callogs.xml";
        File file = new File(path);

        if (file.exists()) {
            try {
                Document dom;
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = null;
                db = dbf.newDocumentBuilder();
                dom = db.parse(file);
                NodeList nl = dom.getElementsByTagName("callog");
                for (int i = 0; i < nl.getLength(); i++) {
                    ContentValues contentValues = new ContentValues();
                    NodeList childNode = nl.item(i).getChildNodes();
                    for (int j = 0; j < childNode.getLength(); j++) {
                        Node node = childNode.item(j);
                        if (node.getNodeType() == Node.ELEMENT_NODE && node.getTextContent() != null) {
                            contentValues.put(node.getNodeName(), node.getTextContent());
                            Log.d("test:", node.getNodeName() + ": " + node.getTextContent());
                        }
                    }
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    context.getContentResolver().insert(CallLog.Calls.CONTENT_URI, contentValues);
                }

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ;
        }
    }
}
