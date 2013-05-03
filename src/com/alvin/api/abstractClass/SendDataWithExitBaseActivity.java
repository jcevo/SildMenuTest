package com.alvin.api.abstractClass;

/*
 * 发送数据,退出,以及菜单功能的基类
 */
public abstract class SendDataWithExitBaseActivity extends SendDataBaseActivity {

    @Override
    public void onBackPressed() {
        ((Alvin_Application) this.getApplication()).exit(this);
    }

}
