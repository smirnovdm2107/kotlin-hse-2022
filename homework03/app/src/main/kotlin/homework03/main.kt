package homework03

import homework03.reddit.RedditHandler
import homework03.reddit.RedditHandlerImpl
import homework03.util.saveComments
import homework03.util.saveTopic
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
    if (args.isEmpty()) {
        println("usage")
    }
    val redditHandler : RedditHandler = RedditHandlerImpl()
    args.map{redditHandler.getTopicWithComments(it)}.forEachIndexed { index, topicWithComments ->
        saveTopic(args[index], topicWithComments.topicSnapshot)
        saveComments(args[index], topicWithComments.commentsSnapshot)
    }
}