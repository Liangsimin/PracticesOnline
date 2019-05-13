package net.lzzy.practicesonline.models;

/**
 * Created by lzzy_gxy on 2019/4/16.
 * Description:
 */
public enum QuestionType {
    /**
     * 题目类型

     */
    SINGLE_CHOICE("单项选择"),MULTI_CHOICE("不定向选择"),JUDGE("判断");
    private String name;
    QuestionType(String name){
        this.name=name;
    }
    @Override
    public String toString(){
        return name;
    }
    public QuestionType getQuestiontype(int val){
        return null;
    }
    public static QuestionType getInstance(int ordinal){
        for (QuestionType type: QuestionType.values()){
            if (type.ordinal()==ordinal){
                return type;
            }
        }
        return null;
    }
}
