package smartbudget.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {

	public enum EventType {
		CREATED,
		UPDATED,
		DELETED
	}

	private EventType eventType;
	private Long transactionId;
	private Long userId;
	private Long categoryId;
	private String categoryName;
	private BigDecimal amount;
	private String type;
	private LocalDate date;
	private String currency;
	private String description;
	private Instant occurredAt;
}
