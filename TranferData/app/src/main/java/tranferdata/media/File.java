package tranferdata.media;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import androidx.annotation.Nullable;
import tranferdata.adapter.adapterDetail;
import tranferdata.callback.calbackDetail;
import tranferdata.home.R;
public class File extends Activity {
    ListView listViewDetail;
    public CheckBox checkAll;
    calbackDetail detail;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        checkAll = findViewById(R.id.detail_checl_all);

        // if all item check or uncheck then set checkbox checkAll (true or false)
        detail = new calbackDetail() {
            @Override
            public void statusCheck(boolean check) {
                checkAll.setChecked(check);
            }
        };

        final adapterDetail adapterDetail = new adapterDetail(File.this,R.layout.list_item_detail,getFile.listFile, detail);
        listViewDetail = findViewById(R.id.list_detail);
        listViewDetail.setAdapter(adapterDetail);
        checkAll.setChecked(setCheckAll());
        checkAll(checkAll, adapterDetail);

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
}
