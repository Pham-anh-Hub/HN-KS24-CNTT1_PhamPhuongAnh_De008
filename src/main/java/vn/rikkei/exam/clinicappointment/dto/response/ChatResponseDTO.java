package vn.rikkei.exam.clinicappointment.dto.response;


import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChatResponseDTO {
    private List<String> content;
    private List<String> sourceDocument;
}
