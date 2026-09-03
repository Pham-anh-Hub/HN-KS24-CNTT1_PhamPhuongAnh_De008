package vn.rikkei.exam.clinicappointment.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.rikkei.exam.clinicappointment.dto.request.CreateReservationRequest;
import vn.rikkei.exam.clinicappointment.dto.response.ReservationAvailableResponse;
import vn.rikkei.exam.clinicappointment.dto.response.ReservationResponse;
import vn.rikkei.exam.clinicappointment.exception.InsufficientInventoryException;
import vn.rikkei.exam.clinicappointment.exception.InvalidReservationException;
import vn.rikkei.exam.clinicappointment.exception.ResourceNotFoundException;
import vn.rikkei.exam.clinicappointment.model.*;
import vn.rikkei.exam.clinicappointment.repository.AppUserRepository;
import vn.rikkei.exam.clinicappointment.repository.ReservationRequestRepository;
import vn.rikkei.exam.clinicappointment.repository.ResourceInventoryRepository;
import vn.rikkei.exam.clinicappointment.repository.ResourceTypeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationInventoryService {
    private final ResourceInventoryRepository resourceInventoryRepository;
    private final AppUserRepository appUserRepository;
    private final ResourceTypeRepository resourceTypeRepository;
    private final ReservationRequestRepository reservationRequestRepository;


    public List<ReservationAvailableResponse> getReservationAvailable(String resourceType, LocalDate startDate, LocalDate endDate){
        List<ResourceInventory> resourceInventories = resourceInventoryRepository.getResourceInventoriesByResourceType_ResourceCodeAndAvailableDateIsBetween(resourceType, startDate, endDate);
        return resourceInventories.stream().map(ReservationAvailableResponse::fromEntity).toList();
    }
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest req) {
        // 1. Kiểm tra User
        AppUser requester = appUserRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + req.getUserId()));

        // 2. Kiểm tra loại phòng
        ResourceType resourceType = resourceTypeRepository.findById(req.getResourceCode())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại phòng thí nghiệm với mã: " + req.getResourceCode()));

        if (Boolean.FALSE.equals(resourceType.getActive())) {
            throw new InvalidReservationException("Loại phòng " + req.getResourceCode() + " hiện không hoạt động");
        }

        // 3. Kiểm tra sức chứa (Capacity Rules)
        if (req.getParticipantCount() > resourceType.getMaxParticipants()) {
            throw new InvalidReservationException("Số lượng người tham gia (" + req.getParticipantCount() +
                    ") vượt quá sức chứa tối đa của phòng " + resourceType.getResourceCode() + " (" + resourceType.getMaxParticipants() + " người)");
        }

        if ("PRM".equalsIgnoreCase(resourceType.getResourceCode()) && req.getParticipantCount() < 2) {
            throw new InvalidReservationException("Nhóm PREMIUM chỉ dành cho yêu cầu từ 2 người trở lên");
        }

        // 4. Kiểm tra ngày bắt đầu và ngày kết thúc (Duration Rules)
        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new InvalidReservationException("Ngày kết thúc không thể trước ngày bắt đầu");
        }

        long durationDays = ChronoUnit.DAYS.between(req.getStartDate(), req.getEndDate()) + 1;
        if (durationDays > 14) {
            throw new InvalidReservationException("Một yêu cầu tối đa 14 ngày (Đơn đăng ký hiện tại: " + durationDays + " ngày)");
        }

        // 5. Kiểm tra mục đích (Purpose Length Rules)
        String purpose = req.getPurpose() != null ? req.getPurpose().trim() : "";
        if (purpose.length() < 10 || purpose.length() > 200) {
            throw new InvalidReservationException("Mục đích sử dụng phải mô tả rõ từ 10 đến 200 ký tự");
        }

        // 6. Kiểm tra slot tồn kho trên toàn bộ khoảng thời gian
        for (LocalDate date = req.getStartDate(); !date.isAfter(req.getEndDate()); date = date.plusDays(1)) {
            final LocalDate currentDate = date;
            Optional<ResourceInventory> invOpt = resourceInventoryRepository.findByResourceTypeResourceCodeAndAvailableDate(req.getResourceCode(), currentDate);
            if (invOpt.isEmpty() || invOpt.get().getAvailableSlots() <= 0) {
                throw new InsufficientInventoryException("Phòng " + req.getResourceCode() + " đã hết slot trống vào ngày " + currentDate);
            }
        }

        // 7. Tạo đơn ở trạng thái PENDING
        String requestId = "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ReservationRequest reservationRequest = ReservationRequest.builder()
                .requestId(requestId)
                .requester(requester)
                .resourceType(resourceType)
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .participantCount(req.getParticipantCount())
                .purpose(purpose)
                .status(ReservationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ReservationRequest saved = reservationRequestRepository.save(reservationRequest);
        return ReservationResponse.fromEntity(saved);
    }

}
