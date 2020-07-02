package tranferdata.media;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import tranferdata.adapter.adapterVideo;
import tranferdata.adapter.itemVideo;
import tranferdata.home.R;

public class video extends Activity {
    GridView gridView;
    GridView gridViewFolder;
    List<itemVideo> list;
    public adapterVideo adapterVideo;
    public adapterVideo adapter;
    TextView txt_type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);
        txt_type = findViewById(R.id.type);
        txt_type.setText("Video");

        adapter = new adapterVideo(this, R.layout.grid_item_image, getVideo.listVideo, true);
        gridView = findViewById(R.id.grid_image);
        gridView.setAdapter(adapter);

        list = new ArrayList<>();
        if(!getVideo.listVideo.isEmpty()){
            list.add(getVideo.listVideo.get(0));
        }
        adapterVideo = new adapterVideo(this, R.layout.grid_item_image, list, false);
        gridViewFolder = findViewById(R.id.grid_image_folder);
        gridViewFolder.setAdapter(adapterVideo);
        clickItemGridView(gridView, adapterVideo);
        saveChooseVideo();
    }
    void clickItemGridView(GridView gridView, final adapterVideo adapter){
       gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               list.clear();
               list.add(getVideo.listVideo.get(position));
               adapter.notifyDataSetChanged();
           }
       });
    }
    void saveChooseVideo(){
        TextView txt_save = findViewById(R.id.save_choose_image);
        txt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        getVideo.listVideo = getIntent().getParcelableArrayListExtra("listVideo");
        super.onBackPressed();
    }
}
