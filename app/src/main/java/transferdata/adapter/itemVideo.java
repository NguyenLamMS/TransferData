package transferdata.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class itemVideo implements Parcelable {
    String folderName;
    Boolean select;
    List<infoItemVideo> listVideo;

    public itemVideo(String folderName, boolean select, List<infoItemVideo> listVideo) {
        this.folderName = folderName;
        this.select = select;
        this.listVideo = listVideo;
    }

    protected itemVideo(Parcel in) {
        folderName = in.readString();
        byte tmpSelect = in.readByte();
        select = tmpSelect == 0 ? null : tmpSelect == 1;
        listVideo = in.createTypedArrayList(infoItemVideo.CREATOR);
    }

    public static final Creator<itemVideo> CREATOR = new Creator<itemVideo>() {
        @Override
        public itemVideo createFromParcel(Parcel in) {
            return new itemVideo(in);
        }

        @Override
        public itemVideo[] newArray(int size) {
            return new itemVideo[size];
        }
    };

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<infoItemVideo> getListVideo() {
        return listVideo;
    }

    public void setListVideo(List<infoItemVideo> listVideo) {
        this.listVideo = listVideo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(folderName);
        dest.writeByte((byte) (select == null ? 0 : select ? 1 : 2));
        dest.writeTypedList(listVideo);
    }
}
