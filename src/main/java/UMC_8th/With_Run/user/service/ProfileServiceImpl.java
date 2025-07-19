package UMC_8th.With_Run.user.service;

import UMC_8th.With_Run.common.apiResponse.status.ErrorStatus;
import UMC_8th.With_Run.common.exception.GeneralException;
import UMC_8th.With_Run.common.security.jwt.JwtTokenProvider;
import UMC_8th.With_Run.user.dto.UserResponseDto;
import UMC_8th.With_Run.user.entity.Profile;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.ProfileRepository;
import UMC_8th.With_Run.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserResponseDto.ProfileResultDTO getProfileByCurrentUser(HttpServletRequest request){
        Authentication authentication = jwtTokenProvider.extractAuthentication(request);
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WRONG_USER));

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAD_REQUEST));

        return UserResponseDto.ProfileResultDTO.builder()
                .id(profile.getId())
                .userId(user.getId())
                .townId(profile.getTownId())
                .cityId(profile.getCityId())
                .provinceId(profile.getProvinceId())
                .name(profile.getName())
                .gender(profile.getGender())
                .birth(profile.getBirth())
                .breed(profile.getBreed())
                .size(profile.getSize())
                .profileImage(profile.getProfileImage())
                .character(profile.getCharacters())
                .style(profile.getStyle())
                .build();
    }
}
