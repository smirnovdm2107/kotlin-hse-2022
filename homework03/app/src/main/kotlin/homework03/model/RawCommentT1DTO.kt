package homework03.model

data class RawCommentT1DTO(
    val created: Long,
    val ups: Int,
    val downs: Int,
    val body: String,
    val author: String,
    val replies: RedditDataDTO?,
    val id: String,
) : RawCommentT {
    override fun toCommentDTO(topicId: String): CommentDTO = CommentDTO(
        created, ups, downs, body, author, replies?.toCommentsDTO(topicId) ?: emptyList(), id, topicId
    )
}
