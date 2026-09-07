package rate.limiter.models;



import java.util.List;

public record UserPrincipal(String id, String email, List<String> roles){
}
