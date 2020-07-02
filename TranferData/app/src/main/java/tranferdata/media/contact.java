package tranferdata.media;
import android.accounts.Account;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import tranferdata.adapter.adapterDetail;
import tranferdata.callback.calbackDetail;
import tranferdata.home.R;
import tranferdata.service.getContact;
import tranferdata.service.getMessenger;

public class contact extends Activity implements View.OnClickListener, calbackDetail {
    private TextView btnSave, btnCancel;
    private ListView lvListItem;
    public static adapterDetail adapterDetail;
    private Account[] liAccounts;
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
        for(int i = 0; i< getContact.listItem.size(); i++){
            if(!getContact.listItem.get(i).isChecked()){
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
        adapterDetail = new adapterDetail(this, R.layout.list_item_detail, getContact.listItem, detail);
        lvListItem.setAdapter(adapterDetail);
        checkAll(checkAll, adapterDetail);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_save: {
                setResult(10);
                finish();
                break;
            }
            case R.id.detail_cancel: {
                onBackPressed();
                break;
            }
        }
    }

    @Override
    public void statusCheck(boolean check) {

    }
    void checkAll(final CheckBox checkBox, final adapterDetail adapter){
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    for(int i = 0;i< getContact.listItem.size();i++){
                        getContact.listItem.get(i).setChecked(true);
                    }
                }else{
                    for(int i = 0;i< getContact.listItem.size();i++){
                        getContact.listItem.get(i).setChecked(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
