package smartbudget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryReportDto {
	private String categoryName;
	private String yearMonth;
	private BigDecimal totalAmount;
	private Long transactionCount;
}
