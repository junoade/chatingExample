package com.example.chatserver.common.configs;

import com.example.chatserver.chat.service.RedisPubSubService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    /**
     * 연결 기본 객체
     * @return
     */
    @Bean
    @Qualifier("chatRedisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);

        // redis pub/sub에서는 특정 데이터베이스에 의존적이지 않는다
        // config.setDatabase(0);
        // 커넥션 객체를 여러 개 만들 순 있긴 하다
        return new LettuceConnectionFactory(config);
    }

    /**
     * publish
     * 일반적으로는 StringRedisTemplate 대신 RedisTemplate<K, V> 를 사용
     * StringRedisTemplate 사용 이유 : 단순 메시지 전파 목적
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    @Qualifier("chatRedisPub")
    public StringRedisTemplate stringRedisTemplate(@Qualifier("chatRedisConnectionFactory")
                                                       RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    /**
     * subscribe
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("chatRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory,
            MessageListenerAdapter listenerAdapter
    ){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        // 처리하는 객체 필요
        container.addMessageListener(listenerAdapter, new PatternTopic("chat"));
        return container;
    }

    // 수신된 메시지를 처리
    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisPubSubService redisPubSubService) {
        // 수신하고 처리할 객체와 수신된 메시지를 처리할 메소드를 지정
        return new MessageListenerAdapter(redisPubSubService, "onMessage");
    }



}
