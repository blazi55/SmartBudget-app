package smartbudget.kafka;

import smartbudget.enitity.Transaction;

public interface TransactionEventPublisher {
	void publishCreated(Transaction transaction);
	void publishUpdated(Transaction transaction);
	void publishDeleted(Transaction transaction);
}
