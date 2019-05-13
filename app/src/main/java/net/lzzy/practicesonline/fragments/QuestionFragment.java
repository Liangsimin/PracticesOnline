package net.lzzy.practicesonline.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.NoCopySpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.models.FavoriteFactory;
import net.lzzy.practicesonline.models.Option;
import net.lzzy.practicesonline.models.Question;
import net.lzzy.practicesonline.models.QuestionFactory;
import net.lzzy.practicesonline.models.QuestionType;
import net.lzzy.practicesonline.models.UserCookies;

import java.util.List;
import java.util.StringTokenizer;

public class QuestionFragment extends BaseFragment {
    private static final String ARG_QUESTION_ID="argQuestionId";
    private static final String ARG_POS="argPos";
    private static final String ARG_IS_COMMITTED="argIsCommitted";
    private Question question;
    private int pos;
    private boolean isCommitted;
    private RadioGroup radioGroup;
    private boolean isMuIti=false;

    public static QuestionFragment newInstance(String questionId,int pos,boolean isCommitted){
        QuestionFragment fragment=new QuestionFragment();
        Bundle args=new Bundle();
        args.putString(ARG_QUESTION_ID,questionId);
        args.putInt(ARG_POS,pos);
        args.putBoolean(ARG_IS_COMMITTED,isCommitted);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            pos=getArguments().getInt(ARG_POS);
            isCommitted=getArguments().getBoolean(ARG_IS_COMMITTED);
            question= QuestionFactory.getInstance().getById(getArguments().getString(ARG_QUESTION_ID));
        }
    }

    @Override
    protected void populate() {
        TextView tvType=find(R.id.fragment_question_type);
        ImageButton imgFavorite=find(R.id.fragment_question_ibt_Favorite);
        radioGroup=find(R.id.fragment_question_option_container);

        //region显示题目
        TextView tvContent=find(R.id.fragment_question_content1);
        int labels=pos+1;
        String qTypes=labels+"."+question.getAnalysis();
        tvContent.setText(qTypes);
        //endregion

        int label=pos+1;
        String qType=label+"."+question.getType().toString();
        tvType.setText(qType);
        int starId= FavoriteFactory.getInstance().isQuestionStarred(question.getId().toString())?
                android.R.drawable.star_on:android.R.drawable.star_big_off;
        imgFavorite.setImageResource(starId);

        //region 设置收藏
        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean collect = FavoriteFactory.getInstance().isQuestionStarred(question.getId().toString());
                if (collect) {
                    FavoriteFactory.getInstance().cancelStarQuestion(question.getId());
                    imgFavorite.setImageResource(android.R.drawable.star_off);
                } else {
                    FavoriteFactory.getInstance().starQuestion(question.getId());
                    imgFavorite.setImageResource(android.R.drawable.star_on);
                }
            }
        });

            /*questionImgFavorite.setOnTouchListener((v, event) -> {
            if (FavoriteFactory.getInstance().isQuestionStarred(question.getId().toString())) {
                FavoriteFactory.getInstance().cancelStarQuestion(question.getId());
                questionImgFavorite.setBackgroundResource(R.drawable.on_favorite);
            } else {
                FavoriteFactory.getInstance().starQuestion(question.getId());
                questionImgFavorite.setBackgroundResource(R.drawable.off_favorite);
            }
            return true;
        });*/
        FavoriteFactory favoriteFactory = FavoriteFactory.getInstance();
        if (favoriteFactory.isQuestionStarred(question.getId().toString())) {
            imgFavorite.setBackgroundResource(android.R.drawable.star_on);
        } else {
            imgFavorite.setBackgroundResource(android.R.drawable.star_off);
        }
        //endregion
        isMuIti=question.getType()==QuestionType.MULTI_CHOICE;
        //region 设置选项
        List<Option> options = question.getOptions();
        for ( Option option : options) {
            CompoundButton btn = isMuIti ? new CheckBox(getContext()) : new RadioButton(getContext());
            btn.setText(option.getLabel() + "、" + option.getContent());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn.setButtonTintList(ColorStateList.valueOf(Color.GRAY));
            }
            btn.setEnabled(!isCommitted);
            //btn.setTextAppearance();
            radioGroup.addView(btn);
            //endregion
            //region 2、选择答案监听
            btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
                UserCookies.getInstance().changeOptionState(option, isChecked, isMuIti);
            });
            boolean shouldCheck = UserCookies.getInstance().isOptionSelected(option);
            if (isMuIti) {
                btn.setChecked(shouldCheck);
            } else if (shouldCheck) {
                radioGroup.check(btn.getId());
            }
            //endregion
            //region 3、答案颜色控制
            if (isCommitted && option.isAnswer()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btn.setTextColor(getResources().getColor(R.color.colorGreen, null));
                } else {
                    btn.setTextColor(getResources().getColor(R.color.colorGreen));

                }
            }
            //endregion
        }

        //endregion

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_question_fragment;
    }

    @Override
    public void search(String kw) {
    }
}
