package vn.rikkei.exam.clinicappointment.repository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.rikkei.exam.clinicappointment.model.ResourceInventory;
import vn.rikkei.exam.clinicappointment.model.ResourceType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ResourceInventoryRepository extends JpaRepository<ResourceInventory, Long> {

    List<ResourceInventory> getResourceInventoriesByResourceType_ResourceCodeAndAvailableDateIsBetween(String resourceType, LocalDate startDate, LocalDate endDate);

    Optional<ResourceInventory> findByResourceTypeResourceCodeAndAvailableDate(
            @NotBlank(message = "resourceCode không được để trống") String resourceCode, LocalDate currentDate
    );
}
