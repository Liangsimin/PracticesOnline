package net.lzzy.practicesonline.network;

import net.lzzy.practicesonline.constants.ApiConstants;
import net.lzzy.practicesonline.models.Option;
import net.lzzy.practicesonline.models.Question;
import net.lzzy.sqllib.JsonConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by lzzy_gxy on 2019/4/22.
 * Description:
 */
public class QuestionService {
    public static String getQuestionOfPracticeFormService(int apiId) throws Exception {
        String address= ApiConstants.URL_QUESTION+apiId;
        return ApiService.okGet(address);
    }

    public static List<Question> getQuestions(String json, UUID practiceId) throws Exception {
        JsonConverter<Question> converter=new JsonConverter<>(Question.class);
        List<Question> questions=converter.getArray(json);
        for (Question question:questions){
            question.setPracticeId(practiceId);
        }
        return questions;
    }

    public static List<Option> getOptionFormJson(String jsonOption,String jsonAnswers) throws IllegalAccessException, InstantiationException, JSONException {
        JsonConverter<Option> converter=new JsonConverter<>(Option.class);
        List<Option> options=converter.getArray(jsonOption);
        List<Integer> answerIds=new ArrayList<>();
        JSONArray array = (JSONArray)(new JSONTokener(jsonAnswers)).nextValue();
        for (int i=0;i<array.length();i++){
            JSONObject obj=array.getJSONObject(i);
            answerIds.add(obj.getInt(ApiConstants.JSON_ANSWERS_OPTION_ID));
        }
        for (Option option:options){
            if (answerIds.contains(option.getApiId())){
                option.setAnswer(true);
            }else {
                option.setAnswer(false);
            }
        }
        return options;
    }
}