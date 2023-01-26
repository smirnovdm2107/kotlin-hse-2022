package homework03.model

import java.util.*

data class CommentsSnapshot(
    val comments: List<CommentDTO>,
    val date: Date = Date()
)
