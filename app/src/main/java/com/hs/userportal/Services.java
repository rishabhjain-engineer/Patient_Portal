package com.hs.userportal;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import config.StaticHolder;
import utils.PreferenceHelper;

public class Services {
    /* Variables declaration */

    private HttpClient client;
    private HttpPost request;
    private HttpResponse response;
    private JSONObject receiveData, temp, titleJSON;
    private JSONArray values;
    private String url, line;
    private static Header[] cookies;
    private BufferedReader reader;
    private StringBuilder sb;
    private Context context;
    private static String checknet = "no";

    public static String hoja = "";
	/*static String init = "https://l141702.cloudchowk.com";*/

	/*static String init ="https://l141702.cloudchowk.com/";*///"http://192.168.1.202:81";//"https://l141702.cloudchowk.com/";//"http://192.168.1.202:81";// "https://l141702.cloudchowk.com/";////;
    // //////
    // static String init = "http://192.168.1.56";

    public Services(Context context) {
        client = new DefaultHttpClient();
        this.context = context;
    }

    public JSONObject LogIn(JSONObject sendData, String url) {// {"browserType":"4.4.2","UserName":"dheer","applicationType":"Mobile","rememberMe":"false","Password":"dheer@123"}

        request = new HttpPost(url);
        request.setHeader("Content-type", "application/json");
        request.setHeader("Accept", "application/json");

        try {
            request.setEntity(new StringEntity(sendData.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            response = getThreadSafeClient().execute(request);
            String result = response.toString();
            cookies = response.getHeaders("Set-Cookie");
            String cookieData = "";
            for (int i = 0; i < cookies.length; i++) {
                cookieData += cookies[i].getValue() + ";"; // ASP.NET_SessionId=spghlyjtkcymxiw5d0pzm3u0;
            }
            hoja = cookieData;
            LocationClass.cook = hoja;
            FileOutputStream outputStream;
            outputStream = context.openFileOutput("Cookie-Data", Context.MODE_PRIVATE);
            outputStream.write(cookieData.getBytes());
            outputStream.close();

            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            receiveData = new JSONObject(new String(sb));

        } catch (ClientProtocolException e) {
            Log.e("Client", "Error checking internet connection", e);
        } catch (IOException e) {
            Log.e("IO", "Error checking internet connection", e);
        } catch (JSONException e) {
            Log.e("JSON", "Error checking internet connection", e);
        }

        return receiveData;
    }

    public JSONObject GetCredentialDetails(JSONObject sendData) {

		/*url = init + "/CredentialsModule/CredentialService.asmx/GetCredentialDetails";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetCredentialDetails);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }// https://l141702.cloudchowk.com/CredentialsModule/CredentialService.asmx/GetCredentialDetails

    public JSONObject PatientDisclaimer(JSONObject sendData) {

		/*url = init + "/CredentialsModule/CredentialService.asmx/PatientDisclaimer";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.PatientDisclaimer);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject GetLatestVersionInfo(JSONObject sendData) {
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetLatestVersionInfo);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;
    }

    public JSONObject IsUserAuthenticated(JSONObject sendData) {
       /* StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.AuthenticateUserSession);
        String url = sttc_holdr.request_Url();
        JSONObject jsonObjectToSend = new JSONObject();
        PreferenceHelper preferenceHelper = PreferenceHelper.getInstance();
        try {
            jsonObjectToSend.put("SessionId", preferenceHelper.getString(PreferenceHelper.PreferenceKey.SESSION_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("d", "true");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //JSONObject jsonObject = common(sendData, url);
        return jsonObject;
    }

    public JSONObject AgreeService(JSONObject sendData) {

		/*url = init + "/StaffModule/StaffService.asmx/agreeTermsCondition";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.agreeTermsCondition);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject LogOut(JSONObject sendData) {

		/*url = init + "/CredentialsModule/CredentialService.asmx/LogOutIOS";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.LogOutIOS);
        String url = sttc_holdr.request_Url();
        request = new HttpPost(url);
        request.setHeader("Content-type", "application/json");
        request.setHeader("Accept", "application/json");

        try {
            request.setEntity(new StringEntity(sendData.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {

            response = getThreadSafeClient().execute(request);
            cookies = response.getHeaders("Set-Cookie");

            String cookieData = "";

            for (int i = 0; i < cookies.length; i++) {
                cookieData += cookies[i].getValue() + ";";
            }

            FileOutputStream outputStream;
            outputStream = context.openFileOutput("Cookie-Data", Context.MODE_PRIVATE);
            outputStream.write(cookieData.getBytes());
            outputStream.close();

            HttpEntity entity = response.getEntity();
            reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            receiveData = new JSONObject(new String(sb));

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return receiveData;

    }

    // GETTING INFORMATION FROM AREA!!!!!!!!!!!!!!!!!!!!!!!!

    public JSONObject fromarea(JSONObject sendData) {
		/*url = init + "/SupplierModule/SupplierMasterService.asmx/GetAutoAreaSearch";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetAutoAreaSearch);
        String url = sttc_holdr.request_Url();
        request = new HttpPost(url);
        request.setHeader("Content-type", "application/json");
        request.setHeader("Accept", "application/json");
        request.setHeader("Cookie", hoja);

        try {
            request.setEntity(new StringEntity(sendData.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {

            // URL myUrl = new URL(url);
            // URLConnection connection = myUrl.openConnection();
            // connection.setConnectTimeout(5000);
            // connection.connect();
            response = getThreadSafeClient().execute(request);

            cookies = response.getHeaders("Set-Cookie");

            String cookieData = "";

            for (int i = 0; i < cookies.length; i++) {
                cookieData += cookies[i].getValue() + ";";
            }

            FileOutputStream outputStream;
            outputStream = context.openFileOutput("Cookie-Data", Context.MODE_PRIVATE);
            outputStream.write(cookieData.getBytes());
            outputStream.close();

            HttpEntity entity = response.getEntity();
            reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            receiveData = new JSONObject(new String(sb));

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return receiveData;

    }

    // To Check For User name!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    public JSONObject useralias(JSONObject sendData) {

		/*url = init + "/CommonMasterModule/CommonMasterService.asmx/checkAliasExist";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.checkAliasExist);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject IsUserNameAliasExists(JSONObject sendData) {

		/*url = init + "/CommonMasterModule/CommonMasterService.asmx/checkAliasExist";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.IsUserNameAliasExists);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }
    // Common Method Called!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    public static DefaultHttpClient getThreadSafeClient() {
        DefaultHttpClient client = new DefaultHttpClient();
        ClientConnectionManager mgr = client.getConnectionManager();
        HttpParams params = client.getParams();
        client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
        return client;
    }

    public JSONObject common(JSONObject sendData, String url) {
        // System.out.println("hahahahahahahahahahaha");
        Log.e("Url: ", url);
        request = new HttpPost(url);
        request.setHeader("Content-type", "application/json");
        request.setHeader("Accept", "application/json");
        request.setHeader("Cookie", hoja);

        try {
            request.setEntity(new StringEntity(sendData.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {

            // URL myUrl = new URL(url);
            // URLConnection connection = myUrl.openConnection();
            // connection.setConnectTimeout(3000);
            // connection.connect();

            // response = getThreadSafeClient().execute(request);
            response = getThreadSafeClient().execute(request);
            cookies = response.getHeaders("Set-Cookie");

            String cookieData = "";

            for (int i = 0; i < cookies.length; i++) {
                cookieData += cookies[i].getValue() + ";";
            }

            FileOutputStream outputStream;
            outputStream = context.openFileOutput("Cookie-Data", Context.MODE_PRIVATE);
            outputStream.write(cookieData.getBytes());
            outputStream.close();
            HttpEntity entity = response.getEntity();
            reader = new BufferedReader(new InputStreamReader(entity.getContent()));

            sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            receiveData = new JSONObject(new String(sb));

        } catch (ClientProtocolException e) {
            checknet = "yes";
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            checknet = "yes";
        } catch (JSONException e) {
            checknet = "yes";
            e.printStackTrace();
        } catch (Exception e) {
            checknet = "yes";
            e.printStackTrace();
        }

        return receiveData;

    }

    public String common1(JSONObject sendData, String url) {
        // System.out.println("hahahahahahahahahahaha");
        request = new HttpPost(url);
        request.setHeader("Content-type", "application/json");
        request.setHeader("Accept", "application/json");
        request.setHeader("Cookie", hoja);
        try {
            request.setEntity(new StringEntity(sendData.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {

            // URL myUrl = new URL(url);
            // URLConnection connection = myUrl.openConnection();
            // connection.setConnectTimeout(3000);
            // connection.connect();
            // response = getThreadSafeClient().execute(request);

            response = getThreadSafeClient().execute(request);
            cookies = response.getHeaders("Set-Cookie");

            String cookieData = "";

            for (int i = 0; i < cookies.length; i++) {
                cookieData += cookies[i].getValue() + ";";
            }

            FileOutputStream outputStream;
            outputStream = context.openFileOutput("Cookie-Data", Context.MODE_PRIVATE);
            outputStream.write(cookieData.getBytes());
            outputStream.close();
            HttpEntity entity = response.getEntity();
            reader = new BufferedReader(new InputStreamReader(entity.getContent()));

            sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

        } catch (ClientProtocolException e) {
            checknet = "yes";
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            checknet = "yes";
        }

        return sb.toString();

    }

    public JSONObject checkemail(JSONObject sendData) {

		/*url = init + "/PatientModule/PatientService.asmx/CheckEmailIdIsExist";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.CheckEmailIdIsExistMobile);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject submit(JSONObject sendData) {

		/*url = init + "/PatientModule/PatientService.asmx/SignUpPatient";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.SignUpPatient);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject patienthistory(JSONObject sendData) {

		/*url = init + "/PatientModule/PatientService.asmx/GetPatientHistory";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetPatientHistory);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject changepassword(JSONObject sendData) {

		/*url = init + "/CredentialsModule/CredentialService.asmx/ChangePasswordIOS";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.ChangePasswordIOS);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject otherarea(JSONObject sendData) {

		/*url = init + "/CommonMasterModule/UIService.asmx/GetOthersFromArea";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetOthersFromArea);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject vaccine(JSONObject sendData) {

		/*url = init + "/PatientModule/PatientService.asmx/GetAllVaccines";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetAllVaccines);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }//

    public JSONObject action_member(JSONObject sendData) {

		/*url = init + "/PatientModule/PatientService.asmx/GetPatientAlertList";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.AcceptRequest);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject allergy(JSONObject sendData) {

		/*url = init + "/PatientModule/PatientService.asmx/GetPatientAlertList";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetPatientAlertList);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject register(JSONObject sendData) {

	/*	url = init + "/laboratorymodule/LISService.asmx/Register";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.Register);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject patientinvestigation(JSONObject sendData) {

		/*url = init + "/LaboratoryModule/LISService.asmx/GetAdvisedInvestigationDetailMobile";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetAdvisedInvestigationDetailMobile);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject patientstatus(JSONObject sendData) {

		/*url = init + "/PatientModule/PatientService.asmx/GetAllLisPatientCaseDetailMobile";*/

        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetAllLisPatientCaseDetailMobileNew);
        String url = sttc_holdr.request_Url();
        request = new HttpPost(url);
        request.setHeader("Content-type", "application/json");
        request.setHeader("Accept", "application/json");
        request.setHeader("Cookie", hoja);

        try {
            request.setEntity(new StringEntity(sendData.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            response = getThreadSafeClient().execute(request);

            cookies = response.getHeaders("Set-Cookie");

            String cookieData = "";

            for (int i = 0; i < cookies.length; i++) {
                cookieData += cookies[i].getValue() + ";";
            }

            FileOutputStream outputStream;
            outputStream = context.openFileOutput("Cookie-Data", Context.MODE_PRIVATE);
            outputStream.write(cookieData.getBytes());
            outputStream.close();

            HttpEntity entity = response.getEntity();
            reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            receiveData = new JSONObject(new String(sb));

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return receiveData;

    }

    public JSONObject getpatient(JSONObject sendData) {

		/*url = init + "/PatientModule/PatientBedAssignmentService.asmx/GetPatient";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetPatient);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    // public JSONObject getTestReportPDF(JSONObject sendData) {
    //
    // url = init+"/LaboratoryModule/LISService.asmx/GetpatienttestReport";
    // request = new HttpPost(url);
    // request.setHeader("Content-type", "application/json");
    // request.setHeader("Accept", "application/octet-stream");
    // request.setHeader("Cookie", hoja);
    //
    // try {
    // request.setEntity(new StringEntity(sendData.toString(), "UTF-8"));
    // } catch (UnsupportedEncodingException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // //System.out.println("1");
    // try {
    // response = getThreadSafeClient().execute(request);
    //
    // cookies = response.getHeaders("Set-Cookie");
    //
    // String cookieData = "";
    //
    // for (int i = 0; i < cookies.length; i++) {
    // cookieData += cookies[i].getValue() + ";";
    // }
    // // System.out.println("2");
    //
    // FileOutputStream outputStream;
    // outputStream = context.openFileOutput("Cookie-Data",
    // Context.MODE_PRIVATE);
    // outputStream.write(cookieData.getBytes());
    // outputStream.close();
    //
    // reader = new BufferedReader(new InputStreamReader(response
    // .getEntity().getContent()));
    // sb = new StringBuilder();
    // while ((line = reader.readLine()) != null) {
    // sb.append(line + "\n");
    // }
    //
    // System.out.println(sb);
    // System.out.println("3");
    // receiveData = new JSONObject(new String(sb));
    // Log.i("REPORT DETAILS", receiveData.toString());
    // } catch (ClientProtocolException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (JSONException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    //
    // return receiveData;
    // }
    String pdf;

    public String getTestReportPDF(JSONObject sendData) {

		/*url = init + "/LaboratoryModule/LISService.asmx/GetpatienttestReport";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetpatienttestReport);
        String url = sttc_holdr.request_Url();
        request = new HttpPost(url);
        request.setHeader("Content-type", "application/json");
        request.setHeader("Accept", "application/json");
        request.setHeader("Cookie", hoja);
        String cookieData = "";

        for (int i = 0; i < cookies.length; i++) {
            cookieData += cookies[i].getValue() + ";";
        }

        request.addHeader("Cookie", cookieData);

        try {
            request.setEntity(new StringEntity(sendData.toString(), "UTF-8"));
            response = getThreadSafeClient().execute(request);

            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

            sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            pdf = sb.toString();

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pdf;
    }

    public JSONObject forgotpassword(JSONObject sendData) {

		/*url = init + "/CredentialsModule/CredentialService.asmx/ForgotPassword";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.ForgotPassword);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject findhelp(JSONObject sendData) {

		/*url = init + "/CredentialsModule/CredentialService.asmx/NeedHelp";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.NeedHelp);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject patienttestdetails(JSONObject sendData) {

		/*url = init + "/LaboratoryModule/LISInvestigationWorklistService.asmx/GetAllCompletedTestDetailsOfPatient";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetAllCompletedTestDetailsOfPatient);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONArray graphreport(JSONObject sendData) {
		/*url = init + "/PatientModule/PatientService.asmx/GetPatientTestRangeDataMobile";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetPatientTestRangeDataMobile);
        String url = sttc_holdr.request_Url();
        request = new HttpPost(url);
        request.setHeader("Content-type", "application/json");
        request.setHeader("Accept", "application/json");
        request.setHeader("Cookie", hoja);

        String cookieData = "";

        for (int i = 0; i < cookies.length; i++) {
            cookieData += cookies[i].getValue() + ";";
        }

        request.addHeader("Cookie", cookieData);

        try {
            request.setEntity(new StringEntity(sendData.toString(), "UTF-8"));
            response = getThreadSafeClient().execute(request);

            HttpEntity entity = response.getEntity();
            reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            receiveData = new JSONObject(new String(sb));

            // Log.i("TEST DETAILS", receiveData.toString());

            String p = receiveData.get("d").toString();
            temp = new JSONObject(p);
            values = temp.getJSONArray("Table");

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return values;

    }

    public JSONObject verify(JSONObject sendData) {

		/*url = init + "/PatientModule/PatientService.asmx/GetPatientVerification";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetPatientVerification);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject verifyemail(JSONObject sendData) {

		/*url = init + "/PatientModule/PatientService.asmx/EmailVerificationClinic";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.EmailVerificationClinic);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject verifyresendsms(JSONObject sendData) {

		/*url = init + "/PatientModule/PatientService.asmx/ResendSmsVerifyCodeClinic";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.ResendSmsVerifyCodeClinic);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject verifysms(JSONObject sendData) {

		/*url = init + "/PatientModule/PatientService.asmx/CheckVerifyCodeClinic";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.CheckVerifyCodeClinic);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject citylist(JSONObject sendData) {

	/*	url = init + "/CommonMasterModule/CommonMasterService.asmx/GetCityList1";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetCityList1);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject citylist1(JSONObject sendData) {

		/*url = init + "/CommonMasterModule/UIService.asmx/GetCityList";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetCityList);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject statefromcity(JSONObject sendData) {

	/*	url = init + "/CommonMasterModule/UIService.asmx/GetStateFromCity";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetStateFromCity);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

	/*public JSONObject countrylist(JSONObject sendData) {

	*//*	url = init + "/CommonMasterModule/UIService.asmx/GetCountryList";*//*
		StaticHolder sttc_holdr=new StaticHolder(StaticHolder.Services_static.GetCountryList);
		String url=sttc_holdr.request_Url();
		JSONObject abc = common(sendData, url);
		return abc;

	}*/

    public JSONObject statelist(JSONObject sendData) {

		/*url = init + "/CommonMasterModule/UIService.asmx/GetStateList";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetStateList);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject pdfreport(JSONObject sendData) {

		/*url = init + "/LaboratoryModule/lisservice.asmx/GetPatientTestReportMobile";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetPatientTestReportMobile);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject getnation(JSONObject sendData) {

		/*url = init + "/CommonMasterModule/UIService.asmx/GetAllNationality";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetAllNationality);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject fblink(JSONObject sendData) {

		/*url = init + "/CredentialsModule/CredentialService.asmx/FacebookLinked";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.FacebookLinked);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject saveBasicDetail(JSONObject sendData) {

 /* url = init + "/laboratorymodule/LISService.asmx/Register";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.saveBasicDetail);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject fblogin(JSONObject sendData) {

		/*url = init + "/CredentialsModule/CredentialService.asmx/FacebookLoginMobile";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.FacebookLoginMobile);
        String url = sttc_holdr.request_Url();
        request = new HttpPost(url);
        request.setHeader("Content-type", "application/json");
        request.setHeader("Accept", "application/json");

        try {
            request.setEntity(new StringEntity(sendData.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {

            // URL myUrl = new URL(url);
            // URLConnection connection = myUrl.openConnection();
            // connection.setConnectTimeout(5000);
            // connection.connect();
            response = getThreadSafeClient().execute(request);

            cookies = response.getHeaders("Set-Cookie");

			/* **** SAVING COOKIE DATA ON PHONE STORAGE *** */
            String cookieData = "";

            for (int i = 0; i < cookies.length; i++) {
                cookieData += cookies[i].getValue() + ";";

            }
            hoja = cookieData;

            FileOutputStream outputStream;
            outputStream = context.openFileOutput("Cookie-Data", Context.MODE_PRIVATE);
            outputStream.write(cookieData.getBytes());
            outputStream.close();

            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            receiveData = new JSONObject(new String(sb));

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            Log.e("Client", "Error checking internet connection", e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("IO", "Error checking internet connection", e);
        } catch (JSONException e) {
            // TODO Auto-generated catch block

            Log.e("JSON", "Error checking internet connection", e);
        }

        return receiveData;

    }

    public JSONObject fbunlink(JSONObject sendData) {

		/*url = init + "/CredentialsModule/CredentialService.asmx/FacebookUnLinked";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.FacebookUnLinked);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject deleteSingularDetails(JSONObject sendData) {

  /*url = init + "/PatientModule/PatientService.asmx/GetPatientHistory";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.deleteSingularDetails);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject saveHealthDetail(JSONObject sendData) {
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.saveHealthDetail);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject countrylist(JSONObject sendData) {


        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetCountryList);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject nationalityList(JSONObject sendData) {


        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.getNationality);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject getAllergies(JSONObject sendData) {

  /*url = init + "/PatientModule/PatientService.asmx/GetPatientHistory";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.getAllergies);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject GetUserDetails(JSONObject sendData) {

	/*	url = init + "/CredentialsModule/CredentialService.asmx/GetUserDeatils";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetUserDeatils);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject ViewImages(JSONObject sendData) {

		/*url = init + "/LaboratoryModule/LISService.asmx/GetPatientTestImagesInCase";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetPatientTestImagesInCase);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject UpdateImage(JSONObject sendData) {

		/*url = init + "/laboratorymodule/LISService.asmx/UpdateImage";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.UpdateImage);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public byte[] pdf(JSONObject sendData, String actName) {
       /* if(actName.equalsIgnoreCase("ReportRecords")) {
            ReportRecords.progress_bar.setProgress(7);
            ReportRecords.progress_bar.setSecondaryProgress(10);
        }*/
        /*if(actName.equalsIgnoreCase("Report Status")) {
            ReportStatus.progress_bar.setProgress(11);
            ReportStatus.progress_bar.setSecondaryProgress(13);
        }*/
        byte[] fileContents = null;
        //url =  "https://l141702.cloudchowk.com/LaboratoryModule/LISService.asmx/GetpatienttestReportAndroid";
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetpatienttestReportAndroid);
        String url = sttc_holdr.request_Url();
        //String url = "https://patient.cloudchowk.com:8081/WebServices/HTMLReports.asmx/GetpatienttestReportHTMLAndroid";
        request = new HttpPost(url);
        request.setHeader("Content-type", "application/json");
        request.setHeader("Accept", "application/octet-stream");
        request.setHeader("Cookie", hoja);
        String cookieData = "";

        for (int i = 0; i < cookies.length; i++) {
            cookieData += cookies[i].getValue() + ";";
        }

        request.addHeader("Cookie", cookieData);

        try {
            request.setEntity(new StringEntity(sendData.toString(), "UTF-8"));
            response = client.execute(request);

            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            receiveData = new JSONObject(new String(sb));
            Log.i("REPORT DETAILS", receiveData.toString());

            String p = receiveData.get("d").toString();
            String[] byteValues = p.substring(1, p.length() - 1).split(",");
            byte[] bytes = new byte[byteValues.length];
            Log.i("byteValues", byteValues.toString());
           /* if(actName.equalsIgnoreCase("ReportRecords")) {
                ReportRecords.progress_bar.setProgress(11);
                ReportRecords.progress_bar.setSecondaryProgress(13);
            }*/
            /*if(actName.equalsIgnoreCase("Report Status")) {
                ReportStatus.progress_bar.setProgress(14);
                ReportStatus.progress_bar.setSecondaryProgress(14);
            }*/
            for (int i = 0, len = bytes.length; i < len; i++) {
                bytes[i] = (byte) Integer.valueOf(byteValues[i].trim()).byteValue();
            }

            Log.i("adksa", p);

            fileContents = bytes;
            Log.v("contents!!", fileContents.toString());
           /* if(actName.equalsIgnoreCase("ReportRecords")) {
                ReportRecords.progress_bar.setProgress(14);
                ReportRecords.progress_bar.setSecondaryProgress(14);
            }*/
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NumberFormatException ex) {
            if(ReportStatus.progress != null){
                ReportStatus.progress.dismiss();
            }
            ReportStatus.progress = null;
        }
       /* if(actName.equalsIgnoreCase("Report Status")) {
            ReportStatus.progress_bar.setProgress(15);
            ReportStatus.progress_bar.setSecondaryProgress(15);
        }*/
        return fileContents;
    }

    public JSONObject uploadUmage(JSONObject sendData) {

	/*	url = init + "/PatientModule/PatientService.asmx/PatientFileVault";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.PatientFileVault);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject patienBasicDetails(JSONObject sendData) {

  /*url = init + "/PatientModule/PatientService.asmx/GetPatientHistory";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.getBasicDetails);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject getpatientHistoryDetails(JSONObject sendData) {

  /*url = init + "/PatientModule/PatientService.asmx/GetPatientHistory";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.getpatientHistoryDetails);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject saveOtherDetail(JSONObject sendData) {

  /*url = init + "/PatientModule/PatientService.asmx/GetPatientHistory";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.saveOtherDetail);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject GetUserDisclaimer(JSONObject sendData) {

		/*url = init + "/StaffModule/StaffService.asmx/GetUserDisclaimer";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetUserDisclaimer);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject GetPackage_TestDetails(JSONObject sendData) {

		/*url = init + "/StaffModule/StaffService.asmx/GetUserDisclaimer";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.SinglePackageDetails);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject GetUserDetailsFromContactNoMobileService(JSONObject sendData) {

		/*url = init + "/CommonMasterModule/CommonMasterService.asmx/GetUserDetailsFromContactNoMobileService";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetUserDetailsFromContactNoMobileService);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

	/*public JSONObject SignUpPatient(JSONObject sendData) {

		*//*url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/SignUpByPatient";*//*
		JSONObject abc = common(sendData, StaticHolder.SIGNUP);
		return abc;

	}*/

    public JSONObject getLabfromTest(JSONObject sendData) {

		/*url = LocationClass.init + "/GetLabByTest";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetLabByTest);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject sendContactToServer(JSONObject sendData) {
        // TODO Auto-generated method stub
		/* url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/UpdateContact";*/
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.UpdateContact);
        String url = sttc_holdr.request_Url();
        JSONObject jsonobj = common(sendData, url);
        return jsonobj;
    }

    public JSONObject patientbussinessModel(JSONObject sendData) {
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.patientbussinessModel);
        String url = sttc_holdr.request_Url();
        JSONObject receivedJsonObj = common(sendData, url);
        return receivedJsonObj;
    }


    public JSONObject getQuizData(JSONObject sendData) {
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetQuizData);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;

    }

    public JSONObject NewLogInApi(JSONObject sendData) {
        StaticHolder staticHolder = new StaticHolder(StaticHolder.Services_static.NewLogIn);
        String url = staticHolder.request_Url();
        JSONObject jsonObjectResponse = LogIn(sendData, url);
        return jsonObjectResponse;

    }

    public JSONObject LogInUser_facebook(JSONObject sendData) {
        StaticHolder staticHolder = new StaticHolder(StaticHolder.Services_static.LogInUser_facebook);
        String url = staticHolder.request_Url();
        JSONObject jsonObjectResponse = LogIn(sendData, url);
        return jsonObjectResponse;

    }

    public JSONObject getUserGrade(JSONObject sendData) {
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetUserGrade);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;
    }

    public JSONObject getUserFact(JSONObject sendData) {
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetUserFact);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;
    }

    public JSONObject saveUserDevice(JSONObject sendData) {
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.SaveUserDevice);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;
    }

    public JSONObject getSchoolDoctorList(JSONObject sendData) {
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetSchoolDoctorList);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;
    }

    public JSONObject getSchoolStudentDetails(JSONObject sendData) {
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.getSchoolStudentDetails);
        String url = sttc_holdr.request_Url();
        JSONObject abc = common(sendData, url);
        return abc;
    }

}
