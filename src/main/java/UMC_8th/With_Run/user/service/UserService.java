package UMC_8th.With_Run.user.service;

import UMC_8th.With_Run.user.dto.UserRequestDto;
import UMC_8th.With_Run.user.dto.UserResponseDto;
import UMC_8th.With_Run.user.repository.ProfileRepository;
import UMC_8th.With_Run.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
}
