package homework03.model

open class CommentDTO(
    val created: Long,
    val ups: Int,
    val downs: Int,
    val text: String,
    val author: String,
    val replies: List<CommentDTO>,
    val id: String,
    val topicId: String
) : Listable {
    override fun toListComment(): ListComment = ListComment(created, ups, downs, text, author, id, topicId)
}
