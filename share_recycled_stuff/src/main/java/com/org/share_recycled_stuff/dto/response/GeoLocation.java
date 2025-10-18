package com.org.share_recycled_stuff.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Geographical location information obtained from GeoIP service")
public class GeoLocation {
    @Schema(description = "Latitude", example = "21.0285")
    @JsonProperty("lat")
    private Double latitude;

    @Schema(description = "Longitude", example = "105.8542")
    @JsonProperty("lon")
    private Double longitude;

    @Schema(description = "City name", example = "Hanoi")
    private String city;

    @Schema(description = "Country name", example = "Vietnam")
    private String country;

    @Schema(description = "Status of the GeoIP lookup", example = "success")
    private String status;

    @Schema(description = "Region/State name", example = "Hanoi")
    @JsonProperty("regionName")
    private String regionName;

    @Schema(description = "Queried IP address", example = "113.160.234.1")
    private String query; // The IP address queried
}

