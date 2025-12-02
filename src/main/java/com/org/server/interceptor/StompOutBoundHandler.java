package com.org.server.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class StompOutBoundHandler implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        GenericMessage genericMessage=(GenericMessage) message.getHeaders().get("simpConnectMessage");
        StompHeaderAccessor accessor=StompHeaderAccessor.wrap(message);

        if(genericMessage!=null) {
            MessageHeaders messageHeaders=genericMessage.getHeaders();
            Map<String,Object> maps=(Map)messageHeaders.get("nativeHeaders");
            StompHeaderAccessor newAccessor = StompHeaderAccessor.create(StompCommand.CONNECTED);
            newAccessor.copyHeaders(accessor.toMap());
            newAccessor.setNativeHeader("x-custom", "value");
            maps.entrySet().stream().forEach(x->{
                List<String> list=(List<String>) x.getValue();
                //클라이언트에게 heartbeat를 이렇게 하자고 협상하는과정. 실질적으로 hearbeat를 저기에 설정한값으로 나가는건아니고
                //걍 알려줘서 협상한다 생각하면됨. 물론 백엔드는 websocketconfig에 설정해둔값에따라서 알아서 나간다.
                //즉 알려주는 데이터랑 백엔드 설정이 달라도 되긴하는대 그건 테스트환경이고 실제론 일치시켜주자.
                //앞의 값이 server가 쏘는 주기(ms단위) 뒤값이 클라이언트가 쏘는값.
                 if(x.getKey().equals("heart-beat")){
                    newAccessor.setNativeHeader(x.getKey(),String.join(",",List.of("30000","0")));
                }
                else {
                    newAccessor.setNativeHeader(x.getKey(), String.join(",", list));
                }
            });
            return MessageBuilder.createMessage(message.getPayload(),newAccessor.getMessageHeaders());
        }

        return message;
    }
}
