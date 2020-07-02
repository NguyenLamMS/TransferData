//package tranferdata.adapter;
//
//import android.app.Activity;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.CheckBox;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import java.util.ArrayList;
//
//import tranferdata.home.R;
//import tranferdata.media.contact;
//
//public class adapterChooseApp extends ArrayAdapter<item> {
//
//    private Context context;
//    private int resouse;
//    private ArrayList<item> itemList;
//    private contact contactActivity;
//
//    public adapterChooseApp(@NonNull Context context, int resource, @NonNull ArrayList<item> objects) {
//        super(context, resource, objects);
//        this.context = context;
//        this.resouse = resource;
//        this.itemList = objects;
//    }
//
//    @Override
//    public int getCount() {
//        return itemList.size();
//    }
//
//    @Nullable
//    @Override
//    public item getItem(int position) {
//        return super.getItem(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int position,View convertView, ViewGroup parent) {
//        View view = LayoutInflater.from(context).inflate(resouse,null);
//        CheckBox cb_item = (CheckBox) view.findViewById(R.id.cb_item);
//        TextView txt_item = (TextView) view.findViewById(R.id.txt_item);
//        ImageView img_item = (ImageView) view.findViewById(R.id.img_item);
//        item it = itemList.get(position);
//        cb_item.setChecked(it.checked);
//        txt_item.setText(it.name);
//        img_item.setImageDrawable(it.getImgDrawable());
//        clickCheckbox(cb_item,position);
//        return view;
//    }
//
//    void clickCheckbox(final CheckBox cb, final int pos){
//        cb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(cb.isChecked()){
//                    itemList.get(pos).checked = true;
//                }else {
//                    itemList.get(pos).checked = false;
//                }
//            }
//        });
//    }
//
//
//}
//
