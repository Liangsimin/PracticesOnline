package net.lzzy.practicesonline.models;

import net.lzzy.practicesonline.constants.ApiConstants;
import net.lzzy.sqllib.Jsonable;
import net.lzzy.sqllib.Sqlitable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by lzzy_gxy on 2019/4/16.
 * Description:
 */
public class Option extends BaseEntity implements Sqlitable, Jsonable {
    public static final String COL_QUESTION_ID="questionld";
    private String content;
    private String label;
    private UUID questionld;
    private boolean isAnswer;
    private int apild;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public UUID getQuestionld() {
        return questionld;
    }

    public void setQuestionld(UUID questionld) {
        this.questionld = questionld;
    }

    public boolean isAnswer() {
        return isAnswer;
    }

    public void setAnswer(boolean answer) {
        isAnswer = answer;
    }

    public int getApild() {
        return apild;
    }

    public void setApild(int apild) {
        this.apild = apild;
    }

    @Override
    public boolean needUpdate() {
        return false;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return null;
    }

    @Override
    public void fromJson(JSONObject json) throws JSONException {

        content = json.getString(ApiConstants.JSON_OPTION_CONTENT);
        label = json.getString(ApiConstants.JSON_OPTION_LABEL);
        apild = json.getInt(ApiConstants.JSON_OPTION_API_ID);



    }

}
