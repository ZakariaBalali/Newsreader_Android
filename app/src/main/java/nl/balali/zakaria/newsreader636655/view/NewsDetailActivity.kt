package nl.balali.zakaria.newsreader636655.view

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import nl.balali.zakaria.newsreader636655.R
import nl.balali.zakaria.newsreader636655.logic.NewsLogic
import nl.balali.zakaria.newsreader636655.model.News
import nl.balali.zakaria.newsreader636655.logic.Retrofit
import nl.balali.zakaria.newsreader636655.utilities.SessionManager
import nl.balali.zakaria.newsreader636655.utilities.ToastUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsDetailActivity : AppCompatActivity() {

    private val newsLogic: NewsLogic = Retrofit.newsLogic
    private lateinit var news : News
    private lateinit var newsArticleTitle : TextView
    private lateinit var newsArticleImage : ImageView
    private lateinit var likeIcon : ImageButton
    private lateinit var newsArticleSummary : TextView
    private lateinit var newsArticleURL : TextView
    private val likeColor = Color.RED
    private val unlikeColor= Color.BLACK
    private var isBeingLiked: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        newsArticleTitle = findViewById(R.id.articleTitle)
        newsArticleImage = findViewById(R.id.articleImage)
        likeIcon = findViewById(R.id.likeIconCell)
        newsArticleSummary = findViewById(R.id.articleSummary)
        newsArticleURL = findViewById(R.id.articleUrl)


        news = (intent.getSerializableExtra("article") as? News)!!
        newsArticleTitle.text = news.Title
        newsArticleImage.load(news.Image)

        if(!news.IsLiked!!){
            likeIcon.setColorFilter(unlikeColor)
        }else{
            likeIcon.setColorFilter(likeColor)
        }

        newsArticleSummary.text = Html.fromHtml(news.Summary, Html.FROM_HTML_MODE_COMPACT)

        newsArticleURL.append(
            " " + Html.fromHtml(
                "<a href=\"${news.Url}\">${news.Url}</a>",
                Html.FROM_HTML_MODE_COMPACT
            )
        )


        likeIcon.setOnClickListener{
            if(!isBeingLiked){
                isBeingLiked = true
                likeNewsArticle(news)
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun likeNewsArticle(news: News){

        if(!news.IsLiked!!){

            newsLogic.likeNewsArticle(news.Id, (SessionManager::getToken)()).enqueue(object :
                Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code().toString()) {
                        "200" -> {
                            news.IsLiked = true
                            likeIcon.setColorFilter(likeColor)
                        }
                        "401" -> {
                            (ToastUtil::shortToast)(getString(R.string.notLoggedIn))
                        }
                        else -> {
                            (ToastUtil::shortToast)(getString(R.string.unexpectedError))
                        }
                    }

                    isBeingLiked = false
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    (ToastUtil::shortToast)(getString(R.string.likingError))
                    isBeingLiked = false
                }
            })

        }else{
            newsLogic.unlikeNewsArticle(news.Id, (SessionManager::getToken)()).enqueue(
                object :
                    Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        when (response.code().toString()) {
                            "200" -> {
                                news.IsLiked = false
                                likeIcon.setColorFilter(unlikeColor)
                            }
                            "401" -> {
                                (ToastUtil::shortToast)(getString(R.string.notLoggedIn))
                            }
                            else -> {
                                (ToastUtil::shortToast)(getString(R.string.unexpectedError))
                            }
                        }

                        isBeingLiked = false
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        (ToastUtil::shortToast)(getString(R.string.likingError))
                        isBeingLiked = false
                    }
                })
        }
    }

}