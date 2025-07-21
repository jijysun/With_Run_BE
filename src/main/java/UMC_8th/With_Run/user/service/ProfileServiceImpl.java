package UMC_8th.With_Run.user.service;

import UMC_8th.With_Run.common.apiResponse.status.ErrorStatus;
import UMC_8th.With_Run.common.exception.GeneralException;
import UMC_8th.With_Run.common.security.jwt.JwtTokenProvider;
import UMC_8th.With_Run.user.dto.UserRequestDto.BreedProfileRequestDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto;
import UMC_8th.With_Run.user.entity.Profile;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.ProfileRepository;
import UMC_8th.With_Run.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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

    @Override
    public BreedProfileRequestDTO createBreedProfile(BreedProfileRequestDTO requestDTO, HttpServletRequest request) {
        Authentication authentication = jwtTokenProvider.extractAuthentication(request);
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WRONG_USER));

        String charactersJson = convertToJson(requestDTO.getCharacters());
        String styleJson = convertToJson(requestDTO.getStyle());

        Profile profile = Profile.builder()
                .user(user)
                .townId(requestDTO.getTownId())
                .cityId(requestDTO.getCityId())
                .provinceId(requestDTO.getProvinceId())
                .name(requestDTO.getName())
                .gender(requestDTO.getGender())
                .birth(requestDTO.getBirth())
                .breed(requestDTO.getBreed())
                .size(requestDTO.getSize())
                .profileImage(requestDTO.getProfileImage())
                .characters(charactersJson)
                .style(styleJson)
                .introduction(requestDTO.getIntroduction())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        profileRepository.save(profile);
        return requestDTO;
    }

    private String convertToJson(List<String> list) {
        try {
            return new ObjectMapper().writeValueAsString(list != null ? list : Collections.emptyList());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }
    }

}
