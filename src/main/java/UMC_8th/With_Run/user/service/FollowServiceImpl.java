package UMC_8th.With_Run.user.service;

import UMC_8th.With_Run.common.apiResponse.status.ErrorStatus;
import UMC_8th.With_Run.common.exception.GeneralException;
import UMC_8th.With_Run.common.security.jwt.JwtTokenProvider;
import UMC_8th.With_Run.user.dto.UserResponseDto.FollowItemDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto.FollowerItemDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto.FollowerListResultDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto.FollowingListResultDTO;
import UMC_8th.With_Run.user.entity.Follow;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.FollowRepository;
import UMC_8th.With_Run.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public FollowingListResultDTO getFollowingList(HttpServletRequest request) {
        Authentication authentication = jwtTokenProvider.extractAuthentication(request);
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WRONG_USER));

        List<Follow> followings = followRepository.findAllByUserId(user.getId());

        List<FollowItemDTO> result = followings.stream()
                .map(follow -> FollowItemDTO.builder()
                        .targetUserId(follow.getTargetUser().getId())
                        .build())
                .toList();

        return FollowingListResultDTO.builder()
                .followings(result)
                .build();
    }

    @Override
    public FollowerListResultDTO getFollowerList(HttpServletRequest request) {
        Authentication authentication = jwtTokenProvider.extractAuthentication(request);
        String email = authentication.getName();

        User me = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WRONG_USER));

        List<Follow> followers = followRepository.findAllByTargetUserId(me.getId());

        List<FollowerItemDTO> result = followers.stream()
                .map(f -> FollowerItemDTO.builder()
                        .userId(f.getUser().getId())
                        .build())
                .toList();

        return FollowerListResultDTO.builder()
                .followers(result)
                .build();
    }
}

