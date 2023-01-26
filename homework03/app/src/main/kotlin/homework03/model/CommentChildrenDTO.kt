package homework03.model

data class CommentChildrenDTO(
    val children: List<CommentDataDTO>
) : RawCommentsDTO {
    override fun toCommentsDTO(topicId: String): List<CommentDTO> = children.map{it.toCommentDTO(topicId)}
}
