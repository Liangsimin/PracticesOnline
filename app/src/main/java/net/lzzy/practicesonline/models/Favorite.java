package net.lzzy.practicesonline.models;

import net.lzzy.sqllib.Ignored;
import net.lzzy.sqllib.Sqlitable;

import java.util.UUID;

public class Favorite extends BaseEntity implements Sqlitable {
    @Ignored
    public static final String COl_QUESTION_ID="questionId";
    private UUID questionId;

    public Favorite() {
    }

    public Favorite(UUID questionId) {
        this.questionId = questionId;

    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    @Override
    public boolean needUpdate() {
        return false;
    }
}
