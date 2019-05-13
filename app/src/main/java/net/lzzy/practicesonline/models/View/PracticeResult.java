package net.lzzy.practicesonline.models.View;

import android.annotation.SuppressLint;

import net.lzzy.practicesonline.constants.ApiConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/5/8.
 * Description:
 */
public class PracticeResult {

    public static final String ID_SPLITTER = ",";
    private List<QuestionResult> results;
    private int id;
    private String info;

    public PracticeResult(List<QuestionResult> results, int apiId, String info) {
        this.id = apiId;
        this.results = results;
        this.info = info;
    }

    private double getRatio() {
        int ratioCount = 0;
        for (QuestionResult result : results) {
            if (result.isRight()) {
                ratioCount++;
            }
        }
        //算分数
        return ratioCount * 1.0 / results.size();
    }

    private String getWrongOrders() {
        //错误题目的序号 1.2.3
        int i = 0;
        String ids = "";
        for (QuestionResult result : results) {
            i++;
            if (!result.isRight()) {
                ids = ids.concat(i + ID_SPLITTER);
            }
        }
        if (ids.endsWith(ID_SPLITTER)){
            ids=ids.substring(0,ids.length()-1);
        }
        return ids;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ApiConstants.JSON_RESULT_API_ID, id);
        json.put(ApiConstants.JSON_RESULT_PERSON_INO, info);
        json.put(ApiConstants.JSON_RESULT_SCORE_RATIO,
                new DecimalFormat("#.00").format(getRatio()));
        json.put(ApiConstants.JSON_RESULT_WRONG_IDS, getWrongOrders());
        //json.put(ApiConstants.JSON_RESULT_PHONE_NO, "");
        return json;
    }
}


