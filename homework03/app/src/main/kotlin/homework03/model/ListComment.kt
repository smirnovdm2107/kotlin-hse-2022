package homework03.model

data class ListComment(
    val created: Long,
    val ups: Int,
    val downs: Int,
    val text: String,
    val author: String,
    val id: String,
    val topicId: String
)
