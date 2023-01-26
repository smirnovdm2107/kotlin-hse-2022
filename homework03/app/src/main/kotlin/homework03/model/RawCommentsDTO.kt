package homework03.model

interface RawCommentsDTO {
    fun toCommentsDTO(topicId: String) : List<CommentDTO>
}
