// src/main/java/UMC_8th/With_Run/common/apiResponse/basecode/PinErrorCode.java
package UMC_8th.With_Run.common.apiResponse.basecode; // BaseErrorCode와 같은 패키지 사용

import UMC_8th.With_Run.common.apiResponse.dto.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PinErrorCode implements BaseErrorCode {
    // 예시 에러 코드: HTTP 상태 코드, 코드, 메시지
    PIN_NOT_FOUND(HttpStatus.NOT_FOUND, "PIN_4001", "요청된 핀 중 일부를 찾을 수 없습니다."),
    // 다른 핀 관련 에러 코드가 있다면 여기에 추가

    ; // Enum 상수가 끝나면 세미콜론 필요

    private final HttpStatus httpStatus; // HTTP 상태 코드
    private final String code; // 커스텀 에러 코드 (예: PIN_4001)
    private final String message; // 사용자에게 보여줄 메시지

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}