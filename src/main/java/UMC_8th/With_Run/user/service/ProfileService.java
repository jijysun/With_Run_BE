package UMC_8th.With_Run.user.service;

import UMC_8th.With_Run.user.dto.UserRequestDto.BreedProfileRequestDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface ProfileService {
    UserResponseDto.ProfileResultDTO getProfileByCurrentUser(HttpServletRequest request);
    BreedProfileRequestDTO createBreedProfile(BreedProfileRequestDTO requestDTO, HttpServletRequest request);
}
