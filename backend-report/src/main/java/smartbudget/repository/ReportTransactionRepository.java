package smartbudget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smartbudget.entity.ReportTransaction;

import java.time.LocalDate;
import java.util.List;

public interface ReportTransactionRepository extends JpaRepository<ReportTransaction, Long> {

	List<ReportTransaction> findByUserIdAndDateBetweenOrderByDateAsc(
			Long userId, LocalDate from, LocalDate to);

	List<ReportTransaction> findByUserIdOrderByDateAsc(Long userId);
}
