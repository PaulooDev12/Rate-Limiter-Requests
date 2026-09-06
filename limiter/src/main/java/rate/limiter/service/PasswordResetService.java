package rate.limiter.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import rate.limiter.models.User;
import rate.limiter.repositories.UserRepository;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetService {

    private static final String LUA_ATOMIC_SCRIPT =
            "local userId = redis.call('GET', KEYS[1])" +
                    "if userId then " +
                    "redis.call('DEL', KEYS[1]) " +
                    "end " +
                    "return userId";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final SecureRandom secureRandom = new SecureRandom();

    private String generateVerificationCode(){
        int code = secureRandom.nextInt(100000000);
        return String.format("%08d",code);
    }
    public void generateResetToken(String email){
        String token = generateVerificationCode();
        User user = userRepository.findByEmail(email).orElse(null);
        if(user == null) return;
        String redisKey = "reset:" + token;
        redisTemplate.opsForValue().set(redisKey, user.getId().toString(), 15,TimeUnit.MINUTES);
        System.out.println(token);
        redisTemplate.expire(redisKey, 15, TimeUnit.MINUTES);

    }
    public void ResetPassword(String token, String password){
        String redisKey = "reset:" + token;
        String userId = redisTemplate.execute(
                new DefaultRedisScript<>(LUA_ATOMIC_SCRIPT, String.class),
                Collections.singletonList(redisKey)
        );
        if(userId == null){
            throw new RuntimeException("token invalido");
        }
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(password);
        userRepository.save(user);
    }

}
