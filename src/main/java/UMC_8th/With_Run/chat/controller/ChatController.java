package UMC_8th.With_Run.chat.controller;


import UMC_8th.With_Run.chat.dto.ChatRequestDTO;
import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    
    @PostMapping("")
    @Operation(summary = "채팅방 생성 API", description = "상대방과 채팅 생성하는 API 입니다. 상대방과 첫 채팅 시에만 호출되고, 이후 다수 초대는 분리하였습니다")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = ChatResponseDTO.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다, 현 기준 사용자의 id 입니다."),
            @Parameter(name="targetUserId", description = "상대방 사용자 id 입니다, 초대할 사용자 id 입니다.")
    })
    public void createChat (@RequestBody ChatRequestDTO.CreateChatReqDTO createChatReqDTO) {
//        chatService.createChat(ChatRequestDTO.CreateChatReqDTO createChatReqDTO);
    }

    @GetMapping("/list")
    @Operation (summary = "채팅방 목록 조회 API", description = "대화를 생성하거나, 초대된 대화에 대한 채팅방 리스트 조회 리스트입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = Chat.class))) // 성공 DTO Response 클래스
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다, PathVariable로 주시면 합니다.")
    })
    public void getChatList (@PathVariable Integer userId) {
//        chatService.getChatList (userId);
    }

    @PatchMapping("")
    @Operation (summary = "채팅방 이름 변경 API", description = "생성된 채팅방에 대한 이름 변경 API 입니다.")
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
    
    @DeleteMapping("")
    @Operation (summary = "채팅방 떠나기 API", description = "참여 채팅방 떠나기 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = Chat.class))) // 성공 DTO Response 클래스
    })
    @Parameters({
            @Parameter(name = "", description = "바꿀 채팅방 이름입니다.")
    })
    public void leaveChat (){

    }

}
