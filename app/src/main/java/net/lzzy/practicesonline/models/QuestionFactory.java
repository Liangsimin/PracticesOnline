package net.lzzy.practicesonline.models;

import android.text.TextUtils;


import net.lzzy.practicesonline.constants.DbConstants;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.util.ArrayList;
import java.util.List;

public class QuestionFactory {
    private static final QuestionFactory ourInstance = new QuestionFactory();
    private SqlRepository<Question> repository;
    private SqlRepository<Option> optionRepository;
    public static QuestionFactory getInstance() {
        return ourInstance;
    }
    private QuestionFactory() {
        repository=new SqlRepository<>(AppUtils.getContext(), Question.class, DbConstants.packager);
        optionRepository=new SqlRepository<>(AppUtils.getContext(), Option.class, DbConstants.packager);
    }





    private void completeQuestion(Question question) throws InstantiationException, IllegalAccessException {
        List<Option> options=optionRepository.getByKeyword(question.getId().toString(),new String[]{Option.QUESTION_ID},true);
        question.setOptions(options);
        question.setDbType(question.getDbType());
    }

    public Question getById(String practiceId){
        try {
            Question question=repository.getById(practiceId);
            completeQuestion(question);
            return question;
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Question> getQuestionByPractice(String practiceId){
        try{
            List<Question> questions=repository.getByKeyword(practiceId,
                    new String[]{Question.Col_PRACTICE_ID},true);
            for (Question question:questions){
                completeQuestion(question);
            }
            return questions;
        }catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public void insert(Question question){
        //todo:1
        List<Option> options=question.getOptions();
        List<String> sqlActions=new ArrayList<>();
        for (Option option:options){
            sqlActions.add(optionRepository.getInsertString(option));
        }
        sqlActions.add(repository.getInsertString(question));
        //执行插入语句
        repository.exeSqls(sqlActions);
    }
    public List<String> getDeleteString(Question question){
        List<String> sqlActions=new ArrayList<>();
        sqlActions.add(repository.getDeleteString(question));
        for (Option option:question.getOptions()){
            sqlActions.add(optionRepository.getDeleteString(option));
        }
        String f=((FavoriteFactory) FavoriteFactory.getInstance()).getDeleteString(question.getId().toString());
        if (!TextUtils.isEmpty(f)){
            sqlActions.add(f);
        }
        return sqlActions;
    }
}
