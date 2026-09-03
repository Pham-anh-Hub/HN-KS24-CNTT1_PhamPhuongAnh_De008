package vn.rikkei.exam.clinicappointment.service.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import vn.rikkei.exam.clinicappointment.dto.request.ChatRequest;
import vn.rikkei.exam.clinicappointment.dto.response.ChatResponseDTO;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    /**
     * Xử lý hội thoại Chat RAG AI an toàn: Tự động trích xuất phản hồi,
     * định dạng content thành danh sách các dòng và trích dẫn nguồn tài liệu.
     */
    public ChatResponseDTO handleChat(ChatRequest chatRequest) {
        try {
            // Lấy phản hồi từ AI theo prompt
            ChatResponse response = chatClient.prompt()
                    .user(chatRequest.getMessage())
                    .advisors(a -> a.param("chat_memory_conversation_id", chatRequest.getConversationId()))
                    .advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                    .call()
                    .chatResponse();

            List<String> contentLines = extractContentLines(response);
            List<String> sourceDocs = extractSourceDocuments(response);

            return ChatResponseDTO.builder()
                    .content(contentLines)
                    .sourceDocument(sourceDocs)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi khi xử lý hội thoại: {}", e.getMessage(), e);
            return ChatResponseDTO.builder()
                    .content(List.of("Đã xảy ra lỗi khi kết nối với trợ lý AI: " + e.getMessage()))
                    .sourceDocument(List.of())
                    .build();
        }
    }

    // Tách chuỗi phản hồi từ AI thành danh sách các dòng cho định dạng JSON

    private List<String> extractContentLines(ChatResponse response) {
        // check rỗng/trống từ phản hồi của AI
        if(response == null || response.getResult() == null || response.getResult().getOutput() == null){
            return List.of("Không có nội dung phản hồi");
        }

        // Phản hồi khác rỗng/có phản hồi --> lấy về nội dung
        String rawText = response.getResult().getOutput().getText();
        if (rawText == null || rawText.isBlank()) {
            return List.of("Không có nội dung phản hồi.");
        }

        return Arrays.stream(rawText.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
    }

    // Trích xuất danh sách trích dẫn nguồn từ Metadata của ChatResponse
//    @SuppressWarnings("unchecked")
    private List<String> extractSourceDocuments(ChatResponse response) {
        if (response == null || response.getMetadata() == null) {
            return List.of();
        }

        List<Document> docs = response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);
        if (docs == null || docs.isEmpty()) {
            return List.of();
        }

        return docs.stream()
                .map(this::formatDocumentSource)
                .toList();
    }

    /**
     * Định dạng ngắn gọn từng trích dẫn gồm tên tệp và đoạn vắn tắt (tối đa 180 ký tự)
     */
    private String formatDocumentSource(Document doc) {
        String fileName = (String) doc.getMetadata().getOrDefault("file_name", "Quy chế SmartHub");
        String text = doc.getText() != null ? doc.getText().trim().replaceAll("\\s+", " ") : "";
        if (text.length() > 180) {
            text = text.substring(0, 180) + "...";
        }
        return fileName + ": " + text;
    }
}
