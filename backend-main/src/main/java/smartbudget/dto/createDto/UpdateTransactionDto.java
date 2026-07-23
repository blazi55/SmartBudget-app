package smartbudget.dto.createDto;

import lombok.Data;
import smartbudget.enums.Currency;
import smartbudget.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateTransactionDto {
	private Long categoryId;
	private BigDecimal amount;
	private TransactionType type;
	private LocalDate date;
	private Currency currency;
	private String description;
}
