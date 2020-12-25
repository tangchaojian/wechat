package com.tcj.sunshine.net;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 接口服务
 */
public interface ApiService {

    @GET
    Observable<ResponseBody> get(@Url String url);

    @GET
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, String> params);

    @Headers({"Content-Type: application/json; charset=utf-8","Accept: application/json"})//需要添加头
    @POST
    Observable<ResponseBody> postJson(@Url String url, @Body RequestBody body);

    @Multipart
    @POST
    Observable<ResponseBody> post(@Url String url, @PartMap Map<String, RequestBody> params);

    @Multipart
    @POST
    Observable<ResponseBody> upload(@Url String url, @PartMap Map<String, RequestBody> params, @Part List<MultipartBody.Part> files);

    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);

    @DELETE("order/app/order/deleteOrder")
    Observable<ResponseBody> deleteOrder(@Query("odrId") String odrId, @Query("year") String year);

    @FormUrlEncoded
    @PUT("order/app/order/updateOrderState")
    Observable<ResponseBody> putUpdateOrderState(@Field("odrId") String odrId, @Field("year") String year, @Field("odrStatus") String odrStatus);

    @FormUrlEncoded
    @PUT
    Observable<ResponseBody> put(@Url String url, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST
    Observable<ResponseBody> post(@Url String url,@QueryMap  Map<String, String> querys, @FieldMap Map<String, String> fields);
}
