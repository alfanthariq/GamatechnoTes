package com.alfanthariq.skeleton.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    /*@GET("list/surat/{user_id}/{desa_id}")
    fun getSurat(@Path("user_id") user_id: Int, @Path("desa_id") desa_id: Int): Call<ResponseBody>

    @FormUrlEncoded
    @POST("update-ttd/")
    fun updateTTD(@Field("nik") nik: String,
                  @Field("userid") userid: Int,
                  @Field("ttd_img") base64img: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("submit/surat/")
    fun approveSurat(@Field("tipe_surat") tipe_surat: Int,
                     @Field("level") level: Int,
                     @Field("nomor_surat") nomor_surat: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("login/")
    fun login(
            @Field("username") username: String,
            @Field("password") password: String): Call<ResponseBody>*/
}