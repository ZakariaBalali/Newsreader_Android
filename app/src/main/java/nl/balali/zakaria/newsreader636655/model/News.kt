package nl.balali.zakaria.newsreader636655.model

import java.io.Serializable

data class News(
    val Id: Int,
    val Feed: Int?,
    val Title: String?,
    val Summary: String?,
    val PublishDate: String?,
    val Image: String?,
    val Url: String?,
    val Related: List<String>?,
    val Categories: List<Category>?,
    var IsLiked: Boolean?
)  : Serializable

data class Category(
    val Id: Int,
    val Name: String?,
) : Serializable