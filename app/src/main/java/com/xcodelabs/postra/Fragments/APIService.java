package com.xcodelabs.postra.Fragments;

import com.xcodelabs.postra.Notifications.MyResponse;
import com.xcodelabs.postra.Notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService
{
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAGLXRE_E:APA91bG1-qkXbqSDUFI_CmAZLuPQV4rjWyaGkaSijzRH3XEO3LPPfNSzv1-_g6XW-K7YYZmtvXDHX_qU6cd1JoVclM07kRDhnl2pwtPhDtqV3jwzZwxRNBV3J5DaNBv_gUaKTKptUkpW"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotiication(@Body Sender body);
}
