package homework03.model

interface RawCommentT {
    fun toCommentDTO(topicId: String) : CommentDTO
}
