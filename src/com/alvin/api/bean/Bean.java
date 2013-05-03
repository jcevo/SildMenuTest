package com.alvin.api.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 列表item josn所需的bean
 * 
 * 项目名称：app43 类名称：App 类描述： 创建人：pc 创建时间：2011-11-11 上午9:52:29 修改人：pc
 * 修改时间：2011-11-11 上午9:52:29 修改备注：
 * 
 * @version
 * 
 */
public class Bean extends BasicBean implements Parcelable {
    /**
     * 切记bean里存储小对象 dest.wtite 和source.read 要一一对应
     */
    String title;
    String downloadUrl;// 下载URL
    String updateTime;// 更新包时间
    int progress;// 进度
    long downDate;// 下载的时间戳
    int state;// 下载是否完成

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bean(Parcel in) {

    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getDownDate() {
        return downDate;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setDownDate(long date) {
        this.downDate = date;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(downloadUrl);
        dest.writeInt(progress);
        dest.writeInt(state);
        dest.writeLong(downDate);
        dest.writeString(updateTime);
    }

    public static final Parcelable.Creator<Bean> CREATOR = new Parcelable.Creator<Bean>() {

        @Override
        public Bean createFromParcel(Parcel source) {
            // 在组件之间传递对象的时候要序列化相应的字段
            Bean app = new Bean();
            app.title = source.readString();
            app.downloadUrl = source.readString();
            app.progress = source.readInt();
            app.state = source.readInt();
            app.downDate = source.readLong();
            app.updateTime = source.readString();
            // app.id = source.readInt();
            return app;
        }

        @Override
        public Bean[] newArray(int size) {
            return new Bean[size];
        }
    };

    public Bean() {

    }

}
