package nl.balali.zakaria.newsreader636655.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import nl.balali.zakaria.newsreader636655.R
import nl.balali.zakaria.newsreader636655.utilities.SessionManager
import nl.balali.zakaria.newsreader636655.R.layout.main_fragment
import nl.balali.zakaria.newsreader636655.adapter.NewsAdapter
import nl.balali.zakaria.newsreader636655.logic.NewsLogic
import nl.balali.zakaria.newsreader636655.logic.response.NewsResponse
import nl.balali.zakaria.newsreader636655.logic.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

class MainFragment : Fragment() {

    private val newsLogic: NewsLogic = Retrofit.newsLogic
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var newsArticleNestedScrollView: NestedScrollView
    private lateinit var newsArticleSwipeRefresh: SwipeRefreshLayout
    private lateinit var newsArticleRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLabel : TextView

    private var nextId: Int? = null
    private var isLoading: Boolean = false
    private val pageLimit : Int = 20

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)
        errorLabel = view.findViewById(R.id.errorLabel)
        getNewsArticles(null)

        newsArticleSwipeRefresh.setOnRefreshListener {
            if (!isLoading) {
                isLoading = true
                getNewsArticles(null)
            }
        }

        newsArticleNestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                if (!isLoading) {
                    if (scrollY == (v.getChildAt(0).measuredHeight - v.measuredHeight)) {
                        showLoader()
                        getNewsArticles(nextId)
                    }
                }
            })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(main_fragment, container, false)
        newsArticleRecyclerView = view.findViewById(R.id.newsArticleRecyclerView)
        newsArticleNestedScrollView = view.findViewById(R.id.newsArticleNestedScrollView)
        newsArticleSwipeRefresh = view.findViewById(R.id.newsArticleSwipeRefresh)
        linearLayoutManager = LinearLayoutManager(this.context)
        newsAdapter = NewsAdapter(this)
        newsArticleRecyclerView.layoutManager = linearLayoutManager
        newsArticleRecyclerView.adapter = newsAdapter

        return view
    }

    private fun getNewsArticles(nextNewsArticleId: Int?){
        hideError()
        if(nextNewsArticleId == null){

            newsLogic.getNewsArticles(pageLimit, (SessionManager::getToken)()).enqueue(
                object : Callback<NewsResponse> {
                    override fun onResponse(
                        call: Call<NewsResponse>,
                        response: Response<NewsResponse>
                    ) {
                        if (response.body() != null) {

                            if (newsAdapter.itemCount > 0) {
                                newsAdapter.clearNewsArticles()
                            }

                            nextId = response.body()!!.NextId
                            newsAdapter.addNewsArticles(response.body()!!.Results)

                            if (newsAdapter.itemCount == 0) {
                                showError(getString(R.string.noNewsArticlesFound))
                            }

                        } else {
                            showError(getString(R.string.noNewsArticlesFound))
                        }
                        newsArticleSwipeRefresh.isRefreshing = false
                        hideLoader()
                    }

                    override fun onFailure(call: Call<NewsResponse>, t: Throwable) {

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

                        newsArticleSwipeRefresh.isRefreshing = false
                        hideLoader()
                    }
                })
        }else{
            newsLogic.getNextNewsArticles(
                nextNewsArticleId,
                pageLimit,
                (SessionManager::getToken)()
            ).enqueue(object :
                Callback<NewsResponse> {
                override fun onResponse(
                    call: Call<NewsResponse>,
                    response: Response<NewsResponse>
                ) {
                    if (response.body() != null) {
                        nextId = response.body()!!.NextId
                        newsAdapter.addNewsArticles(response.body()!!.Results)

                        if (newsAdapter.itemCount == 0) {
                            showError(getString(R.string.noNewsArticlesFound))
                        }

                    } else {
                        showError("Error")
                    }

                    newsArticleSwipeRefresh.isRefreshing = false
                    hideLoader()
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
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

                    newsArticleSwipeRefresh.isRefreshing = false
                    hideLoader()
                }
            })
        }
    }

    private fun showLoader(){
        isLoading = true
        progressBar.visibility = View.VISIBLE
    }

    fun hideLoader(){
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