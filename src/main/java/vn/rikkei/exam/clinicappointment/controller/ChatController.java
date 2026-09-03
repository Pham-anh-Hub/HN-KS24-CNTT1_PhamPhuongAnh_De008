package vn.rikkei.exam.clinicappointment.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.rikkei.exam.clinicappointment.dto.request.ChatRequest;
import vn.rikkei.exam.clinicappointment.dto.response.ChatResponseDTO;
import vn.rikkei.exam.clinicappointment.service.rag.ChatService;

@RestController
@RequestMapping("/api/v1/ai/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponseDTO> chat(@RequestBody ChatRequest chatRequest) {
        ChatResponseDTO response = chatService.handleChat(chatRequest);
        return ResponseEntity.ok(response);
    }


}
