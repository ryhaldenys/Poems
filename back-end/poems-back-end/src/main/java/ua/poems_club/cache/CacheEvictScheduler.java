package ua.poems_club.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CacheEvictScheduler {
    private final CacheManager cacheManager;

    @Scheduled(fixedRate = 4200)
    public void evictAllCache(){
        cacheManager.getCacheNames()
                .forEach(c -> Objects.requireNonNull(cacheManager.getCache(c)).clear());
    }
}
