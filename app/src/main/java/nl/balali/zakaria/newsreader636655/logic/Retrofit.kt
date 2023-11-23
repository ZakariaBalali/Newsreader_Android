package nl.balali.zakaria.newsreader636655.logic

import nl.balali.zakaria.newsreader636655.utilities.Constants.Companion.URL
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Retrofit {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    val authorizationLogic: AuthorizationLogic by lazy {
        retrofit.create(AuthorizationLogic::class.java)
    }

    val newsLogic: NewsLogic by lazy {
        retrofit.create(NewsLogic::class.java)
    }

}