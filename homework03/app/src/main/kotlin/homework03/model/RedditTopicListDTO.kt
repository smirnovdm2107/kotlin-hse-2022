package homework03.model

import com.fasterxml.jackson.annotation.JsonProperty

data class RedditTopicListDTO(
    @JsonProperty("children")
    val redditTopicDataDTOs: List<RedditTopicDataDTO>
)
