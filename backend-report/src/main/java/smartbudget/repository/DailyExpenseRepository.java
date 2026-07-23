package smartbudget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smartbudget.entity.DailyExpense;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyExpenseRepository extends JpaRepository<DailyExpense, Long> {

	Optional<DailyExpense> findByUserIdAndDate(Long userId, LocalDate date);

	List<DailyExpense> findByUserIdAndDateBetweenOrderByDateAsc(Long userId, LocalDate from, LocalDate to);
}
