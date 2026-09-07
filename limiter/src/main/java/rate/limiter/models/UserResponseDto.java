package rate.limiter.models;

import java.util.List;

public record UserResponseDto(String username, List<String> roles) {
}
