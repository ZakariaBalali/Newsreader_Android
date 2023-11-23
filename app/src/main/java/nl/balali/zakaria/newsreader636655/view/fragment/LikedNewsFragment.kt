package nl.balali.zakaria.newsreader636655.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import nl.balali.zakaria.newsreader636655.R
import nl.balali.zakaria.newsreader636655.utilities.SessionManager
import nl.balali.zakaria.newsreader636655.R.layout.liked_fragment
import nl.balali.zakaria.newsreader636655.adapter.LikedNewsAdapter
import nl.balali.zakaria.newsreader636655.logic.NewsLogic
import nl.balali.zakaria.newsreader636655.logic.response.NewsResponse
import nl.balali.zakaria.newsreader636655.logic.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

class LikedNewsFragment : Fragment() {

    private val newsLogic: NewsLogic = Retrofit.newsLogic
    private lateinit var likedNewsAdapter: LikedNewsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var newsSwipeRefresh: SwipeRefreshLayout
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLabel : TextView

    private var nextId: Int? = null
    private var isLoading: Boolean = false

    override fun onResume() {
        super.onResume()

        if(likedNewsAdapter.itemCount > 0){
            likedNewsAdapter.clearNewsArticles()
            showLoader()
            getLikedNewsArticles()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorLabel = view.findViewById(R.id.errorLabel)
        progressBar = view.findViewById(R.id.progressBar)

        newsSwipeRefresh.setOnRefreshListener {
            if (!isLoading) {
                isLoading = true
                getLikedNewsArticles()
            }
        }

        showLoader()
        getLikedNewsArticles()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view =  inflater.inflate(liked_fragment, container, false)
        newsRecyclerView = view.findViewById(R.id.likedNewArticlesRecyclerView)
        newsSwipeRefresh = view.findViewById(R.id.likedNewsSwipeRefresh)

        layoutManager = LinearLayoutManager(this.context)
        likedNewsAdapter = LikedNewsAdapter(this)

        newsRecyclerView.layoutManager = layoutManager
        newsRecyclerView.adapter = likedNewsAdapter

        return view
    }

    private fun getLikedNewsArticles(){
        hideError()

        newsLogic.getLikedNewsArticles((SessionManager::getToken)()).enqueue(object :
            Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if(response.body() != null){

                    if(likedNewsAdapter.itemCount > 0){
                        likedNewsAdapter.clearNewsArticles()
                    }

                    nextId = response.body()!!.NextId
                    likedNewsAdapter.addNewsArticles(response.body()!!.Results)

                    if(likedNewsAdapter.itemCount == 0){
                        showError(getString(R.string.noLikedNewsArticles))
                    }
                }else{
                    showError("Error")
                }
                newsSwipeRefresh.isRefreshing = false
                hideLoader()
            }
            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                showError("Error: ${t.message.toString()}.")
                when (t) {
                    is UnknownHostException -> {
                        showError(getString(R.string.internetError))
                    }
                    is TimeoutException -> {
                        showError(getString(R.string.refreshError))
                    }
                    else -> {
                        showError("${getString(R.string.refreshError)}. Error: ${t.message.toString()}.")
                    }
                }

                newsSwipeRefresh.isRefreshing = false
                hideLoader()
            }
        })
    }

    private fun showLoader(){
        isLoading = true
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoader(){
        isLoading = false
        progressBar.visibility = View.GONE
    }

    private fun showError(message: String){
        errorLabel.visibility = View.VISIBLE
        errorLabel.text = message
    }

    private fun hideError(){
        errorLabel.visibility = View.GONE
    }
}