package homework03.model

import com.fasterxml.jackson.annotation.JsonProperty

data class RedditTopicDataDTO(
    @JsonProperty("data")
    val rawTopicInfoDTO: RawTopicInfoDTO
)
