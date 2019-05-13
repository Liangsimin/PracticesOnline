package net.lzzy.practicesonline.network;

import net.lzzy.practicesonline.constants.ApiConstants;
import net.lzzy.practicesonline.models.Practice;
import net.lzzy.practicesonline.models.View.PracticeResult;
import net.lzzy.sqllib.JsonConverter;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author lzzy_gxy
 * @date 2019/4/22
 * Description:
 */
public class PracticeService {


    public static String getPracticesFromServer() throws IOException {
        return ApiService.okGet(ApiConstants.URL_PRACTICES);
    }

    public static List<Practice> getPractices(String json) throws Exception {
        JsonConverter<Practice> converter=new JsonConverter<>(Practice.class);
        return converter.getArray(json);
    }
    public static int postResult(PracticeResult praticeResult) throws JSONException, IOException {
        return ApiService.okPost(ApiConstants.URL_RESULT,praticeResult.toJson());
    }


}
