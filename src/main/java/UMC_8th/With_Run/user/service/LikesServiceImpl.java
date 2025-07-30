package UMC_8th.With_Run.user.service;

import UMC_8th.With_Run.common.apiResponse.status.ErrorStatus;
import UMC_8th.With_Run.common.exception.GeneralException;
import UMC_8th.With_Run.common.security.jwt.JwtTokenProvider;
import UMC_8th.With_Run.user.dto.UserResponseDto.LikeItemDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto.LikeListResultDTO;
import UMC_8th.With_Run.user.entity.Likes;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.LikesRepository;
import UMC_8th.With_Run.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {

    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LikeListResultDTO getLikesByCurrentUser(HttpServletRequest request) {
        Authentication authentication = jwtTokenProvider.extractAuthentication(request);
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WRONG_USER));

        List<Likes> likes = likesRepository.findAllByUserId(user.getId());

        List<LikeItemDTO> likeItems = likes.stream()
                .map(like -> LikeItemDTO.builder()
                        .courseId(like.getCourse().getId())
                        .likedAt(like.getCreatedAt())
                        .build())
                .toList();

        return LikeListResultDTO.builder()
                .likeList(likeItems)
                .build();
    }
}

