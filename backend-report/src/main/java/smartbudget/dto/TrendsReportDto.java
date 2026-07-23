package smartbudget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendsReportDto {
	private BigDecimal totalIncome;
	private BigDecimal totalExpenses;
	private BigDecimal netBalance;
	private Long transactionCount;
	private List<MonthlyReportDto> monthlyTrend;
	private List<CategoryReportDto> topCategories;
}
