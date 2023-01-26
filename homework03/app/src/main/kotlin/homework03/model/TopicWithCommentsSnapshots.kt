package homework03.model

data class TopicWithCommentsSnapshots(
    val topicSnapshot: TopicSnapshot,
    val commentsSnapshot: List<CommentsSnapshot>
)
