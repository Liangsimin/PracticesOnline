package net.lzzy.practicesonline.models;

import net.lzzy.sqllib.Sqlitable;

import java.util.UUID;

/**
 * Created by lzzy_gxy on 2019/4/16.
 * Description:
 */
public class Favorite extends BaseEntity implements Sqlitable {
    public static final String COL_QUESTION_ID="colQuestionId";
    private UUID questionId;
    private int times;
    private boolean isDone;

    public UUID getQuestionId() {
        return questionId;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }



    @Override
    public boolean needUpdate() {
        return false;
    }
}
