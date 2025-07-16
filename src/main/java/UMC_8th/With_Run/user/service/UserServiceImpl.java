package UMC_8th.With_Run.user.service;

import UMC_8th.With_Run.common.apiResponse.status.ErrorStatus;
import UMC_8th.With_Run.common.security.jwt.JwtTokenProvider;
import UMC_8th.With_Run.user.dto.UserRequestDto.LoginRequestDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto;
import UMC_8th.With_Run.user.dto.UserResponseDto.LoginResultDTO;
import UMC_8th.With_Run.user.entity.Role;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.ProfileRepository;
import UMC_8th.With_Run.user.repository.UserRepository;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserResponseDto.LoginResultDTO login(LoginRequestDTO request) {

        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    // 없으면 회원가입 (가입 처리)
                    User newUser = User.builder()
                            .email(request.getEmail())
                            .naverId(request.getNaverId())
                            .build();
                    return userRepository.save(newUser);
                });

        // 일단 패스워드 없이 이메일로만 로그인
        /*
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        */

        String role = request.getEmail().equals("admin@naver.com") ? "ROLE_ADMIN" : "ROLE_USER";

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null,
                Collections.singleton(new SimpleGrantedAuthority(role))
        );

        String accessToken = jwtTokenProvider.generateToken(authentication);

        return LoginResultDTO.builder()
                .memberId(user.getId())
                .accessToken(accessToken)
                .build();
    }
}

