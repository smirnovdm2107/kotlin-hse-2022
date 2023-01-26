package homework03.util

import com.soywiz.korio.file.std.toVfs
import homework03.csv.csvSerialize
import homework03.model.*
import homework03.reddit.flatten
import java.io.File

suspend fun saveComments(name: String, comments: List<CommentsSnapshot>) {
    comments
        .map { commentSnapshot ->
            commentSnapshot.comments
                .map { it.flatten() }
                .flatten()
        }
        .flatten()
        .run {
            File("./${name}--comments.csv")
                .toVfs()
                .writeString(
                    csvSerialize(
                        this, ListComment::class
                    )
                )

        }
}

suspend fun saveTopic(name: String, topicSnapshot: TopicSnapshot) {
    File("./${name}--topics.csv")
        .toVfs()
        .writeString(csvSerialize(topicSnapshot.infos, TopicInfoDTO::class))
}