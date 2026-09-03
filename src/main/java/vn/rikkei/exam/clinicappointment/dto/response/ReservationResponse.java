package vn.rikkei.exam.clinicappointment.dto.response;

import lombok.*;
import vn.rikkei.exam.clinicappointment.model.ReservationRequest;
import vn.rikkei.exam.clinicappointment.model.ReservationStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReservationResponse {
    private String requestId;
    private String userId;
    private String userName;
    private String department;
    private String resourceCode;
    private String resourceDisplayName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer participantCount;
    private String purpose;
    private ReservationStatus status;
    private String decisionNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReservationResponse fromEntity(ReservationRequest request) {
        if (request == null) return null;
        return ReservationResponse.builder()
                .requestId(request.getRequestId())
                .userId(request.getRequester() != null ? request.getRequester().getUserId() : null)
                .userName(request.getRequester() != null ? request.getRequester().getFullName() : null)
                .department(request.getRequester() != null ? request.getRequester().getDepartment() : null)
                .resourceCode(request.getResourceType() != null ? request.getResourceType().getResourceCode() : null)
                .resourceDisplayName(request.getResourceType() != null ? request.getResourceType().getDisplayName() : null)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .participantCount(request.getParticipantCount())
                .purpose(request.getPurpose())
                .status(request.getStatus())
                .decisionNote(request.getDecisionNote())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }

}
