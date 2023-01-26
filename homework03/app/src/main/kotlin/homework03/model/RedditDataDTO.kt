package homework03.model

data class RedditDataDTO(
    val data: CommentChildrenDTO
) : RawCommentsDTO {
    override fun toCommentsDTO(topicId: String): List<CommentDTO> = data.toCommentsDTO(topicId)
}
