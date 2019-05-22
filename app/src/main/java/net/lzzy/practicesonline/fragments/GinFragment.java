package net.lzzy.practicesonline.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ExpandedMenuView;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.models.Practice;
import net.lzzy.practicesonline.models.View.QuestionResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/5/13.
 * Description:
 */
public class GinFragment extends BaseFragment {

    private GinFragment.StateActivityInterface gingActivityInterface;
    public static final String QUESTION_RESULTS = "questionResults";
    private List<QuestionResult> questionResults;
    /**
     * GridFragment静态工厂类（创建一个GridFragment）
     * @param questionResults 数据源
     * @return GridFragment
     */
    public static  GinFragment newInstance(List<QuestionResult> questionResults){
        //寄存器（用来暂存数据）
        Bundle args = new Bundle();
        //创建一个GridFragment对象
        GinFragment fragment= new GinFragment();
        //将储存器保存到当前GridFragment对象
        args.putParcelableArrayList(QUESTION_RESULTS,
                (ArrayList<? extends Parcelable>) questionResults);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化取回数据源
        if (getArguments()!=null){
            questionResults = getArguments()
                    .getParcelableArrayList(QUESTION_RESULTS);


        }
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.gin_fragment;
    }

    @Override
    protected void populate() {
        //region 1， 设置适配器
        //初始化GridView视图
        GridView gridView= find(R.id.fragment_grid_gv);
        TextView textView= find(R.id.fragment_grid_cut_view);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gingActivityInterface!=null){
                    gingActivityInterface.gotoChart();
                }

            }
        });
        //创建适配器
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return questionResults.size();
            }

            @Override
            public Object getItem(int position) {
                return questionResults.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
               if (convertView == null){
                   convertView = LayoutInflater.from(getContext()).inflate(R.layout.question_grid_item,null);

               }
                TextView textView=convertView.findViewById(R.id.question_grid_item_number);
               QuestionResult questionResult =questionResults.get(position);
                //设置背景
                if (questionResult.isRight()){
                    textView.setBackgroundResource(R.drawable.question_correct);
                }else {
                    textView.setBackgroundResource(R.drawable.question_error);

                }
                textView.setText(position+1+"");
                textView.setTag(position);
                return  convertView;



            }
        };
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (gingActivityInterface!=null){
                    gingActivityInterface.resultActivityInterface(position);

                }
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GinFragment.StateActivityInterface){
            gingActivityInterface = (GinFragment.StateActivityInterface) context;
        }else {
            throw new ClassCastException(context.toString()+"必须实现StateActivityInterface");
        }
    }



    @Override
    public void onDestroy() {
        gingActivityInterface=null;

        super.onDestroy();
    }

    public interface StateActivityInterface {
        void gotoChart();
        void resultActivityInterface(Integer pos);
    }

    @Override
    public void search(String kw) {

    }
}
