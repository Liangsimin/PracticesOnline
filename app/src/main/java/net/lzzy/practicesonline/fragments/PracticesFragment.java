package net.lzzy.practicesonline.fragments;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.models.Practice;
import net.lzzy.practicesonline.models.PracticeFactory;
import net.lzzy.practicesonline.models.Question;
import net.lzzy.practicesonline.models.UserCookies;
import net.lzzy.practicesonline.network.DetectWebService;
import net.lzzy.practicesonline.network.PracticeService;
import net.lzzy.practicesonline.network.QuestionService;
import net.lzzy.practicesonline.utils.AbstractStaticHandler;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.practicesonline.utils.DateTimeUtils;
import net.lzzy.practicesonline.utils.ViewUtils;
import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by lzzy_gxy on 2019/4/16.
 * Description:
 */
public class PracticesFragment extends BaseFragment {
    private static final int WHAT_PRACTICE_DONE = 0;
    private static final int WHAT_EXCEPTION = 1;
    private static final int WHAT_QUESTION_DONE = 2;
    private static final int WHAT_QUESTION_EXCEPTION = 3;

    private boolean isDelete = false;
    private float touchX1;
    private ListView lv;
    private SwipeRefreshLayout swipe;
    private PracticesFragment.practiceSelecTedListener listener;
    private TextView tvHint;
    private TextView tvTime;
    private List<Practice> practices;
    private GenericAdapter<Practice> adapter;
    private PracticeFactory factory = PracticeFactory.getInstance();
    private ThreadPoolExecutor executor = AppUtils.getExecutor();
    private DownloadHandler handler = new DownloadHandler(this);

    private static class DownloadHandler extends AbstractStaticHandler<PracticesFragment> {
        public DownloadHandler(PracticesFragment context) {
            super(context);
        }
static class QuestionDownloader extends AsyncTask<Void,Void,String>{
    Practice practice;
    WeakReference<PracticesFragment> fragment;
    QuestionDownloader(PracticesFragment fragment,Practice practice){

        this.fragment= new WeakReference<>(fragment);
        this.practice= practice;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ViewUtils.showProgress(fragment.get().getContext(),"开始下载题目...");
    }

    @Override
    protected String doInBackground(Void... voids) {

            try {
                return QuestionService.getQuestionsOfPracticeFromServer(practice.getApiId());
            } catch (IOException e) {
                return  e.getMessage();
            }


    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        fragment.get().saveQuestion(s,practice.getId());
        ViewUtils.dismissProgress();
    }
}



