package de.upday.newsservice.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Article(@Id val id: String,
                   val title: String,
                   val description: String,
                   val url: String,
                   val imageUrl: String,
                   @CreatedDate val createdAt: LocalDateTime,
                   @LastModifiedDate val updatedAt: LocalDateTime?,
                   val source: String,
                   val categories: List<String>
)