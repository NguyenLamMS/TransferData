package tranferdata.media;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import tranferdata.adapter.adapterDetail;
import tranferdata.adapter.item;
import tranferdata.callback.calbackDetail;
import tranferdata.home.R;
import tranferdata.service.getMessenger;

public class message extends Activity implements View.OnClickListener, calbackDetail {
    private ListView lvListItem;
    private TextView btnSave, btnCancel;
    public static List<item> itemMain;
    public static adapterDetail adapterDetail;
    calbackDetail detail;
    CheckBox checkAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        mapping();
        addItem();
    }

    public void mapping() {
        lvListItem = findViewById(R.id.list_detail);
        btnSave = findViewById(R.id.detail_save);
        btnCancel = findViewById(R.id.detail_cancel);
        checkAll = findViewById(R.id.detail_checl_all);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }
    Boolean setCheckAll(){
        for(int i = 0;i<getMessenger.listItem.size();i++){
            if(!getMessenger.listItem.get(i).isChecked()){
                return false;
            }
        }
        return true;
    }
    public void addItem() {
        checkAll.setChecked(setCheckAll());
        detail = new calbackDetail() {
            @Override
            public void statusCheck(boolean check) {
                checkAll.setChecked(check);
            }
        };
        adapterDetail = new adapterDetail(message.this, R.layout.list_item_detail, getMessenger.listItem, detail);
        lvListItem.setAdapter(adapterDetail);
        checkAll(checkAll, adapterDetail);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_save: {
                getMessenger.backupMessages();
                finish();
                break;
            }
            case R.id.detail_cancel: {
                onBackPressed();
                break;
            }
        }
    }

    void checkAll(final CheckBox checkBox, final adapterDetail adapter){
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    for(int i = 0;i< getMessenger.listItem.size();i++){
                        getMessenger.listItem.get(i).setChecked(true);
                    }
                }else{
                    for(int i = 0;i< getMessenger.listItem.size();i++){
                        getMessenger.listItem.get(i).setChecked(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void statusCheck(boolean check) {

    }
}
