package vn.rikkei.exam.clinicappointment.dto.response;


import lombok.*;
import vn.rikkei.exam.clinicappointment.model.ResourceInventory;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReservationAvailableResponse {
    private String resourceCode;
    private String resourceDisplayName;
    private LocalDate availableDate;
    private Integer availableSlots;

    public static ReservationAvailableResponse fromEntity(ResourceInventory inventory) {
        if (inventory == null) return null;
        return ReservationAvailableResponse.builder()
                .resourceCode(inventory.getResourceType() != null ? inventory.getResourceType().getResourceCode() : null)
                .resourceDisplayName(inventory.getResourceType() != null ? inventory.getResourceType().getDisplayName() : null)
                .availableDate(inventory.getAvailableDate())
                .availableSlots(inventory.getAvailableSlots())
                .build();
    }
}
