package net.lzzy.practicesonline.models;


import net.lzzy.practicesonline.constants.ApiConstants;
import net.lzzy.sqllib.Ignored;
import net.lzzy.sqllib.Jsonable;
import net.lzzy.sqllib.Sqlitable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Option extends BaseEntity implements Sqlitable, Jsonable {
    @Ignored
    static final String QUESTION_ID = "questionId";
    private String content;
    private String label;
    private UUID questionId;
    private boolean isAnswer;
    private int apiId;

    public Option() {
    }

    public Option(String content, String label, UUID questionId, boolean isAnswer, int apiId) {
        this.content = content;
        this.label = label;
        this.questionId = questionId;
        this.isAnswer = isAnswer;
        this.apiId = apiId;
    }

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

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public boolean isAnswer() {
        return isAnswer;
    }

    public void setAnswer(boolean answer) {
        isAnswer = answer;
    }

    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
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
        label=json.getString(ApiConstants.JSON_OPTION_LABEL);
        apiId=json.getInt(ApiConstants.JSON_OPTION_API_ID);
        content=json.getString(ApiConstants.JSON_OPTION_CONTENT);
    }
}
