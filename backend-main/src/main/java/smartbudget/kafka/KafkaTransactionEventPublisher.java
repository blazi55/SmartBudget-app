package smartbudget.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import smartbudget.enitity.Transaction;
import smartbudget.event.TransactionEvent;

import java.time.Instant;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaTransactionEventPublisher implements TransactionEventPublisher {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final String transactionsTopic;

	public KafkaTransactionEventPublisher(
			final KafkaTemplate<String, String> kafkaTemplate,
			final ObjectMapper objectMapper,
			@Value("${app.kafka.topic.transactions:transactions}") final String transactionsTopic) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
		this.transactionsTopic = transactionsTopic;
	}

	@Override
	public void publishCreated(final Transaction transaction) {
		publish(TransactionEvent.EventType.CREATED, transaction);
	}

	@Override
	public void publishUpdated(final Transaction transaction) {
		publish(TransactionEvent.EventType.UPDATED, transaction);
	}

	@Override
	public void publishDeleted(final Transaction transaction) {
		publish(TransactionEvent.EventType.DELETED, transaction);
	}

	private void publish(final TransactionEvent.EventType eventType, final Transaction transaction) {
		final TransactionEvent event = TransactionEvent.builder()
				.eventType(eventType)
				.transactionId(transaction.getId())
				.userId(transaction.getUser().getId())
				.categoryId(transaction.getCategory().getId())
				.categoryName(transaction.getCategory().getName())
				.amount(transaction.getAmount())
				.type(transaction.getType() != null ? transaction.getType().name() : null)
				.date(transaction.getDate())
				.currency(transaction.getCurrency() != null ? transaction.getCurrency().name() : null)
				.description(transaction.getDescription())
				.occurredAt(Instant.now())
				.build();

		try {
			final String payload = objectMapper.writeValueAsString(event);
			kafkaTemplate.send(transactionsTopic, String.valueOf(transaction.getId()), payload);
			log.info("Published {} event for transaction {}", eventType, transaction.getId());
		} catch (Exception ex) {
			log.error("Failed to publish {} event for transaction {}: {}",
					eventType, transaction.getId(), ex.getMessage());
		}
	}
}
