package net.lzzy.practicesonline.fragments;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;

import android.os.AsyncTask;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.util.Pair;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.constants.ApiConstants;
import net.lzzy.practicesonline.models.Practice;
import net.lzzy.practicesonline.models.PracticeFactory;
import net.lzzy.practicesonline.models.Question;
import net.lzzy.practicesonline.models.QuestionFactory;
import net.lzzy.practicesonline.models.UserCookies;
import net.lzzy.practicesonline.network.ApiService;
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
import java.util.Comparator;
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
    //region 参数
    private static final int WHAT_DOWNLOAD_OK = 1;
    private static final int WHAT_DOWNLOAD_ERR = 0;
    private static final int REFRESH_OK = 1;
    private SwipeRefreshLayout swipe;
    private TextView tvHint;
    private TextView tvTime;
    private ListView lv;
    private List<Practice> practices;
    private GenericAdapter<Practice> adapter;
    private PracticeFactory factory = PracticeFactory.getInstance();
    private float touchX1;
    private boolean isDeleteIng = false;
    private ThreadPoolExecutor executor = AppUtils.getExecutor();
    private Pair<Integer, UUID> pair;
    private StateActivityInterface activityInterface;
    //endregion

    //region 主控制器
    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_practices;
    }

    @Override
    protected void populate() {
        //初始化视图
        initViews();
        //适配练习数据
        loadPractices();
        initSwipe();
    }

    @Override
    public void search(String kw) {
        practices.clear();
        if (kw.isEmpty()) {
            practices.addAll(factory.get());
        } else {
            practices.addAll(factory.search(kw));
        }
        adapter.notifyDataSetChanged();
    }
    //endregion

    //region 1、异步Async下载练习
    static class PracticeDownloader extends AsyncTask<Void, Void, String> {
        WeakReference<PracticesFragment> fragment;
        PracticesFragment practicesFragment;

        PracticeDownloader(PracticesFragment fragment) {
            this.fragment = new WeakReference<>(fragment);
            practicesFragment = this.fragment.get();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            practicesFragment.tvHint.setText("正在同步....");
            practicesFragment.isDeleteIng = false;
            practicesFragment.tvHint.setVisibility(View.VISIBLE);
            practicesFragment.tvTime.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return ApiService.okGet(ApiConstants.URL_PRACTICES);
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            practicesFragment.tvTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(new Date()));
            UserCookies.getInstance().updateLastRefreshTime();
            try {
                List<Practice> practiceList = PracticeService.getPractices(s);
                for (Practice practice : practiceList) {
                    practicesFragment.adapter.add(practice);
                }
                practicesFragment.tvHint.setText("刷新成功");

                practicesFragment.executor.execute(() -> {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    practicesFragment.handler2.sendEmptyMessage(REFRESH_OK);
                });
            } catch (Exception e) {
                e.printStackTrace();
                practicesFragment.handlePracticeException(e.getMessage());
            } finally {
                practicesFragment.swipe.setRefreshing(false);
            }

        }
    }

    /**
     * 下载练习线程错误处理
     *
     * @param message 错误信息
     */
    private void handlePracticeException(String message) {
        finishRefresh();
        Snackbar.make(lv, "同步失败\n" + message, Snackbar.LENGTH_LONG)
                .setAction("重试", v -> {
                    swipe.setRefreshing(true);
                    refreshListener.onRefresh();
                }).show();
    }

    /**
     * 新建下载练习线程
     */
    private void downloadPracticesAsync() {
        new PracticeDownloader(PracticesFragment.this).execute();
    }

    /**
     * 结束刷新
     */
    private void finishRefresh() {
        swipe.setRefreshing(false);
        tvHint.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        NotificationManager manager = (NotificationManager) Objects.requireNonNull(getContext()).
                getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(DetectWebService.NOTIFICATION_DETECT_ID);
        }

    }

    /**
     * 手动启用刷新
     */
    public void startRefresh() {
        swipe.post(() -> {
            swipe.setRefreshing(true);
            downloadPracticesAsync();
        });
        swipe.setRefreshing(true);
        refreshListener.onRefresh();
    }

    //endregion
    //region 2、刷新状态Handler线程
    private RefreshHandler handler2 = new RefreshHandler(PracticesFragment.this);

    private static class RefreshHandler extends AbstractStaticHandler<PracticesFragment> {
        RefreshHandler(PracticesFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, PracticesFragment fragment) {
            if (msg.what == REFRESH_OK) {
                fragment.finishRefresh();
            }
        }
    }

    //endregion
    //region 3、解决下拉刷新与列表冲突
    private SwipeRefreshLayout.OnRefreshListener refreshListener = this::downloadPracticesAsync;

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
    //endregion

    //region 主体方法

    /**
     * 初始化视图
     */
    private void initViews() {
        swipe = find(R.id.fragment_practices_swipe);
        tvHint = find(R.id.fragment_practices_tv_hint);
        tvTime = find(R.id.fragment_practices_tv_time);
        tvTime.setText(UserCookies.getInstance().getLastRefreshTime());
        lv = find(R.id.fragment_practices_lv_practices);
        View empty_view = find(R.id.fragment_practices_empty_view);
        lv.setEmptyView(empty_view);
        lv.setOnTouchListener(new ViewUtils.AbstractTouchListener() {
            @Override
            public boolean handleTouch(MotionEvent event) {
                isDeleteIng = false;
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    /**
     * 适配练习数据
     */
    private void loadPractices() {
        practices = factory.get();
        Collections.sort(practices, (o1, o2) -> o2.getDownloadDate().compareTo(o1.getDownloadDate()));
        adapter = new GenericAdapter<Practice>(getContext(), R.layout.practice_item, practices) {
            @Override
            public void populate(ViewHolder viewHolder, Practice practice) {
                viewHolder.setTextView(R.id.practice_item_tv_name, practice.getName());
                Button btnOutlines = viewHolder.getView(R.id.practice_item_btn_outlines);
                if (practice.isDownloaded()) {
                    btnOutlines.setVisibility(View.VISIBLE);
                    btnOutlines.setOnClickListener(v -> new AlertDialog.Builder(getContext()).setMessage(practice.getOutlines()).show());
                } else {
                    btnOutlines.setVisibility(View.GONE);
                }
                Button btnDel = viewHolder.getView(R.id.practice_item_btn_del);
                btnDel.setOnClickListener(v -> new AlertDialog.Builder(getActivity()).setTitle("删除练习")
                        .setMessage("确定删除当前练习")
                        .setNegativeButton("取消", (dialog, which) -> {
                            btnDel.setVisibility(View.GONE);
                            dialog.dismiss();
                        })
                        .setPositiveButton("确定", (dialog, which) -> {
                            isDeleteIng = false;
                            adapter.remove(practice);
                        }).show());
                int visible = isDeleteIng ? View.VISIBLE : View.GONE;
                btnDel.setVisibility(visible);
                viewHolder.getConvertView().setOnTouchListener(new ViewUtils.AbstractTouchListener() {
                    @Override
                    public boolean handleTouch(MotionEvent event) {
                        slidingToMonitor(event, practice, btnDel);
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

    /**
     * 判断触摸动作
     *
     * @param event    MotionEvent
     * @param practice 练习
     * @param btnDel   删除按钮
     */
    private void slidingToMonitor(MotionEvent event, Practice practice, Button btnDel) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float tourX2 = event.getX();
                //滑动
                if (touchX1 - tourX2 > 10) {
                    if (!isDeleteIng) {
                        btnDel.setVisibility(View.VISIBLE);
                        isDeleteIng = true;
                    }
                } else {
                    if (btnDel.isShown()) {
                        btnDel.setVisibility(View.GONE);
                        isDeleteIng = false;
                    } else if (!isDeleteIng) {
                        performItemClick(event, practice, btnDel);
                    }
                }
                break;
        }
    }

    /**
     * 跳转到QuestionFragment
     *
     * @param event    MotionEvent
     * @param practice 练习
     * @param delBtn   删除按钮
     */
    private void performItemClick(MotionEvent event, Practice practice, Button delBtn) {
        if (practice.isDownloaded() && activityInterface != null) {
            activityInterface.stateActivityInterface(practice.getId().toString(), practice.getApiId());
        } else {
            new AlertDialog.Builder(getContext())
                    .setMessage("下载该章节题目吗？")
                    .setPositiveButton("下载", (dialog, which) ->
                            downloadQuestionAsync(practice.getApiId()))
                    .setNegativeButton("取消", null)
                    .show();
        }
    }

    private void downloadQuestionAsync(int apiId) {
        ViewUtils.showProgress(getContext(), "正在下载题目..");
        new QuestionsDownloader(PracticesFragment.this).execute(apiId);
    }

    static class QuestionsDownloader extends AsyncTask<Integer, UUID, String> {
        WeakReference<PracticesFragment> fragment;
        PracticesFragment practicesFragment;
        PracticeFactory practiceFactory = PracticeFactory.getInstance();
        int apiId;

        QuestionsDownloader(PracticesFragment fragment) {
            this.fragment = new WeakReference<>(fragment);
            practicesFragment = this.fragment.get();
        }

        @Override
        protected String doInBackground(Integer... pairs) {
            try {
                apiId = pairs[0];
                return QuestionService.getQuestionOfPracticeFormService(apiId);
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String p) {
            super.onPostExecute(p);
            try {
                List<Question> questions = QuestionService.getQuestions(p,
                        practiceFactory.getPractiveId(apiId));
                QuestionFactory questionFactory = QuestionFactory.getInstance();
                for (Question question : questions) {
                    questionFactory.insert(question);
                }
                Practice practice = practicesFragment.factory.getPracticeByApiId(apiId);
                practice.setDownloaded(true);
                practicesFragment.factory.update(practice);
                practicesFragment.practices.clear();
                practicesFragment.practices.addAll(practicesFragment.factory.get());
                practicesFragment.adapter.notifyDataSetChanged();
                Toast.makeText(practicesFragment.getContext(), "下载成功", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(practicesFragment.lv, "下载失败+\n" + e.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("重试", v -> {
                            practicesFragment.downloadQuestionAsync(apiId);
                        }).show();
            } finally {
                ViewUtils.dismissProgress();
            }
        }

    }


    //endregion








    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StateActivityInterface ){
            activityInterface = (StateActivityInterface) context;
        }else {
            throw new ClassCastException(context.toString()+"必须实现StateActivityInterface");
        }
    }



    @Override
    public void onDestroy() {
        activityInterface=null;
        handler2.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public interface StateActivityInterface {
        void stateActivityInterface(String practiceId,Integer apiId);
    }

    //region 优化前代码
    //region 判断触摸位置是否在视图上
    /*public boolean dispatchTouchEvent(MotionEvent ev,Button del) {
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        if (ViewUtils.isTouchPointInView(del, x, y)) {
            // insided,do somethings you like
            return true;
        }else {
            return false;
        }
    }*/
    //endregion
    //region  DownloadHandler处理线程 (弃用)
    /*private static class DownloadHandler extends AbstractStaticHandler<PracticesFragment> {
        DownloadHandler(PracticesFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, PracticesFragment fragment) {
            switch (msg.what){
                case WHAT_DOWNLOAD_OK:
                    fragment.tvTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(new Date()));
                    UserCookies.getInstance().updateLastRefreshTime();
                    try {
                        List<Practice> practiceList=PracticeService.getPractices(msg.obj.toString());
                        for (Practice practice:practiceList){
                            fragment.adapter.add(practice);
                        }
                        fragment.tvHint.setText("刷新成功");
                        fragment.swipe.setRefreshing(false);
                        fragment.executor.execute(() -> {
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            fragment.handler2.sendEmptyMessage(REFRESH_OK);
                        });
                    } catch (IllegalAccessException|java.lang.InstantiationException|JSONException e) {
                        e.printStackTrace();
                        fragment.handlePracticeException(e.getMessage());
                    }
                    break;
                case WHAT_DOWNLOAD_ERR:
                    fragment.handlePracticeException(msg.obj.toString());
                    break;
                default:
                    break;
            }
        }
    }
    private DownloadHandler handler=new DownloadHandler(PracticesFragment.this);*/
    //endregion
    //endregion
}
