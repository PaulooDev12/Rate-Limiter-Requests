package rate.limiter.rateLimit;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rate.limiter.exceptions.TooManyRequests;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ProxyManager<byte[]> proxyManager;


    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String ip = getClientIp(request);
        String methodName = joinPoint.getSignature().getName();
        String redisKeyString = "rateLimit:" + ip + ":" + methodName;
        byte[] redisKey = redisKeyString.getBytes(StandardCharsets.UTF_8);

        Bucket bucket = proxyManager.builder().build(redisKey, () -> createBucketConfiguration(rateLimit));

        if(bucket.tryConsume(1)){
            return joinPoint.proceed();
        }
        else{
            throw new TooManyRequests("Max requests exceeded try again later");
        }
    }

    private BucketConfiguration createBucketConfiguration(RateLimit rateLimit) {
        return BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(rateLimit.maxRequests())
                        .refillIntervally(rateLimit.maxRequests(), Duration.ofSeconds(rateLimit.windowInSeconds()))
                        .build())
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwadedFor = request.getHeader("x-forwarded-for");
        if(xForwadedFor != null && !xForwadedFor.isEmpty()){
            return xForwadedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
