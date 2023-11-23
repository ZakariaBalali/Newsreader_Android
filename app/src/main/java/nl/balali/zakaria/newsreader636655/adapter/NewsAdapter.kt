package nl.balali.zakaria.newsreader636655.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import nl.balali.zakaria.newsreader636655.R
import nl.balali.zakaria.newsreader636655.logic.NewsLogic
import nl.balali.zakaria.newsreader636655.model.News
import nl.balali.zakaria.newsreader636655.logic.Retrofit
import nl.balali.zakaria.newsreader636655.utilities.SessionManager
import nl.balali.zakaria.newsreader636655.utilities.ToastUtil
import nl.balali.zakaria.newsreader636655.view.NewsDetailActivity
import nl.balali.zakaria.newsreader636655.view.fragment.LoginFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsAdapter(private var fragment: Fragment) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    var newsArticles : MutableList<News> = ArrayList()
    private val newsLogic: NewsLogic = Retrofit.newsLogic
    private val likeColor = Color.RED
    private val unlikeColor= Color.BLACK
    private var isBeingLiked: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_article_cell, parent, false)

        val viewHolder = ViewHolder(view)


        view.setOnClickListener {
            val intent = Intent(parent.context, NewsDetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            intent.putExtra("article", newsArticles[viewHolder.adapterPosition])
            parent.context.startActivity(intent)
        }


        view.findViewById<ImageButton>(R.id.likeIconCell).setOnClickListener {
            if(!(SessionManager::isLogin)()){
                fragment.activity?.supportFragmentManager?.beginTransaction()?.apply {
                    replace(R.id.frameLayoutWrapper, LoginFragment())
                    commit()
                }
            }else{
                if(!isBeingLiked){
                    isBeingLiked = true
                    likeNewsArticle(viewHolder)
                }
            }
        }

        return viewHolder
    }
    fun addNewsArticles(newsArticles: List<News>) {
        this.newsArticles.addAll(newsArticles)
        notifyItemInserted( this.newsArticles.size - 1);
    }

    fun clearNewsArticles() {
        this.newsArticles.removeAll(this.newsArticles)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val article = newsArticles[position]
        holder.title.text = article.Title

        holder.thumbnail.load(article.Image){
            placeholder(R.drawable.ic_image)
            scale(Scale.FILL)
        }
        if(!article.IsLiked!!){
            holder.likeIcon.setColorFilter(unlikeColor)
        }else{
            holder.likeIcon.setColorFilter(likeColor)
        }
    }

    override fun getItemCount() = newsArticles.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.titleCell)
        val thumbnail: ImageView = itemView.findViewById(R.id.thumbnailCell)
        val likeIcon: ImageButton = itemView.findViewById(R.id.likeIconCell)
    }

    private fun likeNewsArticle(holder: ViewHolder){

        val news = newsArticles[holder.adapterPosition]

        if(!news.IsLiked!!){

            newsLogic.likeNewsArticle(news.Id, (SessionManager::getToken)()).enqueue(object :
                Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code().toString()) {
                        "200" -> {
                            newsArticles[holder.adapterPosition].IsLiked = true
                            holder.itemView.findViewById<ImageButton>(R.id.likeIconCell).setColorFilter(likeColor)
                        }
                        "401" -> {
                            fragment.context?.getString(R.string.notLoggedIn)?.let {
                                (ToastUtil::shortToast)(
                                    it
                                )
                            }
                        }
                        else -> {
                            fragment.context?.getString(R.string.unexpectedError)?.let {
                                (ToastUtil::shortToast)(
                                    it
                                )
                            }
                        }
                    }
                    isBeingLiked = false
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    fragment.context?.getString(R.string.likingError)?.let {
                        (ToastUtil::shortToast)(
                            it
                        )
                    }
                    isBeingLiked = false
                }
            })

        }else{
            newsLogic.unlikeNewsArticle(news.Id, (SessionManager::getToken)()).enqueue(object :
                Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code().toString()) {
                        "200" -> {
                            newsArticles[holder.adapterPosition].IsLiked = false
                            holder.itemView.findViewById<ImageButton>(R.id.likeIconCell).setColorFilter(unlikeColor)
                        }
                        "401" -> {
                            fragment.context?.getString(R.string.notLoggedIn)?.let {
                                (ToastUtil::shortToast)(
                                    it
                                )
                            }
                        }
                        else -> {
                            fragment.context?.getString(R.string.unexpectedError)?.let {
                                (ToastUtil::shortToast)(
                                    it
                                )
                            }
                        }
                    }
                    isBeingLiked = false
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    fragment.context?.getString(R.string.likingError)?.let {
                        (ToastUtil::shortToast)(
                            it
                        )
                    }
                    // disable like load
                    isBeingLiked = false
                }
            })
        }
    }
}