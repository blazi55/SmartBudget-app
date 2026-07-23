package smartbudget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import smartbudget.enitity.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByUserId(Long userId);

	List<Transaction> findByCategoryId(Long categoryId);

	List<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId);

	@Query("SELECT t FROM Transaction t JOIN FETCH t.category")
	List<Transaction> findAllWithCategory();

	@Query("SELECT t FROM Transaction t JOIN FETCH t.category JOIN FETCH t.user")
	List<Transaction> findAllWithCategoryAndUser();
}
