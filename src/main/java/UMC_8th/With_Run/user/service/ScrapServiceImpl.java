package UMC_8th.With_Run.user.service;

import UMC_8th.With_Run.common.apiResponse.status.ErrorStatus;
import UMC_8th.With_Run.common.exception.GeneralException;
import UMC_8th.With_Run.common.security.jwt.JwtTokenProvider;
import UMC_8th.With_Run.user.dto.UserResponseDto.ScrapItemDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto.ScrapListResultDTO;
import UMC_8th.With_Run.user.entity.Scraps;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.ScrapsRepository;
import UMC_8th.With_Run.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService {

    private final ScrapsRepository scrapsRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ScrapListResultDTO getScrapsByCurrentUser(HttpServletRequest request) {
        Authentication authentication = jwtTokenProvider.extractAuthentication(request);
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WRONG_USER));

        List<Scraps> scraps = scrapsRepository.findAllByUserId(user.getId());

        List<ScrapItemDTO> scrapItems = scraps.stream()
                .map(scrap -> ScrapItemDTO.builder()
                        .courseId(scrap.getCourse().getId())
                        .scrapedAt(scrap.getCreatedAt())
                        .build())
                .toList();

        return ScrapListResultDTO.builder()
                .scrapList(scrapItems)
                .build();
    }
}
