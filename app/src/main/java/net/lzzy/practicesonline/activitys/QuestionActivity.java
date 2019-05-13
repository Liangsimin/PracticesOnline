package net.lzzy.practicesonline.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;

import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.fragments.QuestionFragment;
import net.lzzy.practicesonline.models.Question;
import net.lzzy.practicesonline.models.QuestionFactory;
import net.lzzy.practicesonline.models.UserCookies;
import net.lzzy.practicesonline.models.View.PracticeResult;
import net.lzzy.practicesonline.models.View.QuestionResult;
import net.lzzy.practicesonline.network.PracticeService;
import net.lzzy.practicesonline.utils.AbstractStaticHandler;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.practicesonline.utils.ViewUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.lzzy.practicesonline.activitys.PracticesActivity.EXTRA_PRACTICE_ID;

public class QuestionActivity extends AppCompatActivity {

    private static final String EXTRA_RESULT = "result";
    public static final int REQUEST_CODE_RESULT =0;
    private String practiceId;
    private int apiId;
    private List<Question> questions;
    private ViewPager pager;
    private LinearLayout linearLayoutDotsContainer;
    private TextView tvView;
    private TextView tvHint;
    private TextView tvCommit;
    private boolean isCommitted=false;
    private FragmentStatePagerAdapter adapter;
    private QuestionFactory questionFactory=QuestionFactory.getInstance();
    private int pos;
    private View[] dots;
    private List<QuestionResult> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_question);
        AppUtils.addActivity(this);
        retrieveData();
        initView();
        setListeners();
        initData();
        pos = UserCookies.getInstance().getCurrentQuestion(practiceId);
        pager.setCurrentItem(pos);
        UserCookies.getInstance().updateReadCount(questions.get(pos).getId().toString());
    }

    private void initData() {
        int count=questions.size();
        dots = new View[count];
        LinearLayout container=findViewById(R.id.activity_question_dots);
        container.removeAllViews();

        //动态生成视图
        int px= ViewUtils.dp2px(16,this);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(px,px);
        px =ViewUtils.dp2px(5,this);
        params.setMargins(px,px,px,px);

        for (int i=0;i<count;i++){
            TextView tvDoc=new TextView(this);
            tvDoc.setLayoutParams(params);
            tvDoc.setBackgroundResource(R.drawable.hollow_circle);
            tvDoc.setTag(i);
            //tvDoc点击翻页
            tvDoc.setOnClickListener(v -> pager.setCurrentItem((Integer) v.getTag()));
            container.addView(tvDoc);
            dots[i]=tvDoc;
        }
    }

    private void refreshDots(int pos){
        for (int i=0;i<dots.length;i++){
            int draawable=i==pos?R.drawable.solid_circle:R.drawable.hollow_circle;
            dots[i].setBackgroundResource(draawable);
        }
    }

    private void setListeners() {
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //滚动过程中
            }

            @Override
            public void onPageSelected(int position) {
                refreshDots(position);
                //页码发生变化后
                UserCookies.getInstance().updateCurrentQuestion(practiceId,position);
                UserCookies.getInstance().updateReadCount(questions.get(position).getId().toString());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //提交监听
        tvCommit.setOnClickListener(v -> commitPractice());
        //查看监听
        tvView.setOnClickListener(v -> redirect());
    }

    private void redirect() {
        List<QuestionResult> results = UserCookies.getInstance().getResultFromCookies(questions);
        Intent intent = new Intent(this,ResultActivity.class);
        intent.putExtra(EXTRA_PRACTICE_ID,practiceId);
        intent.putParcelableArrayListExtra(EXTRA_RESULT,(ArrayList<? extends Parcelable>)results);
        startActivityForResult(intent, REQUEST_CODE_RESULT);

    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        //todo:返回查看数据（全部 or 收藏）
    }

    /**
     * 提交保存答案
     */
    String info;
    private void commitPractice() {
        results = UserCookies.getInstance().getResultFromCookies(questions);
        List<String> macs=AppUtils.getMacAddress();
        String[] items=new String[macs.size()];
        macs.toArray(items);
        info=items[0];
        new AlertDialog.Builder(this)
                .setTitle("确认提交请选择mac地址")
                .setSingleChoiceItems(items, 0, (dialog, which) ->
                        info=items[which])
                .setNegativeButton("取消",null)
                .setPositiveButton("提交", (dialog, which) -> {
                    PracticeResult result=new PracticeResult(results,apiId,"梁思敏： "+info);
                    postResult(result);
                }).show();
    }


    private void postResult(PracticeResult result) {
        //todo:启动线程提交数据
        AppUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int r= PracticeService.postResult(result);
                    if (r>=200&&r<=220){
                        handler.sendEmptyMessage(1);
                    }else {
                        handler.sendEmptyMessage(2);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(2);
                }
            }
        });
    }

    private AbstractStaticHandler<QuestionActivity> handler=new AbstractStaticHandler<QuestionActivity>(this) {
        @Override
        public void handleMessage(Message msg, QuestionActivity questionActivity) {
            switch (msg.what){
                case 1:
                    questionActivity.isCommitted=true;
                    Toast.makeText(questionActivity,"提交成功",Toast.LENGTH_LONG).show();
                    UserCookies.getInstance().commitPractice(practiceId);
                    break;
                case 2:
                    Toast.makeText(questionActivity,"提交失败，请重试！",Toast.LENGTH_LONG).show();
                    Snackbar.make(questionActivity.pager, "提交失败，请重试！", Snackbar.LENGTH_LONG)
                            .setAction("重试", v -> {
                                PracticeResult result=new PracticeResult(results,apiId,"莫槟铭： "+info);
                                questionActivity.postResult(result);
                            }).show();
                    break;
                default:
                    break;
            }
        }
    };


    private void initView() {
        pager = findViewById(R.id.activity_question_pager);
        linearLayoutDotsContainer = findViewById(R.id.activity_question_dots);
        tvView = findViewById(R.id.activity_question_tv_view);
        tvHint = findViewById(R.id.activity_question_tv_hint);
        tvCommit = findViewById(R.id.activity_question_tv_commit);
        if (isCommitted){
            tvCommit.setVisibility(View.GONE);
            tvView.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.VISIBLE);
        }else {
            tvCommit.setVisibility(View.VISIBLE);
            tvView.setVisibility(View.GONE);
            tvHint.setVisibility(View.GONE);
        }
        adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Question question=questions.get(position);
                return QuestionFragment.newInstance(question.getId().toString(),position,isCommitted);
            }

            @Override
            public int getCount() {
                return questions.size();
            }
        };
        pager.setAdapter(adapter);
    }

    private void retrieveData() {
        practiceId=getIntent().getStringExtra(EXTRA_PRACTICE_ID);
        apiId=getIntent().getIntExtra(PracticesActivity.EXTRA_API_ID,-1);
        questions= QuestionFactory.getInstance().getQuestionByPractice(practiceId);
        isCommitted= UserCookies.getInstance().isPracticeCommitted(practiceId);
        if (apiId<0||questions==null||questions.size()==0){
            Toast.makeText(this,"no question",Toast.LENGTH_LONG).show();
            finish();
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
}
