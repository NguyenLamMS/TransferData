package transferdata.connect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.Nullable;
import transferdata.home.R;
import transferdata.transfer.client;
import transferdata.transfer.server;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver{
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    WifiP2pDevice device;
    private connect mActivity;
    String name = null;
    Boolean NewPhone;
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, connect activity, Boolean NewPhone) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        this.NewPhone = NewPhone;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                System.out.println("WIFI enable...");
            } else {
                System.out.println("WIFI disable..");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    mActivity.statusWifi(false);
                }
                // turn on wifi
                WifiManager wifiManager = (WifiManager)mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if(mManager != null && NewPhone){
                System.out.println("start scan device...");
                mManager.requestPeers(mChannel, mActivity);
            }
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            device = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            name = device.deviceName;
            // Respond to this device's wifi state changing
        }
        get_info_device(intent);
     //   mActivity.connect_success();
        // set device name to class connect

        TextView txt_name_device = mActivity.findViewById(R.id.Device_Name);
        if(name != null && txt_name_device != null){
            txt_name_device.setText(name);
        }

    }
    // Get info device android 10
    void get_info_device(final Intent intent){
        // get devicename
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mManager.requestDeviceInfo(mChannel, new WifiP2pManager.DeviceInfoListener() {
                @Override
                public void onDeviceInfoAvailable(@Nullable WifiP2pDevice wifiP2pDevice) {
                    name = wifiP2pDevice.deviceName;
                }
            });
        }
        //connect success start new activity
        mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                if(mManager == null){
                    return;
                }
                if(info.groupFormed && info.isGroupOwner){
                    Intent intent = new Intent(mActivity, server.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    serverSocket serverSocket = new serverSocket(mActivity);
//                    serverSocket.execute();
                    mActivity.startActivity(intent);
                    mActivity.finish();
                }else if(info.groupFormed){
                    Intent intent = new Intent(mActivity, client.class);
                    intent.putExtra("address", info.groupOwnerAddress.getHostAddress());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    clientSocket clientSocket = new clientSocket(info.groupOwnerAddress.getHostAddress() ,mActivity);
//                    clientSocket.start();
                    mActivity.startActivity(intent);
                    mActivity.finish();
                }
            }
        });
    }
}
