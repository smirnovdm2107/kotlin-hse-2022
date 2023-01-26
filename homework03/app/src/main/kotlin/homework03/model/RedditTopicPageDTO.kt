package homework03.model

import com.fasterxml.jackson.annotation.JsonProperty

data class RedditTopicPageDTO(
    @JsonProperty("data")
    val redditTopicListDTO: RedditTopicListDTO
)
