package smartbudget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyReportDto {
	private LocalDate date;
	private BigDecimal income;
	private BigDecimal expenses;
	private BigDecimal balance;
}
