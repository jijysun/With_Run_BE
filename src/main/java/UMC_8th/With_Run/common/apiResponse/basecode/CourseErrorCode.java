// src/main/java/UMC_8th/With_Run/common/apiResponse/basecode/CourseErrorCode.java
package UMC_8th.With_Run.common.apiResponse.basecode;

import UMC_8th.With_Run.common.apiResponse.dto.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CourseErrorCode implements BaseErrorCode {
    // 코스 관련 에러 코드를 여기에 정의합니다.
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_4001", "해당 코스를 찾을 수 없습니다."),
    // 필요한 다른 코스 에러 코드들을 추가할 수 있습니다.
    // 예: COURSE_ALREADY_EXISTS(HttpStatus.CONFLICT, "COURSE_4002", "이미 존재하는 코스 이름입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

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
                // .httpStatus(httpStatus) // ErrorReasonDTO에 httpStatus 필드가 있다면 추가
                .build();
    }
}