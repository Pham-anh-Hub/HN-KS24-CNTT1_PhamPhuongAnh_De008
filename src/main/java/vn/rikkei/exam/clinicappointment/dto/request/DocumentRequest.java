package vn.rikkei.exam.clinicappointment.dto.request;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DocumentRequest {
    private Long id;
    private MultipartFile file;
}
