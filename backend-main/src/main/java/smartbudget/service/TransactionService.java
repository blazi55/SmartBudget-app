package smartbudget.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartbudget.dto.TransactionDto;
import smartbudget.dto.createDto.CreateTransactionDto;
import smartbudget.dto.createDto.UpdateTransactionDto;
import smartbudget.enitity.Category;
import smartbudget.enitity.Transaction;
import smartbudget.enitity.User;
import smartbudget.exception.NotFoundException;
import smartbudget.kafka.TransactionEventPublisher;
import smartbudget.mapper.TransactionMapper;
import smartbudget.repository.CategoryRepository;
import smartbudget.repository.TransactionRepository;
import smartbudget.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

	private final TransactionRepository transactionRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final TransactionMapper transactionMapper;
	private final TransactionEventPublisher transactionEventPublisher;

	@Transactional
	public TransactionDto create(final CreateTransactionDto dto) {
		final User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> {
					log.error("User not found by id {}", dto.getUserId());
					throw new NotFoundException("User not found");
				});

		final Category category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> {
					log.error("Category not found by id: {}", dto.getCategoryId());
					throw new NotFoundException("Category not found");
				});

		final Transaction transaction = Transaction.builder()
				.user(user)
				.category(category)
				.amount(dto.getAmount())
				.type(dto.getType())
				.date(dto.getDate())
				.currency(dto.getCurrency())
				.description(dto.getDescription())
				.build();

		final Transaction saved = transactionRepository.save(transaction);
		transactionEventPublisher.publishCreated(saved);
		return transactionMapper.toDto(saved);
	}

	public TransactionDto getById(final Long id) {
		return transactionMapper.toDto(transactionRepository.findById(id)
				.orElseThrow(() -> {
					log.error("Transaction not found by id: {}", id);
					throw new NotFoundException("Transaction not found");
				}));
	}

	public List<TransactionDto> getAll(final Long userId, final Long categoryId) {
		if (userId != null && categoryId != null) {
			return transactionMapper.toDtoList(transactionRepository.findByUserIdAndCategoryId(userId, categoryId));
		}

		if (userId != null) {
			return transactionMapper.toDtoList(transactionRepository.findByUserId(userId));
		}

		if (categoryId != null) {
			return transactionMapper.toDtoList(transactionRepository.findByCategoryId(categoryId));
		}

		return transactionMapper.toDtoList(transactionRepository.findAllWithCategory());
	}

	@Transactional
	public TransactionDto update(final Long id, final UpdateTransactionDto dto) {
		final Transaction transaction = transactionRepository.findById(id)
				.orElseThrow(() -> {
					log.error("Transaction not found by id: {}", id);
					throw new NotFoundException("Transaction not found");
				});

		if (dto.getCategoryId() != null) {
			final Category category = categoryRepository.findById(dto.getCategoryId())
					.orElseThrow(() -> {
						log.error("Category not found by id: {}", dto.getCategoryId());
						throw new NotFoundException("Category not found");
					});
			transaction.setCategory(category);
		}
		if (dto.getAmount() != null) {
			transaction.setAmount(dto.getAmount());
		}
		if (dto.getType() != null) {
			transaction.setType(dto.getType());
		}
		if (dto.getDate() != null) {
			transaction.setDate(dto.getDate());
		}
		if (dto.getCurrency() != null) {
			transaction.setCurrency(dto.getCurrency());
		}
		if (dto.getDescription() != null) {
			transaction.setDescription(dto.getDescription());
		}

		final Transaction saved = transactionRepository.save(transaction);
		transactionEventPublisher.publishUpdated(saved);
		return transactionMapper.toDto(saved);
	}

	@Transactional
	public void delete(final Long id) {
		final Transaction transaction = transactionRepository.findById(id)
				.orElseThrow(() -> {
					log.error("Transaction not found by id: {}", id);
					throw new NotFoundException("Transaction not found");
				});

		transactionEventPublisher.publishDeleted(transaction);
		transactionRepository.delete(transaction);
	}

	@Transactional(readOnly = true)
	public int republishAllEvents() {
		final List<Transaction> transactions = transactionRepository.findAllWithCategoryAndUser();
		transactions.forEach(transactionEventPublisher::publishCreated);
		log.info("Republished {} transaction events", transactions.size());
		return transactions.size();
	}
}
