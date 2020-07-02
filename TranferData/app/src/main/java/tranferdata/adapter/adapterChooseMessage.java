//package tranferdata.adapter;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.CheckBox;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import java.util.ArrayList;
//
//import tranferdata.home.R;
//import tranferdata.media.message;
//
//public class adapterChooseMessage extends ArrayAdapter<item> {
//
//    private Context context;
//    private int resouse;
//    private ArrayList<item> itemList;
//    private message messageActivity;
//
//    public adapterChooseMessage(@NonNull Context context, int resource, @NonNull ArrayList<item> objects) {
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
//        CheckBox cb_item = (CheckBox) view.findViewById(R.id.detail_check);
//        TextView txt_item = (TextView) view.findViewById(R.id.detail_name);
//        item it = itemList.get(position);
//        cb_item.setChecked(it.checked);
//        txt_item.setText(it.name);
//        clickCheckbox(cb_item,position);
//        return view;
//    }
//
//    void clickCheckbox(final CheckBox cb, final int pos){
//        cb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(cb.isChecked()){
//                    if(pos == 0) {
//                        for (item i : itemList) i.setChecked(false);
//                    }
//                    else {
//                        if(itemList.get(0).checked) itemList.get(0).setChecked(false);
//                    }
//                    itemList.get(pos).checked = true;
//                    messageActivity.adapterChooseMessage.notifyDataSetChanged();
//                }else {
//
//                    if(pos == 0) {
//                        boolean check = true;
//                        for (item i: itemList) if(i.checked) {
//                            if(pos == 0) continue;
//                            check = false;
//                            break;
//                        }
//                        itemList.get(pos).checked = check;
//                    }
//                    else itemList.get(pos).checked = false;
//                    messageActivity.adapterChooseMessage.notifyDataSetChanged();
//                }
//            }
//        });
//    }
//}
//
