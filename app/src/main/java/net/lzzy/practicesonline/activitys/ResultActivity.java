package net.lzzy.practicesonline.activitys;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.fragments.AnalysysFragment;
import net.lzzy.practicesonline.fragments.ChartFragment;
import net.lzzy.practicesonline.fragments.GinFragment;
import net.lzzy.practicesonline.fragments.PracticesFragment;
import net.lzzy.practicesonline.models.View.QuestionResult;
import net.lzzy.practicesonline.utils.AppUtils;

import java.time.Instant;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/5/13.
 * Description:
 */
public class ResultActivity extends BaseActivity implements GinFragment.StateActivityInterface,ChartFragment.StateActivityInterface{

    public static final int RESULT_CODE = 1;
    public static final String PRACTICE_ID="practices_Id";

    public static final String POS = "pos";
    public String practiceId;
    private List<QuestionResult> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        practiceId=getIntent().getStringExtra(QuestionActivity.EXTRA_PRACTICE_ID);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_result;
    }

    @Override
    protected int getContainerId() {
        return R.id.activity_result_container;
    }

    @Override
    protected Fragment createFragment()
    {
         results =getIntent().getParcelableArrayListExtra(QuestionActivity.EXTRA_RESULT);
        return GinFragment.newInstance(results);
    }





    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        new AlertDialog.Builder(this)
                .setMessage("返回到哪里？")
                .setPositiveButton("查看收藏", (dialog, which) ->{
                    intent.putExtra(PRACTICE_ID,practiceId);
                    setResult(QuestionActivity.COLLECT_RESULT_CODE,intent);
                    finish();
                })
                .setNegativeButton("章节列表", (dialog, which) -> {
                    intent.setClass(this, PracticesActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNeutralButton("返回题目", (dialog, which) -> {
                    finish();
                }).show();

    }



    @Override
    public void gotoChart() {
        getManager().beginTransaction().replace(R.id.activity_result_container, ChartFragment.newInstance(results)).commit();

    }

    @Override
    public void resultActivityInterface(Integer pos) {
        Intent intent= new Intent();
        intent.putExtra(POS,pos);
        setResult(RESULT_CODE,intent);
            finish();
    }

    @Override
    public void gotoGing() {
        getManager().beginTransaction().replace(R.id.activity_result_container, GinFragment.newInstance(results)).commit();


    }
}




