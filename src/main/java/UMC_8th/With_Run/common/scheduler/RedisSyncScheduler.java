package UMC_8th.With_Run.common.scheduler;

import UMC_8th.With_Run.chat.repository.ChatRepository;
import UMC_8th.With_Run.chat.repository.UserChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.Set;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class RedisSyncScheduler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;


    private static final String DIRTY_USER_CHAT_KEY = "dirty:user_chat_key:";
    private static final String DIRTY_CHAT_KEY = "dirty:chat_key:";

    public void markingDirtyUserChat (String key){
        redisTemplate.opsForSet().add(DIRTY_USER_CHAT_KEY, key);
    }

    public void markingDirtyChat (String key){
        redisTemplate.opsForSet().add(DIRTY_CHAT_KEY, key);
    }


}
