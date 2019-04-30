package net.lzzy.practicesonline.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activitys.SplashActivity;

/**
 * Created by lzzy_gxy on 2019/4/15.
 * Description:
 */

public class ViewUtils {
    private static  AlertDialog dialog;
    public  static  void  showProgress(Context context,String message){
        if (dialog == null){
            View view = LayoutInflater.from(context).inflate(R.layout.dia_progress,null);
            TextView tv= view.findViewById(R.id.dialog_progress_tv);
            tv.setText(message);
            dialog = new AlertDialog.Builder(context).create();
            dialog.setView(view);

        }dialog.show();
    }
    public static void dismissProgress(){
        if (dialog!=null && dialog.isShowing()){
            dialog.isShowing();
        }
    }
    public static  void  gotoSetting(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_seting,null);
        Pair<String,String>url = AppUtils.loadServerSetting(context);
        EditText editId = view.findViewById(R.id.dialog_setting_edt_ip);
       editId.setText(url.first);
        EditText editPort = view.findViewById(R.id.dialog_setting_edt_port);
        editPort.setText(url.second);
        new AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton("取消",(dialog, which) -> gotoMain(context))
                .setPositiveButton("保存",(dialog, which) -> {
                    String ip = editId.getText().toString();
                    String port = editPort.getText().toString();
                    if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)){
                        Toast.makeText(context,"信息不完整", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    AppUtils.saveServerSetting(ip,port,context);
                    gotoMain(context);

}).show();
    }
    public abstract static class AbstractTouchListener implements View.OnTouchListener{
        @Override
        @SuppressWarnings("ClickableViewAccessibility")
        public boolean onTouch(View v, MotionEvent event){
            return handleTouch(event);
        }

        /**
         * 处理触摸事件
         * @param event 触摸事件对象
         * @return 是否拦截触摸事件
         */

        public abstract boolean handleTouch(MotionEvent event);

    }
    private static void gotoMain(Context context){
        if (context instanceof SplashActivity){
            ((SplashActivity)context).gotoMain();
        }
    }

    public abstract static  class  AbstractQueryListener implements SearchView.OnQueryTextListener{
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            handleQuery(newText);
            return false;
        }

        /**
         * 处理搜索逻辑
         * @param kw 搜索关键词
         */
        public abstract void handleQuery(String kw);
    }
}
