package tranferdata.media;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import tranferdata.adapter.*;
import tranferdata.home.R;

public class image extends Activity {
    GridView gridView;
    GridView gridViewFolder;
    public List<itemImage> listImage;
    List<itemImage> list;
    public adapterImage adapterImage;
    public adapterImage adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);
        listImage = new ArrayList<>();
        getDataImage dataImage = new getDataImage();
        listImage = dataImage.listImage;
        // grid view folder
        adapter = new adapterImage(this, R.layout.grid_item_image, listImage, true);
        gridView = findViewById(R.id.grid_image);
        gridView.setAdapter(adapter);

        // grid view image of folder
        list = new ArrayList<>();
        if(!listImage.isEmpty()){
            list.add(listImage.get(0));
        }
        adapterImage = new adapterImage(this, R.layout.grid_item_image, list, false);
        gridViewFolder = findViewById(R.id.grid_image_folder);
        gridViewFolder.setAdapter(adapterImage);
        //click album image
        clickItemGridView(gridView, adapterImage);
        //save list image
        saveChooseImage();

    }
     void clickItemGridView(GridView gridView, final adapterImage adapter){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list.clear();
                list.add(listImage.get(position));
                adapter.notifyDataSetChanged();
            }
        });
    }
    void saveChooseImage(){
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
        getDataImage dataImage = new getDataImage();
        dataImage.listImage = getIntent().getParcelableArrayListExtra("listImage");
        super.onBackPressed();
    }
}
