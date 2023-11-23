package nl.balali.zakaria.newsreader636655.logic

import nl.balali.zakaria.newsreader636655.logic.response.LoginResponse
import nl.balali.zakaria.newsreader636655.logic.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.*

interface AuthorizationLogic {

    @FormUrlEncoded
    @POST("users/register")
    fun createUser(@Field("UserName") username: String, @Field("Password") password: String): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("users/login")
    fun login( @Field("UserName") username: String, @Field("Password") password: String): Call<LoginResponse>
}