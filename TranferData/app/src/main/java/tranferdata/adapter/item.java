package tranferdata.adapter;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class item {
    boolean checked, statusLoad;
    int img_resource , size;
    String name, info, source;
    ArrayList<String> type_choose = new ArrayList<>();
    Drawable imgDrawable;

    public ArrayList<String> getType_choose() {
        return type_choose;
    }

    public void setType_choose(ArrayList<String> type_choose) {
        this.type_choose = type_choose;
    }

    public Drawable getImgDrawable() {
        return imgDrawable;
    }

    public void setImgDrawable(Drawable imgDrawable) {
        this.imgDrawable = imgDrawable;
    }

    public boolean isChecked() {
        return checked;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getImg() {
        return img_resource;
    }

    public void setImg(int img) {
        this.img_resource = img;
    }

    public String getTextView() {
        return name;
    }

    public void setTextView(String textView) {
        this.name = textView;
    }

    public boolean isStatusLoad() {
        return statusLoad;
    }

    public void setStatusLoad(boolean statusLoad) {
        this.statusLoad = statusLoad;
    }

    public int getImg_resource() {
        return img_resource;
    }

    public void setImg_resource(int img_resource) {
        this.img_resource = img_resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public item(boolean checked, int img_resource, String name, String info, Boolean statusLoad) {
        this.checked = checked;
        this.img_resource = img_resource;
        this.name = name;
        this.info = info;
        this.statusLoad = statusLoad;
    }
    public item(){

    }
}