        @Override
        public void handleMessage(Message msg, PracticesFragment fragment) {
            switch (msg.what) {
                case WHAT_PRACTICE_DONE:
                    fragment.tvTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(new Date()));
                    UserCookies.getInstance().updateLastRefreshTime();
                    try {
                        List<Practice> practices = PracticeService.getPractices(msg.obj.toString());
                        for (Practice practice : practices) {
                            fragment.adapter.add(practice);
                        }
                        Toast.makeText(fragment.getContext(), "同步完成", Toast.LENGTH_SHORT).show();
                        fragment.finishRefresh();
                    } catch (Exception e) {
                        e.printStackTrace();
                        fragment.handlePracticeException(e.getMessage());
                    }
                    break;
                case WHAT_EXCEPTION:
                    fragment.handlePracticeException(msg.obj.toString());
                    break;
                case WHAT_QUESTION_DONE:
                    UUID practiceId = fragment.factory.getPracticeId(msg.arg1);
                    try {
                        List<Question> questions = QuestionService.getQuestions(msg.obj.toString(), practiceId);
                        fragment.factory.saveQuestions(questions, practiceId);
                        for (Practice practice : fragment.practices) {
                            if (practice.getId().equals(practiceId)) {
                                practice.setDownloaded(true);
                            }
                        }
                        fragment.adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(fragment.getContext(), "下载失败请重试！", Toast.LENGTH_SHORT).show();
                    }
                    ViewUtils.dismissProgress();
                    break;
                case WHAT_QUESTION_EXCEPTION:
                    ViewUtils.dismissProgress();
                    Toast.makeText(fragment.getContext(),"下载失败请重试\n"+msg.obj.toString(),
                    Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    private void saveQuestion(String json, UUID practiceId) {

        try{
            List<Question> questions=QuestionService.getQuestions(json,practiceId);
            factory.saveQuestions(questions,practiceId);
            for (Practice practice:practices){
                if (practice.getId().equals(practiceId)){
                    practice.setDownloaded(true);
                }
            }
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            Toast.makeText(getContext(),"下载失败请重试\n"+e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    static class PracticeDownloader extends AsyncTask<Void, Void, String> {
        WeakReference<PracticesFragment> fragment;

        PracticeDownloader(PracticesFragment fragment) {
            this.fragment = new WeakReference<>(fragment);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PracticesFragment fragment = this.fragment.get();
            fragment.tvTime.setVisibility(View.VISIBLE);
            fragment.tvHint.setVisibility(View.VISIBLE);


        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                return PracticeService.getPracticesFromServer();

            } catch (IOException e) {
                return e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            PracticesFragment fragment = this.fragment.get();
            fragment.tvTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(new Date()));
            UserCookies.getInstance().updateLastRefreshTime();
            try {
                List<Practice> practices = PracticeService.getPractices(s);
                for (Practice practice : practices) {
                    fragment.adapter.add(practice);
                }
                Toast.makeText(fragment.getContext(), "同步完成", Toast.LENGTH_SHORT).show();
                fragment.finishRefresh();
            } catch (Exception e) {
                e.printStackTrace();
                fragment.handlePracticeException(s);
            }
        }
    }

    static class QuestionDownloader extends AsyncTask<Void, Void, String> {
        WeakReference<PracticesFragment> fragment;
        Practice practice;

        QuestionDownloader(PracticesFragment fragment, Practice practice) {
            this.fragment = new WeakReference<>(fragment);
            this.practice = practice;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            ViewUtils.showProgress(fragment.get().getContext(),"开始下载题目...");
        }

        @Override
        protected String doInBackground(Void... voids) {
            int apiId= practice.getApiId();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private void handlePracticeException(String message) {
        finishRefresh();
        Snackbar.make(lv, "同步失败\n" + message, Snackbar.LENGTH_LONG)
                .setAction("重试", v -> {
                    swipe.setRefreshing(true);
                    refreshListener.onRefresh();
                }).show();

    }


    private void finishRefresh() {
        swipe.setRefreshing(false);
        tvTime.setVisibility(View.GONE);
        tvHint.setVisibility(View.GONE);
        NotificationManager manager = (NotificationManager) Objects.requireNonNull(getContext())
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager !=null){
            manager.cancel(DetectWebService.NOTIFICATION_DETECT_ID);
        }
    }
    public void startRefresh(){
        swipe.setRefreshing(true);
        refreshListener.onRefresh();
    }


    @Override
    protected void populate() {
        initViews();
        loadPractices();
        initSwipe();
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = this::downloadPracticesAsync;

    private void downloadPractices(int apiId) {
        tvTime.setVisibility(View.VISIBLE);
        tvHint.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            try {
                String json = PracticeService.getPracticesFromServer();
                handler.sendMessage(handler.obtainMessage(WHAT_PRACTICE_DONE, json));
            } catch (IOException e) {
                e.printStackTrace();
                handler.sendMessage(handler.obtainMessage(WHAT_EXCEPTION, e.getMessage()));
            }
        });
    }

    private void downloadPracticesAsync() {
        new PracticeDownloader(this).execute();
    }

    private void initSwipe() {
        swipe.setOnRefreshListener(refreshListener);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean isTop = view.getChildCount() == 0 || view.getChildAt(0).getTop() >= 0;
                swipe.setEnabled(isTop);
            }
        });
    }

    private void loadPractices() {
        practices = factory.get();
        Collections.sort(practices, (o1, o2) -> o2.getDownloadDate().compareTo(o1.getDownloadDate()));
        adapter = new GenericAdapter<Practice>(getContext(), R.layout.practice_item, practices) {
            @Override
            public void populate(ViewHolder holder, Practice practice) {
                holder.setTextView(R.id.practice_item_tv_name, practice.getName());
                Button btnOutlines = holder.getView(R.id.practice_item_btn_outlines);
                if (practice.isDownloaded()) {
                    btnOutlines.setVisibility(View.VISIBLE);
                    btnOutlines.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                            .setMessage(practice.getOutlines())
                            .show());
                } else {
                    btnOutlines.setVisibility(View.GONE);
                }
                Button btnDel = holder.getView(R.id.practice_item_btn_del);
                btnDel.setVisibility(View.GONE);
                btnDel.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                        .setTitle("确认删除")
                        .setMessage("要删除订单吗")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", (dialog, i) -> {

                            adapter.remove(practice);
                        })
                        .show());
                int visble = isDelete ? View.VISIBLE : View.GONE;
                btnDel.setVisibility(visble);
                holder.getConvertView().setOnTouchListener(new ViewUtils.AbstractTouchListener() {

                    @Override
                    public boolean handleTouch(MotionEvent event) {
                        slideToDelete(event, practice,btnDel);
                        return true;
                    }
                });


            }



