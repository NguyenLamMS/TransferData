package transferdata.adapter;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class item implements Parcelable {
    boolean checked, statusLoad;
    int img_resource , size;
    String name, info, source;
    Drawable imgDrawable;

    protected item(Parcel in) {
        checked = in.readByte() != 0;
        statusLoad = in.readByte() != 0;
        img_resource = in.readInt();
        size = in.readInt();
        name = in.readString();
        info = in.readString();
        source = in.readString();
    }

    public static final Creator<item> CREATOR = new Creator<item>() {
        @Override
        public item createFromParcel(Parcel in) {
            return new item(in);
        }

        @Override
        public item[] newArray(int size) {
            return new item[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeByte((byte) (statusLoad ? 1 : 0));
        dest.writeInt(img_resource);
        dest.writeInt(size);
        dest.writeString(name);
        dest.writeString(info);
        dest.writeString(source);
    }
}
