package net.lzzy.practicesonline.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Window;

import net.lzzy.practicesonline.utils.AppUtils;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutRes());
        AppUtils.addActivity(this);
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(getContainerId());
        if (fragment == null ){
            fragment = createFragment();
            manager.beginTransaction().add(getContainerId(),fragment).commit();

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.setRunning(getLocalClassName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppUtils.setStopped(getLocalClassName());

    }

    /**
     * Activity的布局文件id
     * @return 布局Id
     *
     */
    protected abstract int getLayoutRes();
    /**
     * fragment容器的id
     * @return id
     *
     */
    protected  abstract  int getContainerId();
    /**
     * 生成托管的Fragment对象
     * @return fragment
     *
     */
    protected  abstract androidx.fragment.app.Fragment createFragment();
}

