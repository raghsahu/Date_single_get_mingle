package com.dating.datesinglegetmingle.Interface;


import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


/**
 * Created by nitin on 25-09-2017.
 */

public interface UserInterface {

    //-------- login --------------------

    @POST("Api/login")
    Call<ResponseBody> login(
            @Query("user_email") String user_email,
            @Query("user_pass") String user_pass,
            @Query("register_id") String register_id,
            @Query("lat") String lat,
            @Query("lng") String lon
    );

    //--------- signup --------------

    @POST("Api/SignUp")
    Call<ResponseBody> SignUp(
            @Query("user_name") String user_name,
            @Query("user_email") String user_email,
            @Query("mobile") String mobile,
            @Query("user_pass") String user_pass,
            @Query("gender") String gender,
            @Query("age") String age,
            @Query("city") String city,
            @Query("register_id") String register_id,
            @Query("age_status") String age_status,
            @Query("lat") String lat,
            @Query("lng") String lon,
            @Query("f_Uid") String f_Uid);

    //--------------- forgot pass -------------

    //http://logicalsofttech.com/singleGetMingle/Api/forgotpassword?user_email=logicalsoftwebteam@gmail.com
    @POST("Api/forgotpassword")
    Call<ResponseBody> forgot_password(
            @Query("user_email") String user_email);

    //---------------Intrest_List  -------------

    @POST("Api/Intrest_List")
    Call<ResponseBody> Intrest_List(@Query("user_id") String user_id);

    //--------------- upload profile images --------

    ////http://logicalsofttech.com/singleGetMingle/index.php/Api/Images_Updated
    @Multipart
    @POST("Api/Images_Updated")
    Call<ResponseBody> Images_Updated(
            @Query("user_id") String user_id,
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part file2,
            @Part MultipartBody.Part file3,
            @Part MultipartBody.Part file4,
            @Part MultipartBody.Part file5,
            @Part MultipartBody.Part file6
    );

    //--------------- update profile detail -------------

    //http://logicalsofttech.com/singleGetMingle/index.php/Api/Update_Profile
    @POST("Api/Update_Profile")
    Call<ResponseBody> Update_Profile(
            @Query("user_id") String user_id,
            @Query("user_name") String user_name,
            @Query("city") String city,
            @Query("education") String education,
            @Query("profession") String profession,
            @Query("drinking_habit") String drinking_habit,
            @Query("smoking") String smoking,
            @Query("eating_habit") String eating_habit,
            @Query("about_self") String about_self,
            @Query("interest_id") String interest_id,
            @Query("interest_field_name") String interest_field_name);

    //----------------- online user list ---------

    //http://logicalsofttech.com/singleGetMingle/index.php/Api/Online_List_By_UserId?user_id=3
    @POST("Api/Online_List_By_UserId")
    Call<ResponseBody> Online_List_By_UserId(
            @Query("user_id") String user_id);

    //-------------- get profile ------------

    //http://logicalsofttech.com/singleGetMingle/index.php/Api/Get_Profile
    @POST("Api/Get_Profile")
    Call<ResponseBody> Get_Profile(
            @Query("user_id") String user_id);

    //-------------- CommonInterestUser ------------

    //http://logicalsofttech.com/singleGetMingle/index.php/Api/CommonInterestUser
    @POST("Api/CommonInterest")
    Call<ResponseBody> CommonInterestUser(
            @Query("user_id") String user_id);


    /*@POST("Api/commonInterest")
    Call<ResponseBody> CommonInterestUser(
            @Query("user_id") String user_id);*/

    //-------------- UserFilter ------------

    @POST("Api/userFilter")
    Call<ResponseBody> UserFilter(
            @Query("user_id") String user_id,
            @Query("from_age") String from_age,
            @Query("to_age") String to_age,
            @Query("city") String city,
            @Query("profile_type") String profile_type);


    //-------------- change password ------------

    //http://logicalsofttech.com/singleGetMingle/index.php/Api/changePassword
    @POST("Api/changePassword")
    Call<ResponseBody> changePassword(
            @Query("user_id") String user_id,
            @Query("oldpassword") String oldpassword,
            @Query("newpassword") String newpassword);

    //-------------- support contact ------------

    @POST("Api/contactsupport")
    Call<ResponseBody> contactsupport(
            @Query("user_id") String user_id,
            @Query("user_email") String user_email,
            @Query("message") String message);

    //-------------- city list ------------

    //http://logicalsofttech.com/singleGetMingle/index.php/Api/City_List
    @POST("Api/City_List")
    Call<ResponseBody> City_List();

    //-------------- userLikeUnlike ------------

    //http://logicalsofttech.com/singleGetMingle/ApiController/userLikeUnlike
    @POST("Api/userLikeUnlike")
    Call<ResponseBody> userLikeUnlike(
            @Query("user_id") String user_id,
            @Query("to_user_id") String to_user_id,
            @Query("status") String status,
            @Query("register_id") String register_id);

    //----------------- getDisLikes ---------

    //http://logicalsofttech.com/singleGetMingle/index.php/Api/getDisLikes?user_id=3
    @POST("Api/getDisLikes")
    Call<ResponseBody> getDisLikes(
            @Query("user_id") String user_id);

    //----------------- getDisLikes ---------

    //http://logicalsofttech.com/singleGetMingle/ApiController/updateOffOnline
    @POST("Api/updateOffOnline")
    Call<ResponseBody> updateOffOnline(
            @Query("user_id") String user_id,
            @Query("status") String status
    );
}
