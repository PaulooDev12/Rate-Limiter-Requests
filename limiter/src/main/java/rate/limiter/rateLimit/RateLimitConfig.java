package rate.limiter.rateLimit;


import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Bean
    public ProxyManager<byte[]> proxyManager() {
        String redisUri = String.format("redis://%s:%d", redisHost, redisPort);
        RedisClient redisClient = RedisClient.create(redisUri);

        return LettuceBasedProxyManager.builderFor(redisClient)
                .withExpirationStrategy(
                        ExpirationAfterWriteStrategy
                                .basedOnTimeForRefillingBucketUpToMax(java.time.Duration.ofSeconds(10)))
                .build();
    }
}
