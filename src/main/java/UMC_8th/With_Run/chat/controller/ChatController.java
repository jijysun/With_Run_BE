package UMC_8th.With_Run.chat.controller;


import UMC_8th.With_Run.chat.dto.ChatRequestDTO;
import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.service.ChatService;
import UMC_8th.With_Run.chat.service.impl.MessageService;
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
    private final MessageService messageService;

    /// followee = 내가 팔로우
    /// follower = 나를 팔로우!

    @GetMapping("")
    @Operation(summary = "채팅방 목록 조회 API", description = "대화를 생성하거나, 초대된 채팅방 리스트 조회 리스트입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "CHAT2006", content = @Content(schema = @Schema(implementation = ChatResponseDTO.GetChatListDTO.class))) // 성공 DTO Response 클래스
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다, PathVariable로 주시면 합니다.")
    })
    public StndResponse<List<ChatResponseDTO.GetChatListDTO>> getChatList(HttpServletRequest request) {
        List<ChatResponseDTO.GetChatListDTO> chatListDTO = chatService.getChatList(request);
        return StndResponse.onSuccess(chatListDTO, SuccessCode.GET_LIST_SUCCESS);
    }

    @PostMapping("/hello")
    @Operation(summary = "채팅방 생성 API", description = "상대방과 채팅 생성하는 API 입니다. 상대방과 첫 채팅 시에만 호출되고, 이후 다수 초대는 분리하였습니다")
    @ApiResponses({
            @ApiResponse(responseCode = "CHAT2000", content = @Content(schema = @Schema(implementation = ChatResponseDTO.CreateChatDTO.class)))
    })
    @Parameters({
            @Parameter(name = "targetId", description = "상대방 사용자 id 입니다, 초대할 사용자 id 입니다. 파라미터 입니다")
    })
    public StndResponse<Object> createChat(@RequestParam("id") Long targetId, HttpServletRequest request) {
        // userId = Jwt로 해결이 되니,
        chatService.createChat(targetId, request);
        return StndResponse.onSuccess(null, SuccessCode.CHAT_CREATE_SUCCESS);
    }

    @PatchMapping("/{chatId}/rename")
    @Operation(summary = "채팅방 이름 변경 API", description = "생성된 채팅방에 대한 이름 변경 API 입니다. 다른 응답할 정보가 없어, 성공 코드만 반환할 예정입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "CHAT2003", content = @Content(schema = @Schema(implementation = ChatResponseDTO.RenameChatDTO.class))) // 성공 DTO Response 클래스
    })
    @Parameters({
            @Parameter(name = "chatId", description = "채팅방 id 입니다."),
            @Parameter(name = "name", description = "바꿀 채팅방 이름입니다.")
    })
    public StndResponse<ChatResponseDTO.RenameChatDTO> renameChat(@PathVariable("chatId") Long chatId, @RequestParam("name") String newName, HttpServletRequest request) {
        ChatResponseDTO.RenameChatDTO renameChatDTO = chatService.renameChat(chatId, newName, request);
        // 변경 이름 & id 반환하기
        return StndResponse.onSuccess(renameChatDTO, SuccessCode.RENAME_SUCCESS);
    }

    // 초대할 친구 목록 불러오기
    @GetMapping("/{chatId}/invite")
    @Operation(summary = "채팅방 초대 친구 목록 불러오기 API", description = "채팅방에 초대할 친구 목록을 확인하는 API 입니다. 다수 초대가 가능하며, 채팅방 ID, 초대 사용자 ID 리스트가 필요합니다! 응답 코드는 기본 성공 코드 입니다!")
    @ApiResponse(responseCode = "CHAT2001", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    @Parameters({
            @Parameter(name = "id", description = "채팅방 id 입니다, PathVariable 로 부탁드립니다!"),
            @Parameter(name = "userId", description = "초대할 사용자들의 ID 입니다"),
    })
    public StndResponse<List<ChatResponseDTO.GetInviteUserDTO>> getInviteUser(@PathVariable ("chatId") Long chatId, HttpServletRequest request) {
        List<ChatResponseDTO.GetInviteUserDTO> canInviteUserList = chatService.getInviteUser(chatId, request);
        return StndResponse.onSuccess(canInviteUserList, SuccessCode.GET_INVITE_SUCCESS);
    }

    // 채팅 사용자 초대
    @PostMapping("/{chatId}/invite")
    @Operation(summary = "채팅방 초대 API", description = "채팅방 초대 API 입니다. 딱히 반환할 게 없어 성공 코드만 반활할 예정입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "CHAT2002", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    public StndResponse<Object> inviteUser(@PathVariable("chatId") Long chatId, @RequestBody ChatRequestDTO.InviteUserReqDTO reqDTO, HttpServletRequest request) {
        chatService.inviteUser(chatId, reqDTO, request);
        return StndResponse.onSuccess(null, SuccessCode.INVITE_SUCCESS); // 초대 성공 코드 만들기
    }

    @GetMapping("/{chatId}")
    @Operation(summary = "채팅방 진입 API", description = "채팅반 진입 후 이전 메세지 내역 확인 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "CHAT2004", content = @Content(schema = @Schema(implementation = ChatResponseDTO.BroadcastMsgDTO.class)))
    })
    public StndResponse<List<ChatResponseDTO.BroadcastMsgDTO>> enterChat(@PathVariable("chatId") Long chatId, HttpServletRequest request) {
        return StndResponse.onSuccess(chatService.enterChat(chatId, request), SuccessCode.ENTER_CHAT_SUCCESS);
    }

    // 메세지 채팅
    @MessageMapping("/{chatId}/msg")
    @Operation(summary = "메세징 API", description = "실질적인 채팅 API 입니다.")
    @ApiResponse(responseCode = "CHAT2008", content = @Content(schema = @Schema(implementation = ChatResponseDTO.BroadcastMsgDTO.class)))
    public void chattingWithRedis(@DestinationVariable ("chatId") Long chatId, @Payload ChatRequestDTO.ChattingReqDTO reqDTO) {
//        template.convertAndSend("/sub/" + chatId + "/msg" , chatService.chatting(chatId, reqDTO));
//        chatService.chattingWithRedis(chatId, reqDTO);
        messageService.chatting(chatId, reqDTO);

    }

    @PostMapping("/share")
    @Operation(summary = "산책 코스 공유 API", description = "다수 공유가 가능하며, 채팅방 ID, 초대 사용자 ID 리스트, 산책 코스 id가 필요합니다! 응답 코드는 기본 성공 코드 입니다!")
    @ApiResponse(responseCode = "CHAT2009", content = @Content(schema = @Schema(implementation = ChatResponseDTO.BroadcastCourseDTO.class)))
    public void shareCourse(@RequestBody ChatRequestDTO.ShareReqDTO reqDTO) {
//        chatService.shareCourse(reqDTO);
//        chatService.shareCourseWithRedis(reqDTO);
        messageService.shareCourse(reqDTO);
    }

    @PatchMapping ("/{chatId}")
    @Operation(summary = "채팅방 떠나기 API", description = "참여 채팅방 화면에서 잠시 떠나는 API 입니다. 읽지 않은 메세지 수 계산을 위한 API 입니다!")
    @ApiResponse(responseCode = "CHAT2005", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    public StndResponse<ChatResponseDTO.RenameChatDTO> leaveChat (@PathVariable ("chatId") Long chatId, HttpServletRequest request){
        chatService.leaveChat(chatId, request);
        return StndResponse.onSuccess(null, SuccessCode.LEAVE_CHAT_SUCCESS);
    }

    @DeleteMapping("{chatId}")
    @Operation(summary = "채팅방 삭제 API", description = "참여 채팅방 삭제 API 입니다. 다른 응답할 정보가 없어, 성공 코드만 반환할 예정입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "CHAT2007", content = @Content(schema = @Schema(implementation = Chat.class))) // 성공 DTO Response 클래스
    })
    @Parameters({
            @Parameter(name = "chatId", description = "떠나는 채팅방 id 입니다.")
    })
    public StndResponse<Object> deleteChat(@PathVariable("chatId") Long chatId, HttpServletRequest request) {
        chatService.deleteChat(chatId, request);
        return StndResponse.onSuccess(null, SuccessCode.LEAVE_CHAT_SUCCESS);
    }

}