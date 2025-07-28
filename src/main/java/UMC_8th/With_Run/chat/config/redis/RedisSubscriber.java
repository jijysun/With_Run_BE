package UMC_8th.With_Run.chat.config.redis;

import UMC_8th.With_Run.chat.converter.MessageConverter;
import UMC_8th.With_Run.common.apiResponse.status.ErrorCode;
import UMC_8th.With_Run.common.exception.handler.ChatHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        if (payload.contains("\"isCourse\" : false")){ // contains 로 하는 건 비효율적이다. + API 응답 코드를 바꾸는 게 나을 듯,
            Message msg = objectMapper.readValue(payload, Message.class);

            MessageConverter.toBroadCastMsgDTO()


        }
        else if (payload.contains("\"isCourse\" : true")){
            if (payload.contains("\"isChat\" : true")){ // 산책 코스 - 채팅방 공유

            }
            else if (payload.contains("\"isChat\" : false")){ // 산책 코스 - 1대1 공유
                
            }

            else{
                throw new ChatHandler(ErrorCode.REDIS_CAN_LISTEN_MSG);
            }
        }

    }
}
