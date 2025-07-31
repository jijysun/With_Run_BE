package UMC_8th.With_Run.chat.controller;


import UMC_8th.With_Run.chat.dto.ChatRequestDTO;
import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.Message;
import UMC_8th.With_Run.chat.service.ChatService;
import UMC_8th.With_Run.common.apiResponse.StndResponse;
import UMC_8th.With_Run.common.apiResponse.status.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "채팅 API")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate template;

    /// followee = 내가 팔로우
    /// follower = 나를 팔로우!

    @PostMapping("/hello")
    @Operation(summary = "채팅방 생성 API", description = "상대방과 채팅 생성하는 API 입니다. 상대방과 첫 채팅 시에만 호출되고, 이후 다수 초대는 분리하였습니다")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = ChatResponseDTO.CreateChatDTO.class)))
    })
    @Parameters({
            @Parameter(name = "targetId", description = "상대방 사용자 id 입니다, 초대할 사용자 id 입니다.")
    })
    public StndResponse<Object> createChat(@RequestParam("id") Long targetId, HttpServletRequest request) {
        // userId = Jwt로 해결이 되니,
        chatService.createChat(targetId, request);
        return StndResponse.onSuccess(null, SuccessCode.CHAT_CREATE_SUCCESS);
    }

    // 초대할 친구 목록 불러오기
    @GetMapping("/{id}/invite")
    @Operation(summary = "채팅방 초대 친구 목록 불러오기 API", description = "채팅방에 초대할 친구 목록을 확인하는 API 입니다. 다수 초대가 가능하며, 채팅방 ID, 초대 사용자 ID 리스트가 필요합니다! 응답 코드는 기본 성공 코드 입니다!")
    @ApiResponse(responseCode = "SuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    @Parameters({
            @Parameter(name = "id", description = "채팅방 id 입니다, PathVariable 로 부탁드립니다!"),
            @Parameter(name = "userId", description = "초대할 사용자들의 ID 입니다"),
    })
    public StndResponse<List<ChatResponseDTO.GetInviteUserDTO>> getInviteUser(@PathVariable ("id") Long chatId, HttpServletRequest request) {
        List<ChatResponseDTO.GetInviteUserDTO> canInviteUserList = chatService.getInviteUser(chatId, request);
        return StndResponse.onSuccess(canInviteUserList, SuccessCode.GET_INVITE_SUCCESS);
    }

    // 채팅 유저 추가
    @PostMapping("/{id}/invite")
    @Operation(summary = "채팅방 초대 API", description = "채팅방 초대 API 입니다. 딱히 반환할 게 없어 성공 코드만 반활할 예정입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "Test code", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    public StndResponse<Object> inviteUser(@PathVariable("id") Long chatId, @RequestBody ChatRequestDTO.InviteUserReqDTO reqDTO) {
        chatService.inviteUser(chatId, reqDTO);
        return StndResponse.onSuccess(null, SuccessCode.INVITE_SUCCESS); // 초대 성공 코드 만들기
    }

    @PatchMapping("/{chatId}")
    @Operation(summary = "채팅방 이름 변경 API", description = "생성된 채팅방에 대한 이름 변경 API 입니다. 다른 응답할 정보가 없어, 성공 코드만 반환할 예정입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class))) // 성공 DTO Response 클래스
    })
    @Parameters({
            @Parameter(name = "chatId", description = "채팅방 id 입니다."),
            @Parameter(name = "name", description = "바꿀 채팅방 이름입니다.")
    })
    public StndResponse<Object> renameChat(@PathVariable("chatId") Long chatId, @RequestParam("newName") String newName) {
        chatService.renameChat(chatId, newName);
        return StndResponse.onSuccess(null, SuccessCode.RENAME_SUCCESS);
    }

    @GetMapping("/{id}")
    @Operation(summary = "채팅방 진입 API", description = "채팅반 진입 후 이전 메세지 내역 확인 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "test", content = @Content(schema = @Schema(implementation = ChatResponseDTO.ChatHistoryDTO.class)))
    })
    public StndResponse<List<Message>> enterChat(@PathVariable("id") Long chatId) {
        List<Message> messages = chatService.enterChat(chatId);
        return StndResponse.onSuccess(messages, SuccessCode.ENTER_CHAT_SUCCESS);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "채팅방 떠나기 API", description = "참여 채팅방 떠나기 API 입니다. 다른 응답할 정보가 없어, 성공 코드만 반환할 예정입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = Chat.class))) // 성공 DTO Response 클래스
    })
    @Parameters({
            @Parameter(name = "chatId", description = "떠나는 채팅방 id 입니다.")
    })
    public StndResponse<Object> leaveChat(@PathVariable("id") Long chatId, HttpServletRequest request) {
        chatService.leaveChat(chatId, request);
        return StndResponse.onSuccess(null, SuccessCode.LEAVE_CHAT_SUCCESS);
    }

    @GetMapping("")
    @Operation(summary = "채팅방 목록 조회 API", description = "대화를 생성하거나, 초대된 채팅방 리스트 조회 리스트입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = ChatResponseDTO.GetChatListDTO.class))) // 성공 DTO Response 클래스
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다, PathVariable로 주시면 합니다.")
    })
    public StndResponse<List<ChatResponseDTO.GetChatListDTO>> getChatList(HttpServletRequest request) {
        List<ChatResponseDTO.GetChatListDTO> chatListDTO = chatService.getChatList(request);
        return StndResponse.onSuccess(chatListDTO, SuccessCode.GET_LIST_SUCCESS);
    }

    // 메세지 채팅
    @MessageMapping("/{id}/msg")
    @Operation(summary = "메세징 API", description = "실질적인 채팅 API 입니다.")
    @ApiResponse(responseCode = "SuccessCode", content = @Content(schema = @Schema(implementation = ChatResponseDTO.BroadcastMsgDTO.class)))
    public void chattingWithRedis(@DestinationVariable ("id") Long chatId, @Payload ChatRequestDTO.ChattingReqDTO reqDTO) {

        template.convertAndSend("/sub/" + chatId + "/msg" , chatService.chatting(chatId, reqDTO));
//        chatService.chattingWithRedis(chatId, reqDTO);
    }

    @PostMapping("/share")
    @Operation(summary = "산책 코스 공유 API", description = "다수 공유가 가능하며, 채팅방 ID, 초대 사용자 ID 리스트, 산책 코스 id가 필요합니다! 응답 코드는 기본 성공 코드 입니다!")
    @ApiResponse(responseCode = "SuccessCode", content = @Content(schema = @Schema(implementation = ChatResponseDTO.BroadcastCourseDTO.class)))
    public void shareCourse(@RequestBody ChatRequestDTO.ShareReqDTO reqDTO) {
        chatService.shareCourse(reqDTO);
//        chatService.shareCourseWithRedis(reqDTO);
    }

}
