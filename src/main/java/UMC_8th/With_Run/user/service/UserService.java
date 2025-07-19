package UMC_8th.With_Run.user.service;

import UMC_8th.With_Run.user.dto.UserRequestDto.LoginRequestDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto;

public interface UserService {
    UserResponseDto.LoginResultDTO login(LoginRequestDTO loginRequestDTO);
}
