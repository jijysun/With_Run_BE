package UMC_8th.With_Run.chat.controller;


import UMC_8th.With_Run.chat.dto.ChatRequestDTO;
import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.service.ChatService;
import UMC_8th.With_Run.common.apiResponse.StndResponse;
//import UMC_8th.With_Run.common.apiResponse.code.SuccessCode;
import UMC_8th.With_Run.common.apiResponse.status.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "채팅 API")
public class ChatController {
    

    private final ChatService chatService;
    
    @GetMapping("/test")
    @Operation(summary = "응답 확인용 테스트 API", description = "T E S T")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", description = "성공", content = @Content(schema = @Schema(implementation = ChatResponseDTO.TestDTO.class))),
            @ApiResponse(responseCode = "TestSuccessCode", description = "실패",content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    public StndResponse<ChatResponseDTO.TestDTO> test() {
        ChatResponseDTO.TestDTO test = ChatResponseDTO.TestDTO.builder()
                .test("asdf")
                .testCode(1)
                .build();
//        return StndResponse.onSuccess(test, SuccessCode.REQUEST_SUCCESS);
        return StndResponse.onSuccess(test, SuccessCode.INQUIRY_SUCCESS);
    }
    
    @PostMapping("/hello")
    @Operation(summary = "채팅방 생성 API", description = "상대방과 채팅 생성하는 API 입니다. 상대방과 첫 채팅 시에만 호출되고, 이후 다수 초대는 분리하였습니다")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = ChatResponseDTO.createChatDTO.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다, 현 기준 사용자의 id 입니다."),
            @Parameter(name="targetUserId", description = "상대방 사용자 id 입니다, 초대할 사용자 id 입니다.")
    })
    public void createChat (@RequestBody ChatRequestDTO.CreateChatReqDTO createChatReqDTO) {
//        chatService.createChat(ChatRequestDTO.CreateChatReqDTO createChatReqDTO);
    }

    @PatchMapping("")
    @Operation (summary = "채팅방 이름 변경 API", description = "생성된 채팅방에 대한 이름 변경 API 입니다. 다른 응답할 정보가 없어, 성공 코드만 반환할 예정입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = Chat.class))) // 성공 DTO Response 클래스
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다."),
            @Parameter(name = "chatId", description = "채팅방 id 입니다."),
            @Parameter(name = "name", description = "바꿀 채팅방 이름입니다.")
    })
    public void renameChat (@RequestBody ChatRequestDTO.RenameChatDTO renameChatDTO) {
//        chatService.renameChat(renameChatDTO);
    }

    @GetMapping ("/{id}")
    @Operation (summary = "채팅방 진입 API", description = "채팅반 진입 후 이전 내역 확인 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "test", content = @Content(schema = @Schema(implementation = ChatResponseDTO.chatHistoryDTO.class)))
    })
    @Parameters({
            @Parameter(name = "chatId", description = "진입 채팅방 Id 입니다.")
    })
    public void enterChat (@PathVariable("id") Long chatId){
//        chatService.enterChat(chatId);
    }

    // 채팅 유저 추가
    @PostMapping("/{id}")
    @Operation(summary = "채팅방 초대 API", description = "채팅방 초대 API 입니다. 딱히 반환할 게 없어 성공 코드만 반활할 예정입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "Test code", content = @Content(schema = @Schema(implementation = Chat.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "초대할 사용자 id 입니다.")
    })
    public void inviteUser(@PathVariable ("id") Long userId){

    }

    @DeleteMapping("{id}")
    @Operation (summary = "채팅방 떠나기 API", description = "참여 채팅방 떠나기 API 입니다. 다른 응답할 정보가 없어, 성공 코드만 반환할 예정입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = Chat.class))) // 성공 DTO Response 클래스
    })
    @Parameters({
            @Parameter(name = "chatId", description = "떠나는 채팅방 id 입니다.")
    })
    public void leaveChat (@PathVariable ("id") Long chatId) {
        // chatService.leaveChat (@PathVariable Long chatId)
    }

    @GetMapping("/list/{id}")
    @Operation (summary = "채팅방 목록 조회 API", description = "대화를 생성하거나, 초대된 채팅방 리스트 조회 리스트입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = ChatResponseDTO.getChatListDTO.class))) // 성공 DTO Response 클래스
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다, PathVariable로 주시면 합니다.")
    })
    public StndResponse<ChatResponseDTO.getChatListDTO> getChatList (@PathVariable ("id") Long userId) {
        ChatResponseDTO.getChatListDTO dto = new ChatResponseDTO.getChatListDTO();
        // Chat -> Dto
        List<Chat> chatList = chatService.getChatList(userId);
        return StndResponse.onSuccess(dto, SuccessCode.INQUIRY_SUCCESS);
    }


    // 메세지 채팅
    @MessageMapping("/msg")
    @Operation(summary = "메세지 보내기", description = "실질적인 채팅 API 입니다.")
    public void chatting (){
        
    }


}
