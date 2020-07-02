package transferdata.media;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import transferdata.adapter.adapterDetail;
import transferdata.callback.calbackDetail;
import transferdata.home.R;
public class file extends Activity {
    ListView listViewDetail;
    public CheckBox checkAll;
    calbackDetail detail;
    TextView btn_save, btn_cancel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        checkAll = findViewById(R.id.detail_checl_all);
        TextView title = findViewById(R.id.detail_title);
        title.setText("Choose file");

        // if all item check or uncheck then set checkbox checkAll (true or false)
        detail = new calbackDetail() {
            @Override
            public void statusCheck(boolean check) {
                checkAll.setChecked(check);
            }
        };

        final adapterDetail adapterDetail = new adapterDetail(file.this,R.layout.list_item_detail,getFile.listFile, detail);
        listViewDetail = findViewById(R.id.list_detail);
        listViewDetail.setAdapter(adapterDetail);
        checkAll.setChecked(setCheckAll());
        checkAll(checkAll, adapterDetail);
        clickButton();
    }
    Boolean setCheckAll(){
        for(int i = 0;i<getFile.listFile.size();i++){
            if(!getFile.listFile.get(i).isChecked()){
                return false;
            }
        }
        return true;
    }
    void checkAll(final CheckBox checkBox, final adapterDetail adapter){
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    for(int i = 0;i<getFile.listFile.size();i++){
                        getFile.listFile.get(i).setChecked(true);
                    }
                }else{
                    for(int i = 0;i<getFile.listFile.size();i++){
                        getFile.listFile.get(i).setChecked(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    void clickButton(){
        btn_cancel = findViewById(R.id.detail_cancel);
        btn_save = findViewById(R.id.detail_save);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        getFile.listFile = getIntent().getParcelableArrayListExtra("listFile");
        super.onBackPressed();
    }
}