            @Override
            public boolean persistInsert(Practice practice) {
                return factory.add(practice);
            }

            @Override
            public boolean persistDelete(Practice practice) {
                return factory.deletePracticeAndRelated(practice);
            }
        };
        lv.setAdapter(adapter);
    }

    private void slideToDelete(MotionEvent event,Practice practice, Button btn) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float touchX2 = event.getX();
                if (touchX1 - touchX2 > 200) {
                    if (!isDelete) {
                        btn.setVisibility(View.VISIBLE);
                        isDelete = true;

                    }
                } else {
                    if (btn.isShown()){
                        btn.setVisibility(View.VISIBLE);
                        isDelete=true;
                    }else if (!isDelete){
                        performItemClick(practice);
                    }
                }
                break;
            default:
                break;
        }

    }

    private void performItemClick(Practice practice) {
        if (practice.isDownloaded() && listener != null) {
            //todo:跳转到Question视图
            listener.onPracticeSelected(practice.getId().toString(),practice.getApiId());



        } else {
            new AlertDialog.Builder(getContext())
                    .setMessage("下载该章节题目下载吗")
                    .setPositiveButton("下载",( (dialog, which) -> downloadPracticesAsync(practice)))
                    .setNegativeButton("取消", null)
                    .show();
            }
        }

    private void downloadPracticesAsync(Practice practice) {
        new DownloadHandler.QuestionDownloader(this,practice).execute();
    }


    private void downloadQuetions(int apiId){
        //todo:启动线程下载question资源
        ViewUtils.showProgress(getContext(),"开始下载题目...");
        executor.execute(() ->{
                try{
                    String json = QuestionService.getQuestionsOfPracticeFromServer(apiId);
                    Message msg = handler.obtainMessage(WHAT_QUESTION_DONE,json);
                    msg.arg1 = apiId;
                    handler.sendMessage(msg);
                }catch (IOException e){
                    handler.sendMessage(handler.obtainMessage(WHAT_QUESTION_EXCEPTION,e.getMessage()));
                }

        });


    }

    private void initViews() {
        lv=find(R.id.fragment_practices_tv);
        TextView tvNone=find(R.id.fragment_practices_tv_none);
        lv.setEmptyView(tvNone);
        swipe=find(R.id.fragment_practices_swipe);
        tvHint=find(R.id.fragment_practices_tv_hint);
        tvTime=find(R.id.fragment_practices_tv_time);
        tvTime.setText(UserCookies.getInstance().getLastRefreshTime());
        tvHint.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        find(R.id.fragment_practices_tv).setOnTouchListener(new ViewUtils.AbstractTouchListener() {
            @Override
            public boolean handleTouch(MotionEvent event) {
                isDelete = false;
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_practices;
    }

    @Override
    public void search(String kw) {
        practices.clear();
        if (kw.isEmpty()){
            practices.addAll(factory.get());

        }else {
            practices.addAll(factory.search(kw));

        }
        adapter.notifyDataSetChanged();

    }
    public interface practiceSelecTedListener{
        void  onPracticeSelected(String practiceId,int apiId);
    }
}
