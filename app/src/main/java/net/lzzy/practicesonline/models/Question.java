package net.lzzy.practicesonline.models;

import android.os.Parcel;
import android.os.Parcelable;


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

public class Question extends BaseEntity implements Sqlitable, Jsonable, Parcelable {
    @Ignored
    static final String Col_PRACTICE_ID="practiceId";
    private String content;//内容
    @Ignored//不持久化
    private QuestionType type;//问题类型
    private int dbType;
    private String analysis;//分析
    private UUID practiceId;//练习ID
    @Ignored//不持久化
    private List<Option> options;//选项

    public Question(){
        options=new ArrayList<>();
    }
    public Question(String content, QuestionType type, int dbType, String analysis, UUID practiceId, List<Option> options) {
        this.content = content;
        this.type = type;
        this.dbType = dbType;
        this.analysis = analysis;
        this.practiceId = practiceId;
        this.options = options;
    }

    protected Question(Parcel in) {
        content = in.readString();
        dbType = in.readInt();
        analysis = in.readString();
    }

    @Ignored
    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

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
        this.options.clear();
        this.options.addAll(options);
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
        analysis=json.getString(ApiConstants.JSON_QUESTION_ANALYSIS);
        content=json.getString(ApiConstants.JSON_QUESTION_CONTENT);
        setDbType(json.getInt(ApiConstants.JSON_QUESTION_TYPE));
        String strOptions=json.getString(ApiConstants.JSON_QUESTION_OPTIONS);
        String strAnswers=json.getString(ApiConstants.JSON_QUESTION_ANSWERS);
        try {
            List<Option> options= QuestionService.getOptionFormJson(strOptions,strAnswers);
            for (Option option:options){
                option.setQuestionId(id);
            }
            setOptions(options);
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeInt(dbType);
        dest.writeString(analysis);
    }
}
