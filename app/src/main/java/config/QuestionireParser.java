package config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayaz on 31/1/17.
 */

public class QuestionireParser {

    private static List<QuestionDetail> mQuestionDetailsListStatus1 = new ArrayList<QuestionDetail>();
    private static List<QuestionDetail> mQuestionDetailsListStatus0 = new ArrayList<QuestionDetail>();
    public static int mPageCount = 0;

    public static List<QuestionDetail> getQuestionDetailListStatus1() {
        return mQuestionDetailsListStatus1;
    }
    public static List<QuestionDetail> getQuestionDetailListStatus0() {
        return mQuestionDetailsListStatus0;
    }

    public static void paseData(String stringResponse) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(stringResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mQuestionDetailsListStatus1.clear();
        mQuestionDetailsListStatus0.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            QuestionDetail questionDetail = new QuestionDetail();
            JSONObject innerJsonObject = jsonArray.optJSONObject(i);
            questionDetail.setQuestion(innerJsonObject.optString("Question"));
            questionDetail.setQuestion2(innerJsonObject.optString("Question2"));
            questionDetail.setStatusValue(innerJsonObject.optString("Value"));
            if (questionDetail.getStatusValue().equalsIgnoreCase("True")) {
                mPageCount++;
                mQuestionDetailsListStatus1.add(questionDetail);
            } else {
                mQuestionDetailsListStatus0.add(questionDetail);
            }
        }
    }


    public static class QuestionDetail {

        private String question;
        private String Question2;
        private String statusValue;


        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getQuestion2() {
            return Question2;
        }

        public void setQuestion2(String question2) {
            Question2 = question2;
        }

        public String getStatusValue() {
            return statusValue;
        }

        public void setStatusValue(String statusValue) {
            this.statusValue = statusValue;
        }
    }
}
