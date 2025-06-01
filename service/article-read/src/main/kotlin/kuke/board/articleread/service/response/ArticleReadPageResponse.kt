package kuke.board.articleread.service.response

data class ArticleReadPageResponse(
    val articles: List<ArticleReadResponse>,
    val articleCount: Long,
)