package rate.limiter.rateLimit;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
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

    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String targetIdentifier = resolveKey(joinPoint, rateLimit.key());
        String methodName = joinPoint.getSignature().getName();
        String redisKeyString = "rateLimit:"  + methodName + ":" + targetIdentifier;
        byte[] redisKey = redisKeyString.getBytes(StandardCharsets.UTF_8);

        Bucket bucket = proxyManager.builder().build(redisKey, () -> createBucketConfiguration(rateLimit));

        if(bucket.tryConsume(1)){
            return joinPoint.proceed();
        }
        else{
            throw new TooManyRequests("Max requests exceeded try again later");
        }
    }

    private String resolveKey(ProceedingJoinPoint joinPoint, String keySpel) {
        if (keySpel == null || keySpel.isEmpty()) {
            return getClientIp(request);
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        Object value = parser.parseExpression(keySpel).getValue(context);
        return value != null ? value.toString() : getClientIp(request);
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
