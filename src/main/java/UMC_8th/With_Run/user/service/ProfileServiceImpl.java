package UMC_8th.With_Run.user.service;

import UMC_8th.With_Run.common.apiResponse.status.ErrorStatus;
import UMC_8th.With_Run.common.config.s3.S3Uploader;
import UMC_8th.With_Run.common.exception.GeneralException;
import UMC_8th.With_Run.common.security.jwt.JwtTokenProvider;
import UMC_8th.With_Run.map.entity.RegionProvince;
import UMC_8th.With_Run.map.entity.RegionsCity;
import UMC_8th.With_Run.map.entity.RegionsTown;
import UMC_8th.With_Run.user.dto.UserRequestDto.BreedProfileRequestDTO;
import UMC_8th.With_Run.user.dto.UserRequestDto.UpdateProfileDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto;
import UMC_8th.With_Run.user.entity.Profile;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.ProfileRepository;
import UMC_8th.With_Run.user.repository.RegionCityRepository;
import UMC_8th.With_Run.user.repository.RegionProvinceRepository;
import UMC_8th.With_Run.user.repository.RegionTownRepository;
import UMC_8th.With_Run.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final RegionProvinceRepository provinceRepository;
    private final RegionCityRepository cityRepository;
    private final RegionTownRepository townRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final S3Uploader s3Uploader;

    @Override
    public UserResponseDto.ProfileResultDTO getProfileByCurrentUser(HttpServletRequest request){
        Authentication authentication = jwtTokenProvider.extractAuthentication(request);
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WRONG_USER));

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAD_REQUEST));

        RegionProvince province = provinceRepository.findById(profile.getProvinceId())
                .orElseThrow(() -> new IllegalArgumentException("도 정보 없음"));
        RegionsCity city = cityRepository.findById(profile.getCityId())
                .orElseThrow(() -> new IllegalArgumentException("시/군/구 정보 없음"));
        RegionsTown town = townRepository.findById(profile.getTownId())
                .orElseThrow(() -> new IllegalArgumentException("동 정보 없음"));

        return UserResponseDto.ProfileResultDTO.builder()
                .id(profile.getId())
                .userId(user.getId())
                .provinceId(province.getId())
                .provinceName(province.getName())
                .cityId(city.getId())
                .cityName(city.getName())
                .townId(town.getId())
                .townName(town.getName())
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

        RegionProvince province = provinceRepository.findById(requestDTO.getProvinceId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 도 ID"));
        RegionsCity city = cityRepository.findById(requestDTO.getCityId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 시/군/구 ID"));
        RegionsTown town = townRepository.findById(requestDTO.getTownId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 동/읍/면 ID"));

        String charactersJson = convertToJson(requestDTO.getCharacters());
        String styleJson = convertToJson(requestDTO.getStyle());

        Profile profile = Profile.builder()
                .user(user)
                .name(requestDTO.getName())
                .provinceId(province.getId())
                .cityId(city.getId())
                .townId(town.getId())
                .gender(requestDTO.getGender())
                .birth(requestDTO.getBirth())
                .breed(requestDTO.getBreed())
                .size(requestDTO.getSize())
                .characters(charactersJson)
                .style(styleJson)
                .introduction(requestDTO.getIntroduction())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        profileRepository.save(profile);
        return requestDTO;
    }

    @Override
    public UpdateProfileDTO updateProfile(UpdateProfileDTO dto, HttpServletRequest request) {
        Authentication authentication = jwtTokenProvider.extractAuthentication(request);
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WRONG_USER));

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAD_REQUEST));

        // 업데이트
        profile.setTownId(dto.getTownId());
        profile.setCityId(dto.getCityId());
        profile.setProvinceId(dto.getProvinceId());
        profile.setName(dto.getName());
        profile.setGender(dto.getGender());
        profile.setBirth(dto.getBirth());
        profile.setBreed(dto.getBreed());
        profile.setSize(dto.getSize());
        profile.setCharacters(convertToJson(dto.getCharacters()));
        profile.setStyle(convertToJson(dto.getStyle()));
        profile.setIntroduction(dto.getIntroduction());
        profile.setUpdatedAt(LocalDateTime.now());

        profileRepository.save(profile);
        return dto;
    }


    private String convertToJson(List<String> list) {
        try {
            return new ObjectMapper().writeValueAsString(list != null ? list : Collections.emptyList());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }
    }

    public String uploadProfileImage(MultipartFile file, HttpServletRequest request) throws IOException {
        Authentication authentication = jwtTokenProvider.extractAuthentication(request);
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WRONG_USER));

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAD_REQUEST));



        String oldImageUrl = profile.getProfileImage();
        if (oldImageUrl != null && !oldImageUrl.isBlank()) {
            String s3Key = s3Uploader.extractKeyFromUrl(oldImageUrl);
            s3Uploader.fileDelete(s3Key);
        }

        String profileUrl = s3Uploader.upload(file, "profile");
        profile.setProfileImage(profileUrl);
        profile.setUpdatedAt(LocalDateTime.now());

        profileRepository.save(profile);

        return "프로필 사진 업로드에 성공하였습니다.";
    }


}
