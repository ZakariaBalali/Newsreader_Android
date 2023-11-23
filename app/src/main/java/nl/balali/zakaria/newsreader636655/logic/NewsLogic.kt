package nl.balali.zakaria.newsreader636655.logic

import nl.balali.zakaria.newsreader636655.logic.response.NewsResponse
import retrofit2.Call
import retrofit2.http.*

interface NewsLogic {

    @GET("articles")
    fun getNewsArticles(@Query("count") maximumArticles: Int, @Header("x-authtoken") xauthtoken: String?): Call<NewsResponse>

    @GET("articles/{id}")
    fun getNextNewsArticles(@Path("id") articleId: Int?, @Query("count") maximumArticles: Int?, @Header("x-authtoken") xauthtoken : String?): Call<NewsResponse>

    @GET("articles/liked")
    fun getLikedNewsArticles(@Header("x-authtoken") xauthtoken : String?): Call<NewsResponse>

    @PUT("articles/{id}/like")
    fun likeNewsArticle(@Path("id") articleId: Int, @Header("x-authtoken") xauthtoken : String?): Call<Void>

    @DELETE("articles/{id}/like")
    fun unlikeNewsArticle(@Path("id") articleId: Int, @Header("x-authtoken") xauthtoken : String?): Call<Void>
}