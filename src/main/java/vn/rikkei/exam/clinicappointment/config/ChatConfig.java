package vn.rikkei.exam.clinicappointment.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(50)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory){
        return chatClientBuilder
                .defaultSystem("""
                        Bạn là Trợ lý AI thông minh chuyên hỗ trợ Đặt lịch khám bệnh.
                Bạn có khả năng tự động thực thi các công cụ (Tools) sau để hỗ trợ người dùng:
                1. Tra cứu lịch khám khả dụng theo ngày (`checkReservationAvailability`).
                2. Tạo đơn đặt phòng thí nghiệm mới (`createReservationRequest`).
                Quy tắc phục vụ:
                - Nếu thông tin người dùng cung cấp thiếu cho việc tạo mới lịch khám (ví dụ thiếu tên, ngày bắt đầu/kết thúc, số người, hoặc mục đích), hãy hỏi lại rõ ràng trước khi tạo đơn.
                - Trả lời lịch sự, chuyên nghiệp, ngắn gọn và chính xác.
                """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
