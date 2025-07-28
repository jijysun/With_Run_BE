package UMC_8th.With_Run.chat.converter;

import UMC_8th.With_Run.chat.dto.ChatRequestDTO;
import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.Message;
import UMC_8th.With_Run.course.entity.Course;
import UMC_8th.With_Run.user.entity.Profile;
import UMC_8th.With_Run.user.entity.User;

import java.time.LocalDateTime;

public class MessageConverter {

    public static Message toMessage(User user, Chat chat, ChatRequestDTO.ChattingReqDTO dto, Course course) {

        return Message.builder()
                .user(user)
                .chat(chat)
                .isCourse(false)
                .msg(dto.getMessage())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Message toShareMessage(User user, Chat chat, Course course) {
        return Message.builder()
                .user(user)
                .chat(chat)
                .isCourse(true)
                .msg("산책 코스를 공유하였습니다")
                .courseId(course.getId())
                .courseImage(course.getCourseImage())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static ChatResponseDTO.BroadcastMsgDTO toBroadCastMsgDTO (Long userId, Long chatId, Profile profile, Message message) {
        return ChatResponseDTO.BroadcastMsgDTO.builder()
                .userId(userId)
                .chatId(chatId)
                .userName(profile.getName())
                .userProfileImage(profile.getProfileImage())
                .msg(message.getMsg())
                .isCourse(false)
                .createdAt(message.getCreatedAt())
                .build();
    }

    public static ChatResponseDTO.BroadcastCourseDTO toBroadCastCourseDTO (Long userId, Course course) {
        return ChatResponseDTO.BroadcastCourseDTO.builder()
                .userId(userId)
                .msg("산책 코스를 공유하였습니다")
                .isCourse(true)
                .courseId(course.getId())
                .courseImage(course.getCourseImage())
                .keyword(course.getKeyWord())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
