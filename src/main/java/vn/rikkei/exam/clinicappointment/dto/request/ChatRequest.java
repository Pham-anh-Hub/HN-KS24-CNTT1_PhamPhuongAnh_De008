package vn.rikkei.exam.clinicappointment.dto.request;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChatRequest {
    private String message;
    private String conversationId;
}
