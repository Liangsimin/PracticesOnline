package net.lzzy.practicesonline.models.View;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by lzzy_gxy on 2019/5/8.
 * Description:
 */
public class QuestionResult implements Parcelable {
    private UUID questionId;
    private  boolean isRight;

    public QuestionResult(Parcel in) {
        isRight = in.readByte() !=0;
        questionId = (UUID) in.readSerializable();
        type= WrongType.getInstance(in.readInt());
    }

    public QuestionResult() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeByte((byte)(isRight ? 1 : 0));
        dest.writeSerializable(questionId);
        dest.writeInt(type.ordinal());
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }

    public WrongType getWrongType() {
        return type;
    }

    public void setType(WrongType type) {
        this.type = type;
    }

    private  WrongType type;

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<QuestionResult> CREATOR =new Creator<QuestionResult>() {
        @Override
        public QuestionResult createFromParcel(Parcel in) {
            return new QuestionResult(in);
        }

        @Override
        public QuestionResult[] newArray(int size) {
            return new QuestionResult[size];
        }

    };



}
