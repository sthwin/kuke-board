package kuke.board.article.service.response

data class ArticlePageResponse(
    val articles: List<ArticleResponse>,
    val articleCount: Long,
) {
}