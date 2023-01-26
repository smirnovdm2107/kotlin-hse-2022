package homework03.model

data class CommentDataDTO(
    val data: RawCommentT
) : RawCommentT {
    override fun toCommentDTO(topicId: String): CommentDTO = data.toCommentDTO(topicId)
}
