package vn.rikkei.exam.clinicappointment.tool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import vn.rikkei.exam.clinicappointment.dto.request.CreateReservationRequest;
import vn.rikkei.exam.clinicappointment.dto.response.ReservationAvailableResponse;
import vn.rikkei.exam.clinicappointment.dto.response.ReservationResponse;
import vn.rikkei.exam.clinicappointment.service.ReservationInventoryService;
import vn.rikkei.exam.clinicappointment.service.rag.RAGService;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationTools {
    private final ReservationInventoryService reservationService;
    private final RAGService ragService;

    @Tool(description = "Tra cứu số slot khả dụng của nhóm lịch khám (STD hoặc PRM) trong khoảng thời gian từ startDate đến endDate (định dạng YYYY-MM-DD).")
    public List<ReservationAvailableResponse> checkReservationAvailability(String resourceCode, String startDate, String endDate) {
        log.info("Tool called: checkResourceAvailability(resourceCode={}, startDate={}, endDate={})", resourceCode, startDate, endDate);
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return reservationService.getReservationAvailable(resourceCode, start, end);
    }

    @Tool(description = "Tạo đơn đặt lịch khám mới cho người dùng. Yêu cầu đầy đủ: userId, resourceCode (STD hoặc PRM), startDate (YYYY-MM-DD), endDate (YYYY-MM-DD), participantCount, purpose. thiếu bất kỳ trường nào phải trả thông báo yêu cầu người dùng bổ sung 1 cách lịch sự, thân thiện")
    public ReservationResponse createReservationRequest(String userId, String resourceCode, String startDate, String endDate, Integer participantCount, String purpose) {
        log.info("Tool called: createReservationRequest(userId={}, resourceCode={}, startDate={}, endDate={}, participantCount={}, purpose={})",
                userId, resourceCode, startDate, endDate, participantCount, purpose);

        CreateReservationRequest req = CreateReservationRequest.builder()
                .userId(userId)
                .resourceCode(resourceCode)
                .startDate(LocalDate.parse(startDate))
                .endDate(LocalDate.parse(endDate))
                .participantCount(participantCount)
                .purpose(purpose)
                .build();

        return reservationService.createReservation(req);
    }

}
