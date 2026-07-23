package smartbudget.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import smartbudget.dto.TransactionDto;
import smartbudget.dto.createDto.CreateTransactionDto;
import smartbudget.enitity.Category;
import smartbudget.enitity.Transaction;
import smartbudget.enitity.User;
import smartbudget.enums.Currency;
import smartbudget.enums.TransactionType;
import smartbudget.exception.NotFoundException;
import smartbudget.kafka.TransactionEventPublisher;
import smartbudget.mapper.TransactionMapper;
import smartbudget.repository.CategoryRepository;
import smartbudget.repository.TransactionRepository;
import smartbudget.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private TransactionMapper transactionMapper;

	@Mock
	private TransactionEventPublisher transactionEventPublisher;

	@InjectMocks
	private TransactionService transactionService;

	private CreateTransactionDto dto;
	private User user;
	private Category category;
	private Transaction transaction;
	private TransactionDto transactionDto;

	@BeforeEach
	void setup() {
		dto = new CreateTransactionDto();
		dto.setUserId(1L);
		dto.setCategoryId(2L);
		dto.setAmount(BigDecimal.valueOf(100));
		dto.setType(TransactionType.EXPENSE);
		dto.setDate(LocalDate.now());
		dto.setCurrency(Currency.USD);
		dto.setDescription("Food");

		user = User.builder().id(1L).build();
		category = Category.builder().id(2L).build();

		transaction = Transaction.builder()
				.user(user)
				.category(category)
				.amount(dto.getAmount())
				.type(dto.getType())
				.date(dto.getDate())
				.currency(dto.getCurrency())
				.description(dto.getDescription())
				.build();

		transactionDto = new TransactionDto();
	}

	// =========================
	// CREATE
	// =========================

	@Test
	void shouldCreateTransaction() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
		when(transactionRepository.save(any())).thenReturn(transaction);
		when(transactionMapper.toDto(any())).thenReturn(transactionDto);

		TransactionDto result = transactionService.create(dto);

		assertNotNull(result);
		verify(transactionRepository).save(any());
	}

	@Test
	void shouldThrowWhenUserNotFound() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class,
				() -> transactionService.create(dto));
	}

	@Test
	void shouldThrowWhenCategoryNotFound() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class,
				() -> transactionService.create(dto));
	}

	// =========================
	// GET BY ID
	// =========================

	@Test
	void shouldReturnTransactionById() {
		when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
		when(transactionMapper.toDto(transaction)).thenReturn(transactionDto);

		TransactionDto result = transactionService.getById(1L);

		assertNotNull(result);
	}

	@Test
	void shouldThrowWhenTransactionNotFound() {
		when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class,
				() -> transactionService.getById(1L));
	}

	// =========================
	// GET ALL (🔥 kluczowe)
	// =========================

	@Test
	void shouldReturnByUserAndCategory() {
		when(transactionRepository.findByUserIdAndCategoryId(1L, 2L))
				.thenReturn(List.of(transaction));
		when(transactionMapper.toDtoList(any())).thenReturn(List.of(transactionDto));

		List<TransactionDto> result = transactionService.getAll(1L, 2L);

		assertEquals(1, result.size());
		verify(transactionRepository).findByUserIdAndCategoryId(1L, 2L);
	}

	@Test
	void shouldReturnByUserOnly() {
		when(transactionRepository.findByUserId(1L))
				.thenReturn(List.of(transaction));
		when(transactionMapper.toDtoList(any())).thenReturn(List.of(transactionDto));

		List<TransactionDto> result = transactionService.getAll(1L, null);

		assertEquals(1, result.size());
		verify(transactionRepository).findByUserId(1L);
	}

	@Test
	void shouldReturnByCategoryOnly() {
		when(transactionRepository.findByCategoryId(2L))
				.thenReturn(List.of(transaction));
		when(transactionMapper.toDtoList(any())).thenReturn(List.of(transactionDto));

		List<TransactionDto> result = transactionService.getAll(null, 2L);

		assertEquals(1, result.size());
		verify(transactionRepository).findByCategoryId(2L);
	}

	@Test
	void shouldReturnAllWhenNoFilters() {
		when(transactionRepository.findAllWithCategory())
				.thenReturn(List.of(transaction));
		when(transactionMapper.toDtoList(any())).thenReturn(List.of(transactionDto));

		List<TransactionDto> result = transactionService.getAll(null, null);

		assertEquals(1, result.size());
		verify(transactionRepository).findAllWithCategory();
	}

	// =========================
	// DELETE
	// =========================

	@Test
	void shouldDeleteTransaction() {
		when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

		transactionService.delete(1L);

		verify(transactionRepository).delete(transaction);
		verify(transactionEventPublisher).publishDeleted(transaction);
	}

	@Test
	void shouldThrowWhenDeleteNotFound() {
		when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class,
				() -> transactionService.delete(1L));
	}
}