package smartbudget.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
		name = "daily_expenses",
		uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"}),
		indexes = @Index(name = "idx_daily_user_date", columnList = "user_id, date")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyExpense {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private LocalDate date;

	@Column(nullable = false, precision = 14, scale = 2)
	@Builder.Default
	private BigDecimal income = BigDecimal.ZERO;

	@Column(nullable = false, precision = 14, scale = 2)
	@Builder.Default
	private BigDecimal expenses = BigDecimal.ZERO;

	@Column(nullable = false, precision = 14, scale = 2)
	@Builder.Default
	private BigDecimal balance = BigDecimal.ZERO;
}
