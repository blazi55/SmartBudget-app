package smartbudget.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
		name = "report_transactions",
		indexes = {
				@Index(name = "idx_report_user_date", columnList = "user_id, date"),
				@Index(name = "idx_report_user_category", columnList = "user_id, category_name")
		}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportTransaction {

	@Id
	private Long transactionId;

	@Column(nullable = false)
	private Long userId;

	private Long categoryId;

	private String categoryName;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	@Column(nullable = false)
	private String type;

	@Column(nullable = false)
	private LocalDate date;

	private String currency;

	private String description;
}
