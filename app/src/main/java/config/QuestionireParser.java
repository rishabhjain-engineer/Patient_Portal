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

    public static void paseData(String stringResponse) {
        //jsonObject = new JSONObject(jsonString);
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
