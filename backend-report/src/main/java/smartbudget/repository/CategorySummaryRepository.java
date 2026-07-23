package smartbudget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smartbudget.entity.CategorySummary;

import java.util.List;
import java.util.Optional;

public interface CategorySummaryRepository extends JpaRepository<CategorySummary, Long> {

	Optional<CategorySummary> findByUserIdAndCategoryNameAndYearMonth(
			Long userId, String categoryName, String yearMonth);

	List<CategorySummary> findByUserIdAndYearMonthOrderByTotalAmountDesc(Long userId, String yearMonth);

	List<CategorySummary> findByUserIdAndYearMonthBetweenOrderByYearMonthAscTotalAmountDesc(
			Long userId, String from, String to);

	List<CategorySummary> findByUserIdOrderByTotalAmountDesc(Long userId);
}
