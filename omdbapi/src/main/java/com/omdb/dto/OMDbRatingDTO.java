package com.omdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OMDbRatingDTO {

    @JsonProperty("Source")
    private String source;

    @JsonProperty("Value")
    private String value;
}

