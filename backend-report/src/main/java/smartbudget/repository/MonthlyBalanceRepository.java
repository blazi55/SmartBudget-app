package smartbudget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smartbudget.entity.MonthlyBalance;

import java.util.List;
import java.util.Optional;

public interface MonthlyBalanceRepository extends JpaRepository<MonthlyBalance, Long> {

	Optional<MonthlyBalance> findByUserIdAndYearMonth(Long userId, String yearMonth);

	List<MonthlyBalance> findByUserIdAndYearMonthBetweenOrderByYearMonthAsc(
			Long userId, String from, String to);

	List<MonthlyBalance> findByUserIdOrderByYearMonthAsc(Long userId);
}
