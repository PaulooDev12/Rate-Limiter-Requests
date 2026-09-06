package rate.limiter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rate.limiter.rateLimit.RateLimit;

@RestController
@RequestMapping("/")
public class Controller {

    @RateLimit(windowInSeconds = 25, maxRequests = 2)
    @GetMapping("test")
    public String test(){
        return "Ainda tem Requests";
    }
}
