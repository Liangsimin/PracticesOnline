package net.lzzy.practicesonline.models;

import net.lzzy.practicesonline.constants.ApiConstants;
import net.lzzy.practicesonline.network.QuestionService;
import net.lzzy.sqllib.Ignored;
import net.lzzy.sqllib.Jsonable;
import net.lzzy.sqllib.Sqlitable;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by lzzy_gxy on 2019/4/17.
 * Description:
 */
public class Question extends BaseEntity implements Sqlitable, Jsonable {


    @Ignored
    public static final  String COL_PRACTICE_ID= "practiceId";
    private String content;
    private int dbType;
    private QuestionType type;

    private String analysis;
    private UUID practiceId;
    private List<Option> options;


    public Question(){
        options=new ArrayList<>();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public QuestionType getType() {
        return type;
    }

    public int getDbType() {
        return dbType;
    }

    public void setDbType(int dbType) {
        this.dbType = dbType;
        type=QuestionType.getInstance(dbType);
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public UUID getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(UUID practiceId) {
        this.practiceId = practiceId;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options .clear();
        this.options.addAll(options);
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return null;
    }

    @Override
public void fromJson(JSONObject json) throws JSONException {
        analysis= json.getString(ApiConstants.JSON_QUESTION_ANALYSIS);
    content= json.getString(ApiConstants.JSON_QUESTION_CONTENT);

    setDbType(json.getInt(ApiConstants.JSON_QUESTION_TYPE));
    String strOptions = json.getString(ApiConstants.JSON_QUESTION_OPTIONS);
    String strAnswers  = json.getString(ApiConstants.JSON_QUESTION_ANSWER);
    try {
        List<Option> options = QuestionService.getOptionsFromJson(strOptions, strAnswers);
        this.options=options;
    }catch (IllegalAccessException|InstantiationException e){
        e.printStackTrace();
    }
}

    @Override
    public boolean needUpdate() {
        return false;
    }
}