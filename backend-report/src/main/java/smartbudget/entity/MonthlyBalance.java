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
import java.time.YearMonth;

@Entity
@Table(
		name = "monthly_balance",
		uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "year_month"}),
		indexes = @Index(name = "idx_monthly_user", columnList = "user_id, year_month")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyBalance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(name = "year_month", nullable = false, length = 7)
	private String yearMonth;

	@Column(nullable = false, precision = 14, scale = 2)
	@Builder.Default
	private BigDecimal income = BigDecimal.ZERO;

	@Column(nullable = false, precision = 14, scale = 2)
	@Builder.Default
	private BigDecimal expenses = BigDecimal.ZERO;

	@Column(nullable = false, precision = 14, scale = 2)
	@Builder.Default
	private BigDecimal balance = BigDecimal.ZERO;

	public static String format(YearMonth yearMonth) {
		return yearMonth.toString();
	}
}
