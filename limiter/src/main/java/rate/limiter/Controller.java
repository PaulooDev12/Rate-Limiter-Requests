package rate.limiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rate.limiter.models.User;
import rate.limiter.rateLimit.RateLimit;
import rate.limiter.repositories.UserRepository;
import rate.limiter.service.PasswordResetService;

@RestController
@RequestMapping("/")
public class Controller {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetService passwordResetService;

    @RateLimit(windowInSeconds = 25, maxRequests = 2)
    @GetMapping("test")
    public String test(){
        return "Ainda tem Requests";
    }

    @RateLimit(windowInSeconds = 100, maxRequests = 2)
    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody User user){
        return ResponseEntity.ok(userRepository.save(user));
    }
    @GetMapping("data")
    public ResponseEntity<?> getUserData(){
        return ResponseEntity.ok(userRepository.findAll());
    }
    @RateLimit(windowInSeconds = 200, maxRequests = 2, key = "#email")
    @PostMapping("forgot-pass/{email}")
    public ResponseEntity<Void> generateForgotPass(@PathVariable String email){
        passwordResetService.generateResetToken(email);
        return ResponseEntity.ok().build();
    }
    @PostMapping("reset-pass")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String password){
        passwordResetService.ResetPassword(token, password);
        return ResponseEntity.ok().body("Reset Password Success");
    }
}
