package smartbudget.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "user_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatistics {

	@Id
	private Long userId;

	@Column(nullable = false, precision = 14, scale = 2)
	@Builder.Default
	private BigDecimal totalIncome = BigDecimal.ZERO;

	@Column(nullable = false, precision = 14, scale = 2)
	@Builder.Default
	private BigDecimal totalExpenses = BigDecimal.ZERO;

	@Column(nullable = false, precision = 14, scale = 2)
	@Builder.Default
	private BigDecimal netBalance = BigDecimal.ZERO;

	@Column(nullable = false)
	@Builder.Default
	private Long transactionCount = 0L;
}
