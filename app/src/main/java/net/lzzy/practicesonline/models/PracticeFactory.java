package net.lzzy.practicesonline.models;


import net.lzzy.practicesonline.constants.DbConstants;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PracticeFactory  {
    private static final PracticeFactory ourInstance = new PracticeFactory();
    private SqlRepository<Practice> repository;

    public static PracticeFactory getInstance() {
        return ourInstance;
    }
    private PracticeFactory() {
        repository=new SqlRepository<>(AppUtils.getContext(), Practice.class, DbConstants.packager);

    }
    public List<Practice> search(String kw) {
        try {
            return repository.getByKeyword(kw,new String[]{Practice.PRACTICE_NAME,Practice.PRACTICE_OUTLINES},false);
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



    public UUID getPractiveId(int apiId){
        try {
            List<Practice> practices=repository.getByKeyword(String.valueOf(apiId),
                    new String[]{Practice.PRACTICE_APIID},true);
            if (practices.size()>0){
                return practices.get(0).getId();
            }
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    //修改下载状态
    private void setPracticeDown(String id){
        Practice practice=getById(id);
        if (practice != null){
            practice.setDownloaded(true);
            repository.update(practice);
        }
    }

    public List<Practice> get(){
        return repository.get();
    }
    private Practice getById(String id) {
        return repository.getById(id);
    }

    public void saveQuestions(List<Question> questionList,UUID pUUID){
        for (Question q:questionList){
            ((QuestionFactory)QuestionFactory.getInstance()).insert(q);
        }
        setPracticeDown(pUUID.toString());
    }

    public boolean deletePracticeAndRelated(Practice practice){
        try {
            List<String> sqlActions=new ArrayList<>();
            sqlActions.add(repository.getDeleteString(practice));
            QuestionFactory factory= (QuestionFactory) QuestionFactory.getInstance();
            List<Question> questions=factory.getQuestionByPractice(practice.getId().toString());
            if (questions.size()>0){
                for (Question question:questions){
                    sqlActions.addAll(factory.getDeleteString(question));
                }
            }
            repository.exeSqls(sqlActions);
            if (!isPracticeInDb(practice)){
                //todo:清除Cookiss
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean isPracticeInDb(Practice practice) {

        try {
            return repository.getByKeyword(String.valueOf(practice.getApiId()),
                    new String[]{Practice.PRACTICE_APIID},true).size()>0;
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean add(Practice practice){
        if (isPracticeInDb(practice)){
            return false;
        }
        repository.insert(practice);
        return true;
    }

    public boolean selete(Practice practice){
        if (isPracticeInDb(practice)){
            repository.delete(practice);
            return true;
        }
        return false;
    }

    public boolean update(Practice practice){
        repository.update(practice);
        return true;
    }

    public Practice getPracticeByApiId(int apiId){
        List<Practice> practices=repository.get();
        for (Practice practice:practices){
            if (practice.getApiId()==apiId){
                return practice;
            }
        }
        return null;
    }

    public void saveQuestions() {

    }
}
