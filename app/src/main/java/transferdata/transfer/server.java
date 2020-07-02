package transferdata.transfer;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import transferdata.connect.connect;
import transferdata.home.R;
import transferdata.socket.serverSocket;
public class server extends Activity{
    public static WifiP2pManager manager;
    public static WifiP2pManager.Channel channel;
    Button btn_disconnect;
    TextView txtNameHost;
    serverSocket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_conect);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(),null);
        btn_disconnect = findViewById(R.id.btn_disconnect);
        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect.isConnect(server.this);
            }
        });
        // start socket server
        socket = new serverSocket(server.this);
        socket.execute();
        show_name_server();
    }
    @Override
    public void onBackPressed() {
        connect.isConnect(server.this);
    }
    private void show_name_server(){
        manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                txtNameHost = findViewById(R.id.name_server);
                txtNameHost.setText(group.getOwner().deviceName);
            }
        });
    }
}

