package net.lzzy.practicesonline.constants;

import net.lzzy.practicesonline.utils.AppUtils;

/**
 * Created by lzzy_gxy on 2019/4/15.
 * Description:
 */
public class ApiConstants {


    //region 服务器相关
    /**
     * 服务器ip
     */
    private static final String IP= AppUtils.loadServerSetting(AppUtils.getContext()).first;
    /**
     * 服务器端口
     */
    private static final String PORT=AppUtils.loadServerSetting(AppUtils.getContext()).second;
    /**
     * 服务器请求头
     */
    private static final String PROTOCOL="http://";
    //endregion

    //region API接口Url相关
    /**
     * Api地址
     */
    public static final String URL_API=PROTOCOL.concat(IP).concat(":").concat(PORT);
    /**
     * Api项目地址
     */
    private static final String ACTION_PRACTICES="/api/practices";
    public static final String URL_PRACTICES=URL_API.concat(ACTION_PRACTICES);
    private static final String ACTION_QUESTION="/api/pquestions?practiceid=";
    public static final String URL_QUESTION=URL_API.concat(ACTION_QUESTION);
    //endregion
    /**
     * Practice字段
     */
    public static final String JSON_PRACTICE_API_ID="Id";
    public static final String JSON_PRACTICE_API_NAME="Name";
    public static final String JSON_PRACTICE_API_QUESTION_COUNT ="QuestionCount";
    public static final String JSON_PRACTICE_API_UPTIME="UpTime";
    public static final String JSON_PRACTICE_API_ISREADY="IsReady";
    public static final String JSON_PRACTICE_API_OUTLINES="OutLines";

    /**
     * Question字段
     */
    public static final String JSON_QUESTION_ANALYSIS = "Analysis";
    public static final String JSON_QUESTION_CONTENT = "Content";
    public static final String JSON_QUESTION_TYPE = "QuestionType";
    public static final String JSON_QUESTION_OPTIONS = "Options";
    public static final String JSON_QUESTION_ANSWERS = "Answers";
    ;

    /**
     * Option字段
     */
    public static final String JSON_OPTION_CONTENT = "Content";
    public static final String JSON_OPTION_API_ID = "Id";
    public static final String JSON_ANSWERS_OPTION_ID = "OptionId";
    public static final String JSON_OPTION_LABEL = "Label";

    /**
     * post方法的json标签
     */
    public  static  final String JSON_RESULT_API_ID = "PracticeID";
    public  static  final String JSON_RESULT_SCORE_RATIO = "ScroreRatio";
    public  static  final String JSON_RESULT_WRONG_IDS= "WrongQuestionIds";
    public  static  final String JSON_RESULT_PERSON_INO = "PhoneNo";

    /**
     * 提交结果
     */
    private static final String ACTION_RESULT="/api/result/PracticeResult";
    public static final String URL_RESULT=URL_API.concat(ACTION_RESULT);



}