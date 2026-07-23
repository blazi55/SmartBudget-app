package smartbudget.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import smartbudget.enitity.Transaction;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "false")
public class NoOpTransactionEventPublisher implements TransactionEventPublisher {

	@Override
	public void publishCreated(final Transaction transaction) {
		log.debug("Kafka disabled – skip CREATED for transaction {}", transaction.getId());
	}

	@Override
	public void publishUpdated(final Transaction transaction) {
		log.debug("Kafka disabled – skip UPDATED for transaction {}", transaction.getId());
	}

	@Override
	public void publishDeleted(final Transaction transaction) {
		log.debug("Kafka disabled – skip DELETED for transaction {}", transaction.getId());
	}
}
