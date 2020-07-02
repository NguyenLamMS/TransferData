package tranferdata.adapter;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class itemImage implements Parcelable {
    String folderName;
    Boolean select;
    List<infoImage>listPathImage;
    public itemImage(String folderName, Boolean select, List<infoImage> listPathImage) {
        this.folderName = folderName;
        this.select = select;
        this.listPathImage = listPathImage;
    }

    public itemImage() {
    }

    protected itemImage(Parcel in) {
        folderName = in.readString();
        byte tmpSelect = in.readByte();
        select = tmpSelect == 0 ? null : tmpSelect == 1;
        listPathImage = in.createTypedArrayList(infoImage.CREATOR);
    }

    public static final Creator<itemImage> CREATOR = new Creator<itemImage>() {
        @Override
        public itemImage createFromParcel(Parcel in) {
            return new itemImage(in);
        }

        @Override
        public itemImage[] newArray(int size) {
            return new itemImage[size];
        }
    };

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<infoImage> getListPathImage() {
        return listPathImage;
    }

    public void setListPathImage(List<infoImage> listPathImage) {
        this.listPathImage = listPathImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(folderName);
        dest.writeByte((byte) (select == null ? 0 : select ? 1 : 2));
        dest.writeTypedList(listPathImage);
    }
}
