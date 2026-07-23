package smartbudget.dto;

import lombok.Data;
import smartbudget.enums.Currency;
import smartbudget.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionDto {
	private Long id;
	private Long userId;
	private BigDecimal amount;
	private TransactionType type;
	private LocalDate date;
	private Currency currency;
	private String description;
	private CategoryDto categoryDto;
}
