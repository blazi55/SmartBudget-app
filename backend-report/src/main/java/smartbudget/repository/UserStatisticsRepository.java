package smartbudget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smartbudget.entity.UserStatistics;

public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Long> {
}
