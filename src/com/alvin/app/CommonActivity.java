package com.alvin.app;

import com.alvin.api.config.Env;
import com.alvin.common.utils.CountUtils;
import com.alvin.ui.SimpleToast;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class CommonActivity {
    private final static String TAG = CommonActivity.class.getSimpleName();

    public static interface ActivityInterface {
        public void doSuperBackPressed();
        public boolean isRoot();
    }

    public static class BaseActivity extends Activity implements ActivityInterface {
        private boolean root = false;
        protected BackPressedListener backPressedListener = new BackPressedListener(this);

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            CommonActivity.onCreate(this);
        }

        protected void onResume() {
            super.onResume();
            CommonActivity.onResume(this);
        }

        protected void onPause() {
            super.onPause();
            CommonActivity.onPause(this);
        }

        protected void onStop() {
            super.onStop();
            CommonActivity.onStop(this);
        }

        public void onBackPressed() {
            CommonActivity.onBackPressed(this);
        }

        public void doSuperBackPressed() {
            super.onBackPressed();
        }

        protected void setAsRoot() {
            root = true;
        }

        public boolean isRoot() {
            return root;
        }

        protected void showShortToast(String msg) {
            SimpleToast.show(this, msg, Toast.LENGTH_SHORT);
        }

        protected void showLongToast(String msg) {
            SimpleToast.show(this, msg, Toast.LENGTH_LONG);
        }
    }

    public static class BaseListActivity extends ListActivity implements ActivityInterface {
        private boolean root = false;
        protected BackPressedListener backPressedListener = new BackPressedListener(this);

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            CommonActivity.onCreate(this);
        }

        protected void onResume() {
            super.onResume();
            CommonActivity.onResume(this);
        }

        protected void onPause() {
            super.onPause();
            CommonActivity.onPause(this);
        }

        protected void onStop() {
            super.onStop();
            CommonActivity.onStop(this);
        }

        public void onBackPressed() {
            CommonActivity.onBackPressed(this);
        }

        public void doSuperBackPressed() {
            super.onBackPressed();
        }

        protected void setAsRoot() {
            root = true;
        }

        public boolean isRoot() {
            return root;
        }

        protected void showShortToast(String msg) {
            SimpleToast.show(this, msg, Toast.LENGTH_SHORT);
        }

        protected void showLongToast(String msg) {
            SimpleToast.show(this, msg, Toast.LENGTH_LONG);
        }
    }

    public static class BaseActivityGroup extends ActivityGroup implements ActivityInterface {
        private boolean root = false;
        protected BackPressedListener backPressedListener = new BackPressedListener(this);

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            CommonActivity.onCreate(this);
        }

        protected void onResume() {
            super.onResume();
            CommonActivity.onResume(this);
        }

        protected void onPause() {
            super.onPause();
            CommonActivity.onPause(this);
        }

        protected void onStop() {
            super.onStop();
            CommonActivity.onStop(this);
        }

        public void onBackPressed() {
            CommonActivity.onBackPressed(this);
        }

        public void doSuperBackPressed() {
            super.onBackPressed();
        }

        protected void setAsRoot() {
            root = true;
        }

        public boolean isRoot() {
            return root;
        }

        protected void showShortToast(String msg) {
            SimpleToast.show(this, msg, Toast.LENGTH_SHORT);
        }

        protected void showLongToast(String msg) {
            SimpleToast.show(this, msg, Toast.LENGTH_LONG);
        }
    }

    //返回按钮监听
    protected static class BackPressedListener implements OnClickListener {
        private Activity activity;

        public BackPressedListener(Activity activity) {
            this.activity = activity;
        }

        public void onClick(View v) {
            activity.onBackPressed();
        }
    }

    private static void onCreate(Activity activity) {
        resetTopActivity(activity);
        if(Env.display == null) {
            Env.display = activity.getWindowManager().getDefaultDisplay();
        }

        Initializer initializer = ((CommonApplication)activity.getApplication()).getInitializer();
        if(initializer != null) {
            initializer.init(activity);
        }
    }

    private static void onResume(Activity activity) {
        resetTopActivity(activity);
        CountUtils.updateUseTime(false);
//        System.out.println("##### onResume - " + activity);
    }

    private static void onPause(Activity activity) {
        CountUtils.updateUseTime(false);
//        System.out.println("##### onPause - " + activity);
    }

    private static void onStop(Activity activity) {

    }

    private static void onBackPressed(Activity activity) {
        Activity parent = activity;
        while(parent.getParent() != null) {
            parent = parent.getParent();
        }

        if(((ActivityInterface)parent).isRoot()) {
//            ((CommonApplication)parent.getApplication()).exit(parent);
        } else {
            ((ActivityInterface)parent).doSuperBackPressed();
        }
    }

    private synchronized static void resetTopActivity(Activity activity) {
        Activity parent = activity;
        while(parent.getParent() != null) {
            parent = parent.getParent();
        }
        if(parent != Env.topActivity) {
            Env.topActivity = parent;
            Log.i(TAG, "Top activity = " + parent);
        }
    }
}
