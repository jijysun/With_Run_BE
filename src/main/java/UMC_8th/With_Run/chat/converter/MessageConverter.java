package UMC_8th.With_Run.chat.converter;

import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.Message;
import UMC_8th.With_Run.course.entity.Course;
import UMC_8th.With_Run.user.entity.Profile;

import java.time.LocalDateTime;

public class MessageConverter {

    public static ChatResponseDTO.BroadcastMsgDTO toBroadCastMsgDTO (Long userId,Profile profile, Message message) {
        return ChatResponseDTO.BroadcastMsgDTO.builder()
                .userId(userId)
                .userName(profile.getName())
                .userProfileImage(profile.getProfileImage())
                .msg(message.getMsg())
                .isCourse(false)
                .createdAt(message.getCreatedAt())
                .build();
    }

    public static ChatResponseDTO.BroadcastCourseDTO toBroadCastCourseDTO (Long userId, Profile profile, Course course) {
        return ChatResponseDTO.BroadcastCourseDTO.builder()
                .userId(userId)
                .userName(profile.getName())
                .userProfileImage(profile.getProfileImage())
                .msg("산책 코스를 공유하였습니다")
                .isCourse(true)
                .courseId(course.getId())
                .courseImage(course.getCourseImage())
                .courseTag(course.getCourseTag())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
