package UMC_8th.With_Run.common.apiResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum SuccessCode {

    // Common
    INTERNAL_ERROR (HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5001", "서버 오류입니다. 관리자에게 문의해주세요");
    // INTERNAL_ERROR, 서버 오류

    // User
    // 존재하지 않는 사용자


    // Friend

    // Chat
    

    // Course

    // Map

    // Notice

    // MyPage

    private final HttpStatus status;
    private final String code;
    private final String message;


}
