package homework03.model

data class RawCommentT3DTO(
    val created: Long,
    val ups: Int,
    val downs: Int,
    val selftext: String?,
    val author: String,
    val id: String,
) : RawCommentT{
    override fun toCommentDTO(topicId: String): CommentDTO = CommentDTO(
        created, ups, downs, selftext ?: "", author, emptyList(), id, topicId
    )
}
