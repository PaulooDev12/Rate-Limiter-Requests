package rate.limiter.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LocalizationInfo(
        @JsonProperty("contry") String country,
        @JsonProperty("regionName") String regionName,
        @JsonProperty("city") String city,
        @JsonProperty("isp") String isp
        ) {
}
