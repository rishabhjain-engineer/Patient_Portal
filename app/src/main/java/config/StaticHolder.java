package config;

import android.app.Activity;

import com.android.volley.RequestQueue;
import com.hs.userportal.Services;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class StaticHolder {


    //public final String BASE_URL1 = "https://api.healthscion.com/"; //TODO commented by ayaz LIVE
    //public final String BASE_URL = "https://api.healthscion.com/WebServices/LabService.asmx/"; //TODO commented by ayaz LIVE


    //public final String BASE_URL = "http://192.168.1.11/WebServices/LabService.asmx/"; //TODO opened by ayaz LOCAL
    //public final String BASE_URL1 = "http://192.168.1.11/";//TODO opened by ayaz LOCAL

    public final String BASE_URL = "https://apidemo.healthscion.com/WebServices/LabService.asmx/"; //TODO opened by ayaz Demo
    public final String BASE_URL1 = "https://apidemo.healthscion.com/";//TODO opened by ayaz Demo


    public static ArrayList<HashMap<String, String>> allPackageslist = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> finalOrderedListAlways = new ArrayList<HashMap<String, String>>();

    Services_static serviceName;
    Activity context;
    JSONObject sendData;
    String[] url_parts = {
            "PatientModule/PatientService.asmx/", "WebServices/CredentialsService.asmx/", "StaffModule/StaffService.asmx", "SupplierModule/SupplierMasterService.asmx/",
            "CommonMasterModule/CommonMasterService.asmx/", "CommonMasterModule/UIService.asmx", "laboratorymodule/LISService.asmx/", "PatientModule/PatientBedAssignmentService.asmx/",
            "CredentialsModule/CredentialService.asmx/", "LaboratoryModule/LISInvestigationWorklistService.asmx/"};

    public StaticHolder(Activity activity, Services_static serviceName, JSONObject sendData) {
        this.serviceName = serviceName;
        this.context = activity;
        this.sendData = sendData;
    }

    public StaticHolder(Activity activity, Services_static serviceName) {
        this.serviceName = serviceName;
        this.context = activity;
    }

    public JSONObject request_services() {
        Services service = new Services(context);
        JSONObject receiveData = null;
        switch (serviceName) {
            case LogIn:
                System.out.println("login.");
                receiveData = service.LogIn(sendData, BASE_URL1 + url_parts[1] + "LogIn");
                break;

            case SignUpByPatient:
                receiveData = service.common(sendData, BASE_URL + "SignUpByPatient");
                break;


            default:
                System.out.println("Google - biggest search giant.. ATT - my carrier provider..");
                break;
        }
        return receiveData;
    }


    public StaticHolder(Services_static serviceName) {
        this.serviceName = serviceName;
    }

    public String request_Url() {
        String url = null;
        switch (serviceName) {

            case CheckContactNoExist:
                //url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/CheckContactNoExist";
                url = BASE_URL + "CheckContactNoExist";
                break;

            case CheckDupUserName:
                //url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/CheckDupUserName";
                url = BASE_URL + "CheckDupUserName";
                break;

            case NewSignUpByPatientMod:
                //url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/NewSignUpByPatient";
                url = BASE_URL + "NewSignUpByPatientMod";
                break;

            case CheckEmailIdIsExistMobile:
                //url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/CheckEmailIdIsExistMobile";
                url = BASE_URL + "CheckEmailIdIsExistMobile";
                break;
            case GetCredentialDetails:
                //url = LIVELOGIN_URL + "CredentialsModule/CredentialService.asmx/GetCredentialDetails";
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/GetCredentialDetails";
                break;
          /*  case SignUpPatient:
                url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/SignUpPatient";
                break;*/
            case LogOutIOS:
                // url = LIVELOGIN_URL + "CredentialsModule/CredentialService.asmx/LogOutIOS";
                url = BASE_URL1 + "WebServices/CredentialService.asmx/LogOutIOS";
                break;
            case ChangePasswordIOS:
                //url = LIVELOGIN_URL + "CredentialsModule/CredentialService.asmx/ChangePasswordIOS";
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/ChangePasswordIOS";
                break;
            /*case Register:
                url = LIVELOGIN_URL + "laboratorymodule/LISService.asmx/Register";
                break;*/
            case GetUserDisclaimer:
                // url = LIVELOGIN_URL + "StaffModule/StaffService.asmx/GetUserDisclaimer";
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/GetUserDisclaimer";
                break;
            case FacebookLinked:
                //url = LIVELOGIN_URL + "CredentialsModule/CredentialService.asmx/FacebookLinked";
                url = BASE_URL1 + "WebServices/CredentialService.asmx/FacebookLinked";
                break;
           /* case FacebookLoginMobile:
                url = LIVELOGIN_URL + "CredentialsModule/CredentialService.asmx/FacebookLoginMobile";
                break;*/
          /*  case FacebookUnLinked:
                url = LIVELOGIN_URL + "CredentialsModule/CredentialService.asmx/FacebookUnLinked";
                break;*/
            case GetUserDeatils:
                //url = LIVELOGIN_URL + "CredentialsModule/CredentialService.asmx/GetUserDeatils";
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/GetUserDeatils";
                break;
            case ForgotPassword:
                // url = LIVELOGIN_URL + "CredentialsModule/CredentialService.asmx/ForgotPassword";
                url = BASE_URL + "ForgotPassword";
                break;
            case NeedHelp:
                // url = LIVELOGIN_URL + "CredentialsModule/CredentialService.asmx/NeedHelp";
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/NeedHelp";
                break;

            case PatientDisclaimer:
                //url = LIVELOGIN_URL + "CredentialsModule/CredentialService.asmx/PatientDisclaimer";
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/PatientDisclaimer";
                break;
            case EmailIdExistFacebook:
                url = BASE_URL + "EmailIdExistFacebook";
                break;
            case GetUserCodeFromEmail:
                url = BASE_URL + "GetUserCodeFromEmail";
                break;

            case GetPatientTestImagesInCase:
                url = BASE_URL + "GetPatientTestImagesInCase";
                break;
          /*  case PatientFileVault:
                url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/PatientFileVault";
                break;*/
            case UpdateImage:
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/UpdateImage";
                break;
            case GetpatienttestReportAndroid:
                url = BASE_URL1 + "WebServices/HTMLReports.asmx/GetpatienttestReportHTMLAndroid"; //TODO ayaz uncomment it on live
                //url = "https://api.healthscion.com/WebServices/HTMLReports.asmx/GetpatienttestReportHTMLAndroid";
                //url = "http://192.168.1.11/WebServices/HTMLReports.asmx/GetpatienttestReportHTMLAndroid"; // Local //TODO ayaz uncomment it on local
                //http://192.168.1.202:86/WebServices/HTMLReports.asmx
                break;
            case GetUserDetailsFromContactNoMobileService:
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/GetUserDetailsFromContactNoMobileService";
                break;
        /*    case GetPatientFiles:
                url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/GetPatientFiles";
                break;
            case loadVaultMobile:
                url = LIVELOGIN_URL + "Patient/loadVaultMobile";
                break;
            case getDistinctTags:
                url = LIVELOGIN_URL + "Patient/getDistinctTags";
                break;
            case DeletePatientFiles:
                url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/DeletePatientFiles";
                break;
            case GetPatientImagesInCase:
                url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/GetPatientImagesInCase";
                break;
            case GetAutoAreaSearch:
                url = LIVELOGIN_URL + "SupplierModule/SupplierMasterService.asmx/GetAutoAreaSearch";
                break;
            case checkAliasExist:
                url = LIVELOGIN_URL + "CommonMasterModule/CommonMasterService.asmx/checkAliasExist";
                break;*/
            case GetLabByTest:
                url = BASE_URL + "GetLabByTest";
                break;
            case GetAllObjectFromS3:
                url = BASE_URL + "GetAllObjectFromS3";
                break;
            case FindLabsTest:
                url = BASE_URL + "FindLabsTest";
                break;
          /*  case GetPatientHistory:
                url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/GetPatientHistory";
                break;
            case GetOthersFromArea:
                url = LIVELOGIN_URL + "CommonMasterModule/UIService.asmx/GetOthersFromArea";
                break;
            case GetAllVaccines:
                url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/GetAllVaccines";
                break;
            case GetPatientAlertList:
                url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/GetPatientAlertList";
                break;*/
            case getBasicDetails:
                url = BASE_URL + "getBasicDetails";
                break;
            case GetAdvisedInvestigationDetailMobile:
                url = BASE_URL + "GetAdvisedInvestigationDetailMobile";
                break;
            case GetAllLisPatientCaseDetailMobileNew:
                url = BASE_URL + "GetAllLisPatientCaseDetailMobileNew";
                break;
            case UploadPrescriptionMail:
                url = BASE_URL + "UploadPrescriptionMail";
                break;
            case SendCouponWithTestViaSMSNew:
                url = BASE_URL + "SendCouponWithTestViaSMSNew";
                break;
            case SendCouponWithOutTestViaSMS:
                url = BASE_URL + "SendCouponWithOutTestViaSMS";
                break;
            case SendCouponWithTestViaEmailNew:
                url = BASE_URL + "SendCouponWithTestViaEmailNew";
                break;
            case SendCouponWithOutTestViaEmail:
                url = BASE_URL + "SendCouponWithOutTestViaEmail";
                break;
            case GetPatientTestRangeDataMobile:
                url = BASE_URL + "GetPatientTestRangeDataMobile";
                break;
            case GetPatientVerification:
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/GetPatientVerification";
                break;
            case EmailVerificationClinic:
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/SendMailiOS";
                break;

            case GenerateCouponNo:
                url = BASE_URL + "GenerateCouponNo";
                break;
/*
            case PatientFileVaultNew:
                url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/PatientFileVaultNew";
                break;

            case CheckEmailIdIsExist:
                url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/CheckEmailIdIsExist";
                break;*/
            case CheckAliasExistMobile:
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/CheckAliasExistMobile";
                break;
            case IsUserNameAliasExists:
                url = BASE_URL + "IsUserNameAliasExists";
                break;
          /*  case GetPatientTestReportMobile:
                url = LIVELOGIN_URL + "LaboratoryModule/lisservice.asmx/GetPatientTestReportMobile";
                break;*/
            case GetTestByLab:
                url = BASE_URL + "GetTestByLab";
                break;
            case SendLabContactDetail:
                url = BASE_URL + "SendLabContactDetail";
                break;
            case UpdateContact:
                url = BASE_URL + "UpdateContact";
                break;
            case SamplePickUp:
                url = BASE_URL + "SamplePickUp";
                break;
            case GetLabList:
                url = BASE_URL + "GetLabList";
                break;
            case GetAllTestData:
                url = BASE_URL + "GetAllTestData";
                break;
            case GetPhotographData:
                url = BASE_URL + "GetPhotographData";
                break;
            case GetDoctorData:
                url = BASE_URL + "GetDoctorData";
                break;
            case GetCentreData:
                url = BASE_URL + "GetCentreData";
                break;
            case SinglePackageDetails:
                url = BASE_URL + "SinglePackageDetails";
                break;
            case GetOrderHistoryDetails:
                url = BASE_URL + "GetOrderHistoryDetails";
                break;
            case applypromocode:
                url = BASE_URL + "applypromocode";
                break;
            case GetFilesForOrderID:
                url = BASE_URL + "GetFilesForOrderID";
                break;
            case IsContactNoExists:
                url = BASE_URL + "IsContactNoExists";
                break;
            case BookTestNew:
                url = BASE_URL + "BookTestNew";
                break;
            case SendAllReportToUser:
                url = BASE_URL + "SendAllReportToUser";
                break;
            case GenerateInvoice:
                url = BASE_URL + "GenerateInvoice";
                break;
            case CheckLabrangefrom_area:
                url = BASE_URL + "CheckLabrangefrom_area";
                break;
            case AuthenticateUserSession:
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/AuthenticateUserSessionNew";
                break;
            case agreeTermsCondition:
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/agreeTermsCondition";
                break;
           /* case GetPatient:
                url = LIVELOGIN_URL + "PatientModule/PatientBedAssignmentService.asmx/GetPatient";
                break;
            case GetpatienttestReport:
                url = LIVELOGIN_URL + "LaboratoryModule/LISService.asmx/GetpatienttestReport";
                break;
            case GetAllCompletedTestDetailsOfPatient:
                url = LIVELOGIN_URL + "LaboratoryModule/LISInvestigationWorklistService.asmx/" +
                        "GetAllCompletedTestDetailsOfPatient";
                break;
            case ResendSmsVerifyCodeClinic:
                url = LIVELOGIN_URL + "PatientModule/PatientService.asmx/ResendSmsVerifyCodeClinic";
                break;
            case GetCityList1:
                url = LIVELOGIN_URL + "CommonMasterModule/CommonMasterService.asmx/GetCityList1";
                break;
            case GetCityList:
                url = LIVELOGIN_URL + "CommonMasterModule/UIService.asmx/GetCityList";
                break;
            case GetStateFromCity:
                url = LIVELOGIN_URL + "CommonMasterModule/UIService.asmx/GetStateFromCity";
                break;*/

          /*  case GetCountryList:
                url = LIVELOGIN_URL + "CommonMasterModule/UIService.asmx/GetCountryList";
                break;*/
          /*  case GetAllNationality:
                url = LIVELOGIN_URL + "CommonMasterModule/UIService.asmx/GetAllNationality";
                break;*/
            case CheckVerifyCodeClinic:
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/CheckVerifyCode";
                break;

            case CallmePatient:
                url = BASE_URL + "CallmePatient";
                break;
            case CancelOrder:
                url = BASE_URL + "CancelOrder";
                break;
            case AllPackage:
                url = BASE_URL + "AllPackage";
                break;
            case HomePackage:
                url = BASE_URL + "HomePackage";
                break;
            /*case GetStateList:
                url = LIVELOGIN_URL + "CommonMasterModule/UIService.asmx/GetStateList";
                break;*/
            case BASE_URL:
                url = BASE_URL;
                break;
            /*case LIVELOGIN_URL:

                 url = "http://192.168.1.56:8085/"; //local  dheer   dheer@123
                //  url ="http://192.168.1.107/";
               // url = "https://l141702.cloudchowk.com/";//live
                //   url = "https://d141702.cloudchowk.com/";//demo   /rahul2  androidios
                break;*/
            case saveOtherDetail:
                url = BASE_URL + "saveOtherDetailMod";
                break;

            case CreateFolder:
                url = BASE_URL + "CreateFolder";
                break;

            case CreateLockFolder:
                url = BASE_URL + "CreateLockFolder";
                break;

            case DeleteObject:
                url = BASE_URL + "DeleteObject";
                break;
            case AcceptRequest:
                url = BASE_URL + "AcceptRequest";
                break;
            case MoveObject:
                url = BASE_URL + "MoveObject";
                break;

            case deleteSingularDetails:
                url = BASE_URL + "deleteSingularDetails";
                break;

            case saveHealthDetail:
                url = BASE_URL + "saveHealthDetailMod";
                break;

            case getAllergies:
                url = BASE_URL + "getAllergies";
                break;

            case GetCountryList:
                url = BASE_URL + "getCountries";
                break;
            case getNationality:
                url = BASE_URL + "getNationality";
                break;
            case saveBasicDetail:
                url = BASE_URL + "saveBasicDetail";
                break;
            case UploadProfilePic:
                url = BASE_URL + "UploadProfilePic";
                break;
            case GetMember:
                url = BASE_URL + "GetMember";
                break;
            case AddMember:
                url = BASE_URL + "AddMember";
                break;
            case IsContactExist:
                url = BASE_URL + "IsContactExist";
                break;
            case GetMemberRecords:
                url = BASE_URL + "GetMemberRecords";
                break;
            case getpatientHistoryDetails:
                url = BASE_URL + "getpatientHistoryDetails";
                break;
            case Updatepatientbloodgroup:
                url = BASE_URL + "Updatepatientbloodgroup";
                break;
            case patientbussinessModel:
                url = BASE_URL + "patientbussinessModel";
                break;
            case GetQuizData:
                url = BASE_URL + "GetQuizData";
                break;
            case GetVaccineDetails:
                url = BASE_URL + "GetVaccineDetails";
                break;
            case UpdatePatientVaccineDetails:
                url = BASE_URL + "UpdatePatientVaccineDetails";
                break;

            case InsertIntoPatientVaccineDetails:
                url = BASE_URL + "InsertIntoPatientVaccineDetails";
                break;

            case GetLatestVersionInfo:
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/GetLatestVersionInfo1";
                break;

            case NewLogIn:
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/" + "NewLogin1";
                break;
            case NewFacebookLoginMod:
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/" + "NewFacebookLoginMod";
                break;

            case NewSignUpByPatientFacebookMod:
                url = BASE_URL + "NewSignUpByPatientFacebookMod";
                break;
            case LogInUser_facebookMod:
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/" + "LogInUser_facebookMod";
                break;

            case GetUserGrade:
                url = BASE_URL + "GetUserGrade";
                break;

            case GetUserFact:
                url = BASE_URL + "GetUserFact";
                break;

            case SaveUserDevice:
                url = BASE_URL1 + "WebServices/CredentialsService.asmx/" + "SaveUserDevice";
                break;

            case GetSchoolDoctorList:
                url = BASE_URL + "GetSchoolDoctorList";
                break;

            case getSchoolStudentDetails:
                url = BASE_URL + "getSchoolStudentDetails";
                break;

            case UploadImage_New:
                url = BASE_URL + "UploadImage_New";
                break;
            case saveHealthDetailMod:
                url = BASE_URL + "saveHealthDetailMod";
                break;
            case UpdateALId:
                url = BASE_URL + "UpdateALId";
                break;

            case ConsultAdd:
                url = BASE_URL + "ConsultAdd";
                break;

            case ConsultTestList:
                url = BASE_URL + "ConsultTestList";
                break;

            case ConsultSymptomsList:
                url = BASE_URL + "ConsultSymptomsList";
                break;

            case ConsultPayment:
                url = BASE_URL + "ConsultPayment";
                break;

            case ConsultS3Records:
                url = BASE_URL + "ConsultS3Records";
                break;

            case ConsultAddSymptoms:
                url = BASE_URL + "ConsultAddSymptoms";
                break;

            case PastVisitList:
                url = BASE_URL + "PastVisitList";
                break;

            case PastVisitDetails:
                url = BASE_URL + "PastVisitDetails";
                break;

            case GetPatientInfo:
                url = BASE_URL + "GetPatientInfo";
                break;

            case GetPrescriptionReport:
                url = BASE_URL + "GetPrescriptionReport";
                break;

            case PastPatientList:
                url = BASE_URL + "PastPatientList";
                break;

            case PastPatientDetails:
                url = BASE_URL + "PastPatientDetails";
                break;

            default:
                break;
        }
        return url;
    }


    public enum Services_static {
        LogIn, NewLogIn, AuthenticateUserSession, GetCredentialDetails, agreeTermsCondition, SignUpPatient,
        LogOutIOS, ChangePasswordIOS, Register, GetUserDisclaimer, FacebookLinked, FacebookLoginMobile,
        FacebookUnLinked, GetUserDeatils, ForgotPassword, NeedHelp, CheckEmailIdIsExistMobile,
        PatientDisclaimer, EmailIdExistFacebook, GetUserCodeFromEmail, SignUpByPatient,
        GetPatientTestImagesInCase, PatientFileVault, UpdateImage, GetpatienttestReportAndroid,
        GetUserDetailsFromContactNoMobileService, GetPatientFiles, loadVaultMobile, getDistinctTags,
        DeletePatientFiles, GetPatientImagesInCase, GetAutoAreaSearch, checkAliasExist, GetLabByTest,
        FindLabsTest, GetPatientHistory, getBasicDetails, saveOtherDetail, GetOthersFromArea,
        GetAllVaccines, GetPatientAlertList, GetAdvisedInvestigationDetailMobile,
        GetAllLisPatientCaseDetailMobileNew, SendLabContactDetail, UpdateContact, SamplePickUp, BookTest,
        GetLabList, GetAllTestData, GetPhotographData, GetDoctorData, GetCentreData, GetPatient,
        GetpatienttestReport, GetAllCompletedTestDetailsOfPatient, UploadPrescriptionMail,
        SendCouponWithTestViaSMSNew, SendCouponWithOutTestViaSMS, SendCouponWithTestViaEmailNew,
        SendCouponWithOutTestViaEmail, GetPatientTestRangeDataMobile, GetPatientVerification,
        EmailVerificationClinic, ResendSmsVerifyCodeClinic, CheckVerifyCodeClinic, GetAllNationality,
        GetCountryList, GetStateList, GetCityList1, GetStateFromCity, GenerateCouponNo,
        PatientFileVaultNew, CheckEmailIdIsExist, CheckAliasExistMobile, IsUserNameAliasExists, GetPatientTestReportMobile,
        GetTestByLab, BookTestNew, SinglePackageDetails, GetOrderHistoryDetails, applypromocode,
        GetFilesForOrderID, IsContactNoExists, SendAllReportToUser, GenerateInvoice, CheckLabrangefrom_area,
        GetCityList, AllPackage, HomePackage, BASE_URL, LIVELOGIN_URL, CallmePatient, CancelOrder,
        CreateFolder, DeleteObject, MoveObject, deleteSingularDetails, saveHealthDetail, getAllergies,
        getNationality, saveBasicDetail, UploadProfilePic, GetAllObjectFromS3, CreateLockFolder,
        GetMember, AddMember, AcceptRequest, IsContactExist, GetMemberRecords, getpatientHistoryDetails,
        Updatepatientbloodgroup, patientbussinessModel, GetQuizData, GetVaccineDetails, InsertIntoPatientVaccineDetails,
        UpdatePatientVaccineDetails, GetLatestVersionInfo, NewFacebookLoginMod, CheckContactNoExist, CheckDupUserName, NewSignUpByPatientMod,
        LogInUser_facebookMod, NewSignUpByPatientFacebookMod, GetUserGrade, SaveUserDevice, GetSchoolDoctorList, getSchoolStudentDetails, UploadImage_New, GetUserFact,
        saveHealthDetailMod, UpdateALId, ConsultSymptomsList, ConsultAdd, ConsultTestList, ConsultPayment, ConsultS3Records, ConsultAddSymptoms,
        PastVisitList, PastVisitDetails, GetPatientInfo, GetPrescriptionReport, PastPatientList, PastPatientDetails
    }

}


