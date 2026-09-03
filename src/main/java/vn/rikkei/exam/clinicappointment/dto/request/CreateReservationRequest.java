
package vn.rikkei.exam.clinicappointment.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReservationRequest {

    @NotBlank(message = "userId không được để trống")
    private String userId;

    @NotBlank(message = "resourceCode không được để trống")
    private String resourceCode;

    @NotNull(message = "startDate không được để trống")
    private LocalDate startDate;

    @NotNull(message = "endDate không được để trống")
    private LocalDate endDate;

    @NotNull(message = "participantCount không được để trống")
    @Min(value = 1, message = "Số lượng người tham gia tối thiểu là 1")
    private Integer participantCount;

    @NotBlank(message = "Mục đích không được để trống")
    @Size(min = 10, max = 200, message = "Mục đích sử dụng phải mô tả từ 10 đến 200 ký tự")
    private String purpose;
}
