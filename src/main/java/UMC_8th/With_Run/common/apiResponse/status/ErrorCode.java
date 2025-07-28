package UMC_8th.With_Run.common.apiResponse.status;


import UMC_8th.With_Run.common.apiResponse.basecode.BaseErrorCode;
import UMC_8th.With_Run.common.apiResponse.dto.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseErrorCode {

    // 400번대, Common
    BAD_REQUEST(HttpStatus.BAD_REQUEST , "COMMON4000", "잘못된 요청입니다"),
    WRONG_INPUT(HttpStatus. BAD_REQUEST, "COMMON4001", "잘못된 값이 입력되었습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON4002", "권한이 존재하지 않습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON4004", "찾을 수 없는 페이지 입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED , "COMMON4005", "지원되지 않는 HTTP 프로토콜입니다."),

    // INTERNAL_ERROR, 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5000", "서버 오류입니다."),
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "COMMON5001", "구현되지 않았습니다"),

    // User
    USERNAME_REQUIRED(HttpStatus.BAD_REQUEST, "USER4001", "닉네임은 필수 입력입니다."),
    DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "USER4002", "중복된 닉네임 입니다."),
    MORE_TAG_REQUIRED(HttpStatus.BAD_REQUEST , "USER4003", "최소 3개의 태그가 필요합니다."),
    MORE_STYLE_REQUIRED(HttpStatus.BAD_REQUEST , "USER4004", "최소 2개의 산택 스타일이 필요합니다."),
    WRONG_BREED(HttpStatus.BAD_REQUEST , "USER4005", "잘못된 견종 입니다."),
    WRONG_USER(HttpStatus.BAD_REQUEST , "USER4006", "잘못된 사용자 입니다."),
    INVALID_TOKEN (HttpStatus.BAD_REQUEST, "MEMBER4007", "만료된 토큰 입니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),

    // Friend
    BANNED_USER(HttpStatus.BAD_REQUEST , "FRIEND4001", "차단된 사용자 입니다."),
    ALREADY_REPORTED(HttpStatus.BAD_REQUEST , "FRIEND4002", "이미 신고한 사용자 입니다."),
    ALREADY_FOLLOWED(HttpStatus.BAD_REQUEST , "FRIEND4003", "이미 팔로우 한 사용자 입니다."),


    // Chat
    ALREADY_INVITED(HttpStatus.BAD_REQUEST , "CHAT4001", "이미 초대한 사용자 입니다."),
    WRONG_CHAT_NAME(HttpStatus.BAD_REQUEST , "CHAT4002", "잘못된 채팅방 이름 입니다."),
    EMPTY_CHAT_LIST(HttpStatus.BAD_REQUEST, "CHAT4003", "참여하고 있는 채팅방이 없습니다."),
    CHAT_IS_FULL(HttpStatus.BAD_REQUEST, "CHAT4004", "해당 채탕방은 꽉 찼습니다."),
    WRONG_CHAT(HttpStatus.BAD_REQUEST, "CHAT4005", "잘못된 채팅방 입니다."),
    MSG_SERIALIZE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "CHAT5001", "메세지 변환에 실패했습니다"),
    REDIS_CANT_LISTEN_MSG(HttpStatus.INTERNAL_SERVER_ERROR, "CHAT5002", "Redis - 메세지 발행에 실패했습니다"),



    // Course
    WRONG_COURSE(HttpStatus.BAD_REQUEST, "COURSE4001", "잘못된 산책 코스 입니다."),

    // Map
    PIN_NAME_REQUIRED(HttpStatus.BAD_REQUEST , "MAP4001", "핀 이름은 필수 입니다."),
    MORE_PIN_REQUIRED(HttpStatus.BAD_REQUEST , "MAP4002", "최소 2개 이상의 핀이 필요합니다."),
    COURSE_NAME_REQUIRED(HttpStatus.BAD_REQUEST , "MAP4003", "산책 코스 이름은 필수 입니다."),
    COURSE_TAG_REQUIRED(HttpStatus.BAD_REQUEST , "MAP4004", "산책 코스 태그는 필수 입니다."),
    TIME_REQUIRED(HttpStatus.BAD_REQUEST , "MAP4005", "소요 시간은 필수 입니다."),

    // Notice
    // 아직은 필요한 에러 코드가 없어 보입니다.

    // MyPage
    // 아직은 필요한 에러 코드가 없어 보입니다.

    // PAGING 오류
    WRONG_PAGE (HttpStatus.BAD_REQUEST, "PAGE4001", "올바르지 않은 페이지 번호 입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}