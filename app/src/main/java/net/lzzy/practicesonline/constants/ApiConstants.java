package net.lzzy.practicesonline.constants;

import net.lzzy.practicesonline.utils.AppUtils;

/**
 * Created by lzzy_gxy on 2019/4/15.
 * Description:
 */
public class ApiConstants {
    private  static  final  String  IP= AppUtils.loadServerSetting(AppUtils.getContext()).first;
    private  static  final  String PORT=AppUtils.loadServerSetting(AppUtils.getContext()).second;
    private  static  final  String PROTOCOL="http://";


    /**
     *API地址
     **/
    public static  final  String URL_API=PROTOCOL.concat(IP).concat(":").concat(PORT);
    public  static  final  String ACTION_PRACTICES="/api/practices";


    /**
     *questions地址
     **/
    public  static  final  String ACTION_QUESTIONS="/api/pquestions?practiceid=";
    public  static  final  String URL_QUESTIONS= URL_API.concat(ACTION_QUESTIONS);

    /**
     *practices地址
     **/
    public  static  final  String URL_PRACTICES=URL_API.concat(ACTION_PRACTICES);
    public  static  final  String JSON_PRACTICE_API_ID="Id";
    public  static  final  String JSON_PRACTICE_NAME="Name";
    public  static  final  String JSON_PRACTICE_OUTLINES="OutLines";
    public  static  final  String JSON_PRACTICE_QUESTION_COUNT="QuestionCount";


    /**
     *question地址
     **/
    public static final String JSON_QUESTION_ANALYSIS="Analysis";
    public static final String JSON_QUESTION_CONTENT="Content";
    public static final String JSON_QUESTION_TYPE="QuestionType";
    public static final String JSON_QUESTION_OPTIONS="Options";
    public static final String JSON_QUESTION_ANSWER="Answers";

    /**
     *option地址
     */
    public static final String JSON_OPTION_CONTENT="Content";
    public static final String JSON_OPTION_LABEL="Label";
    public static final String JSON_OPTION_API_ID="Id";
    public static final String JSON_OPTION_OPTION_ID="OptionId";



}


