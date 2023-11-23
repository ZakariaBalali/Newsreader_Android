package nl.balali.zakaria.newsreader636655.logic.response

import nl.balali.zakaria.newsreader636655.model.News

data class NewsResponse(
    val Results: List<News>,
    val NextId: Int?,
)