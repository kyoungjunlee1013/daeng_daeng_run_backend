// src/main/java/com/daengdaengrun/daengdaengrunbackend/global/redis/RedisUtil.java

package com.daengdaengrun.daengdaengrunbackend.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    /**
     * Redis에서 KEY를 통해 VALUE를 가져오는 메소드
     * @param key Redis 키
     * @return 키에 해당하는 값 (없으면 null)
     */
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 유효 시간(초)을 지정하여 Redis에 KEY-VALUE 쌍을 저장하는 메소드
     * @param key Redis 키
     * @param value 저장할 값
     * @param durationSeconds 유효 시간 (초 단위)
     */
    public void setDataExpire(String key, String value, long durationSeconds) {
        Duration expireDuration = Duration.ofSeconds(durationSeconds);
        redisTemplate.opsForValue().set(key, value, expireDuration);
    }

    /**
     * Redis에서 KEY에 해당하는 데이터를 삭제하는 메소드
     * @param key 삭제할 Redis 키
     */
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}