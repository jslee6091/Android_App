package com.example.auth0_first

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface RetrofitAPI {
    @GET("/android") // 서버에 GET 요청을 할 주소
    // SecondActivity 에서 불러와서 이 함수에 큐를 만들고 대기열에 콜백을 넣어주면 그것으로 요청
    fun getTodoList() : Call<JsonObject>
}

interface RetrofitPOST {
    @FormUrlEncoded
    @POST("/android2")
    fun dataTransfer(
        @Field("menu") menu : String,
        @Field("cash") cash : String
    ) : Call<JsonObject>
}