package vn.rikkei.exam.clinicappointment.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.rikkei.exam.clinicappointment.model.ReservationRequest;
public interface ReservationRequestRepository extends JpaRepository<ReservationRequest, String> { }
