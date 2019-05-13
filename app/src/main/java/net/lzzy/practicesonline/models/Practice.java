package net.lzzy.practicesonline.models;


import net.lzzy.practicesonline.constants.ApiConstants;
import net.lzzy.practicesonline.network.ApiService;
import net.lzzy.sqllib.Ignored;
import net.lzzy.sqllib.Jsonable;
import net.lzzy.sqllib.Sqlitable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Practice extends BaseEntity implements Sqlitable, Jsonable {
    @Ignored
    static final String PRACTICE_NAME = "name";
    @Ignored
    static final String PRACTICE_OUTLINES = "outlines";
    @Ignored
    static final String PRACTICE_APIID = "apiId";
    private String name;//练习名称
    private int questionCount;//问题数
    private Date downloadDate;//下载时间
    private String outlines;//概括
    private boolean isDownloaded;//是否已下载
    private int apiId;

    public Practice() {
    }

    public Practice(String name, int questionCount, Date downloadDate, String outlines, boolean isDownloaded, int apild) {
        this.name = name;
        this.questionCount = questionCount;
        this.downloadDate = downloadDate;
        this.outlines = outlines;
        this.isDownloaded = isDownloaded;
        this.apiId = apiId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }

    public String getOutlines() {
        return outlines;
    }

    public void setOutlines(String outlines) {
        this.outlines = outlines;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
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
        apiId=json.getInt(ApiConstants.JSON_PRACTICE_API_ID);
        name=json.getString(ApiConstants.JSON_PRACTICE_API_NAME);
        questionCount=json.getInt(ApiConstants.JSON_PRACTICE_API_QUESTION_COUNT);
        outlines=json.getString(ApiConstants.JSON_PRACTICE_API_OUTLINES);
        downloadDate= new Date();
    }
}
