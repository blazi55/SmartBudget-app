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
public class MonthlyReportDto {
	private String yearMonth;
	private BigDecimal income;
	private BigDecimal expenses;
	private BigDecimal balance;
}
