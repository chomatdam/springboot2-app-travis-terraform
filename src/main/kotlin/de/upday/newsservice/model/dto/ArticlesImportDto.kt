package de.upday.newsservice.model.dto

import de.upday.newsservice.model.Article

data class ArticlesImportDto(val status: Int,
                             val articles: List<Article>)