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

    private static String jsonString = "{\n" +
            "  \"questiondetails\": [\n" +
            "    {\n" +
            "      \"questionId\": \"\\\"2ew4y45rdfbv\\\"\",\n" +
            "      \"status\": 1,\n" +
            "      \"question2\": \"Blood Pressure\",\n" +
            "      \"question\": \"\\\"Have u checked ur eye within 2 year?\\\"\"\n" +
            "    },\n" +
            "    \n" +
            "      {\n" +
            "        \"questionId\": \"\\\"ew4356we\\\"\",\n" +
            "        \"status\": 0,\n" +
            "        \"question2\": \"Blood Pressure\",\n" +
            "        \"question\": \"\\\"Have you checked your blood pressure?\\\"\"\n" +
            "      },\n" +
            "    \n" +
            "    {\n" +
            "      \"questionId\": \"\\\"2ew4y45rdfbv\\\"\",\n" +
            "      \"status\": 1,\n" +
            "       \"question2\": \"Blood Pressure\",\n" +
            "      \"question\": \"\\\"Have u checked ur eye within 2 year?\\\"\"\n" +
            "    },\n" +
            "    \n" +
            "    {\n" +
            "      \"questionId\": \"\\\"2ew4y45rdfbv\\\"\",\n" +
            "      \"status\": 1,\n" +
            "      \"question2\": \"Blood Pressure\",\n" +
            "      \"question\": \"\\\"Have u checked ur eye Hair 2 year?\\\"\"\n" +
            "    },\n" +
            "    \n" +
            "    {\n" +
            "      \"questionId\": \"\\\"2ew4y45rdfbv\\\"\",\n" +
            "      \"status\": 1,\n" +
            "      \"question2\": \"Blood Pressure\",\n" +
            "      \"question\": \"\\\"Have u checked ur Stomach within 2 year?\\\"\"\n" +
            "    },\n" +
            "    \n" +
            "    {\n" +
            "      \"questionId\": \"\\\"2ew4y45rdfbv\\\"\",\n" +
            "      \"status\": 1,\n" +
            "      \"question2\": \"Blood Pressure\",\n" +
            "      \"question\": \"\\\"Have u checked ur eye within 2 year?\\\"\"\n" +
            "    },\n" +
            "    \n" +
            "    \n" +
            "    {\n" +
            "      \"questionId\": \"\\\"2ew4y45rdfbv\\\"\",\n" +
            "      \"status\": 1,\n" +
            "      \"question2\": \"Blood Pressure\",\n" +
            "      \"question\": \"\\\"Have u checked ur Hair within 2 year?\\\"\"\n" +
            "    },\n" +
            "    \n" +
            "    \n" +
            "    {\n" +
            "      \"questionId\": \"\\\"2ew4y45rdfbv\\\"\",\n" +
            "      \"status\": 0,\n" +
            "       \"question2\": \"Blood Pressure\",\n" +
            "      \"question\": \"\\\"Have u checked ur Ear within 2 year?\\\"\"\n" +
            "    }\n" +
            " ]\n" +
            "}";

    public static void paseData(JSONObject jsonObject) {
        try {
            jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.optJSONArray("questiondetails");
            mQuestionDetailsListStatus1.clear();
            mQuestionDetailsListStatus0.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                QuestionDetail questionDetail = new QuestionDetail();
                JSONObject innerJsonObject = jsonArray.optJSONObject(i);
                questionDetail.setQuestionId(innerJsonObject.optString("questionId"));
                questionDetail.setStatus(innerJsonObject.optInt("status"));
                questionDetail.setQuestionText(innerJsonObject.optString("question"));
                if (questionDetail.getStatus() == 1) {
                    mPageCount++;
                    mQuestionDetailsListStatus1.add(questionDetail);
                } else {
                    mQuestionDetailsListStatus0.add(questionDetail);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static class QuestionDetail {

        private String questionId;
        private int status;
        private String questionText;

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getQuestionText() {
            return questionText;
        }

        public void setQuestionText(String questionText) {
            this.questionText = questionText;
        }
    }
}
