package tranferdata.connect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import tranferdata.home.R;
import tranferdata.tranfer.client;


public class connect extends Activity implements WifiP2pManager.PeerListListener {
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    ListView list_device;
    TextView txtNotFound;
    ArrayAdapter<String> arrayAdapter;
    List<String>list_device_near = new ArrayList<String>();
    Boolean NewPhone = false;
    private List<WifiP2pDevice> peerslist = new ArrayList<WifiP2pDevice>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent1 = new Intent(this, client.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
        finish();
        // check status GPS
        isGPSEnabled(this);

        // check inten new phone or old phone
        Intent intent = this.getIntent();
        NewPhone = intent.getBooleanExtra("NewPhone",true);
        if(NewPhone){
            setContentView(R.layout.new_phone);
            find_id();
            load_data_listview();
        }else{
            setContentView(R.layout.phone_old);
        }

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(),null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this, NewPhone);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("Successs");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("false");
            }
        });

    }
    void find_id(){

        list_device = findViewById(R.id.list_device_near);
        txtNotFound = findViewById(R.id.txtnotfound);
    }
    void load_data_listview(){
        if(list_device_near.size() == 0){
            txtNotFound.setVisibility(View.VISIBLE);
        }else{
            txtNotFound.setVisibility(View.GONE);
        }
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_device_near);
        list_device.setAdapter(arrayAdapter);

        list_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Adress = peerslist.get(position).deviceAddress;
                if(Adress != null){
                    connect_device(Adress);
                }
            }
        });
    }
    void connect_device(final String Address){
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = Address;
        config.groupOwnerIntent = 15;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("Connect Successs");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("Connect false");
            }
        });
    }
    void statusWifi(boolean status){
        if(!status){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Wifi not enable");
            builder.setMessage("Need to turn on wifi to scan devices around");
            builder.setPositiveButton("Open Setting", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        if(!peerslist.equals(peers) && NewPhone){
            peerslist.clear();
            list_device_near.clear();
            peerslist.addAll(peers.getDeviceList());
            for(WifiP2pDevice index : peerslist){
                list_device_near.add(index.deviceName);
            }
            arrayAdapter.notifyDataSetChanged();
            if(list_device_near.size() == 0){
                txtNotFound.setVisibility(View.VISIBLE);
            }else{
                txtNotFound.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public boolean isGPSEnabled(Context mContext)
    {
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsStatus = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsStatus) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS not enable");
            builder.setMessage("Need to turn on GPS to scan devices around");
            builder.setPositiveButton("Open Setting", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return gpsStatus;
    }
}