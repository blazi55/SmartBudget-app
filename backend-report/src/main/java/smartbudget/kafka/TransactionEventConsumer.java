package smartbudget.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import smartbudget.event.TransactionEvent;
import smartbudget.service.AggregationService;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class TransactionEventConsumer {

	private final AggregationService aggregationService;
	private final ObjectMapper objectMapper;

	@KafkaListener(
			topics = "${app.kafka.topic.transactions:transactions}",
			groupId = "${spring.kafka.consumer.group-id:smart-budget-report-group}"
	)
	public void consume(final String payload) {
		try {
			final TransactionEvent event = objectMapper.readValue(payload, TransactionEvent.class);
			log.info("Received transaction event: {} for id {}",
					event.getEventType(), event.getTransactionId());
			aggregationService.handle(event);
		} catch (Exception ex) {
			log.error("Failed to process transaction event payload: {}", ex.getMessage(), ex);
		}
	}
}
