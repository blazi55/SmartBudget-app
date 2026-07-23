package smartbudget.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartbudget.entity.CategorySummary;
import smartbudget.entity.DailyExpense;
import smartbudget.entity.MonthlyBalance;
import smartbudget.entity.ReportTransaction;
import smartbudget.entity.UserStatistics;
import smartbudget.event.TransactionEvent;
import smartbudget.repository.CategorySummaryRepository;
import smartbudget.repository.DailyExpenseRepository;
import smartbudget.repository.MonthlyBalanceRepository;
import smartbudget.repository.ReportTransactionRepository;
import smartbudget.repository.UserStatisticsRepository;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationService {

	private final ReportTransactionRepository reportTransactionRepository;
	private final DailyExpenseRepository dailyExpenseRepository;
	private final MonthlyBalanceRepository monthlyBalanceRepository;
	private final CategorySummaryRepository categorySummaryRepository;
	private final UserStatisticsRepository userStatisticsRepository;

	@Transactional
	public void handle(final TransactionEvent event) {
		if (event == null || event.getEventType() == null || event.getTransactionId() == null) {
			log.warn("Ignoring invalid transaction event: {}", event);
			return;
		}

		switch (event.getEventType()) {
			case CREATED -> applyCreated(event);
			case UPDATED -> applyUpdated(event);
			case DELETED -> applyDeleted(event);
		}
	}

	private void applyCreated(final TransactionEvent event) {
		if (reportTransactionRepository.existsById(event.getTransactionId())) {
			applyUpdated(event);
			return;
		}

		final ReportTransaction tx = toEntity(event);
		reportTransactionRepository.save(tx);
		applyDelta(event.getUserId(), event, BigDecimal.ONE);
	}

	private void applyUpdated(final TransactionEvent event) {
		final Optional<ReportTransaction> existing = reportTransactionRepository.findById(event.getTransactionId());
		if (existing.isPresent()) {
			applyDelta(existing.get().getUserId(), fromEntity(existing.get()), BigDecimal.valueOf(-1));
			reportTransactionRepository.delete(existing.get());
		}

		final ReportTransaction tx = toEntity(event);
		reportTransactionRepository.save(tx);
		applyDelta(event.getUserId(), event, BigDecimal.ONE);
	}

	private void applyDeleted(final TransactionEvent event) {
		final Optional<ReportTransaction> existing = reportTransactionRepository.findById(event.getTransactionId());
		if (existing.isEmpty()) {
			return;
		}

		final ReportTransaction tx = existing.get();
		applyDelta(tx.getUserId(), fromEntity(tx), BigDecimal.valueOf(-1));
		reportTransactionRepository.delete(tx);
	}

	private void applyDelta(final Long userId, final TransactionEvent event, final BigDecimal direction) {
		final BigDecimal amount = event.getAmount() != null ? event.getAmount().abs() : BigDecimal.ZERO;
		final boolean income = "INCOME".equalsIgnoreCase(event.getType());
		final BigDecimal signedIncome = income ? amount.multiply(direction) : BigDecimal.ZERO;
		final BigDecimal signedExpense = income ? BigDecimal.ZERO : amount.multiply(direction);
		final long countDelta = direction.signum();

		updateDaily(userId, event, signedIncome, signedExpense);
		updateMonthly(userId, event, signedIncome, signedExpense);
		updateCategory(userId, event, amount.multiply(direction), countDelta);
		updateUserStats(userId, signedIncome, signedExpense, countDelta);
	}

	private void updateDaily(
			final Long userId,
			final TransactionEvent event,
			final BigDecimal incomeDelta,
			final BigDecimal expenseDelta) {
		final DailyExpense daily = dailyExpenseRepository.findByUserIdAndDate(userId, event.getDate())
				.orElseGet(() -> DailyExpense.builder()
						.userId(userId)
						.date(event.getDate())
						.income(BigDecimal.ZERO)
						.expenses(BigDecimal.ZERO)
						.balance(BigDecimal.ZERO)
						.build());

		daily.setIncome(daily.getIncome().add(incomeDelta));
		daily.setExpenses(daily.getExpenses().add(expenseDelta));
		daily.setBalance(daily.getIncome().subtract(daily.getExpenses()));
		dailyExpenseRepository.save(daily);
	}

	private void updateMonthly(
			final Long userId,
			final TransactionEvent event,
			final BigDecimal incomeDelta,
			final BigDecimal expenseDelta) {
		final String yearMonth = YearMonth.from(event.getDate()).toString();
		final MonthlyBalance monthly = monthlyBalanceRepository.findByUserIdAndYearMonth(userId, yearMonth)
				.orElseGet(() -> MonthlyBalance.builder()
						.userId(userId)
						.yearMonth(yearMonth)
						.income(BigDecimal.ZERO)
						.expenses(BigDecimal.ZERO)
						.balance(BigDecimal.ZERO)
						.build());

		monthly.setIncome(monthly.getIncome().add(incomeDelta));
		monthly.setExpenses(monthly.getExpenses().add(expenseDelta));
		monthly.setBalance(monthly.getIncome().subtract(monthly.getExpenses()));
		monthlyBalanceRepository.save(monthly);
	}

	private void updateCategory(
			final Long userId,
			final TransactionEvent event,
			final BigDecimal amountDelta,
			final long countDelta) {
		if (!"EXPENSE".equalsIgnoreCase(event.getType())) {
			return;
		}

		final String categoryName = event.getCategoryName() != null ? event.getCategoryName() : "Other";
		final String yearMonth = YearMonth.from(event.getDate()).toString();
		final CategorySummary summary = categorySummaryRepository
				.findByUserIdAndCategoryNameAndYearMonth(userId, categoryName, yearMonth)
				.orElseGet(() -> CategorySummary.builder()
						.userId(userId)
						.categoryName(categoryName)
						.yearMonth(yearMonth)
						.totalAmount(BigDecimal.ZERO)
						.transactionCount(0L)
						.build());

		summary.setTotalAmount(summary.getTotalAmount().add(amountDelta));
		summary.setTransactionCount(Math.max(0, summary.getTransactionCount() + countDelta));
		categorySummaryRepository.save(summary);
	}

	private void updateUserStats(
			final Long userId,
			final BigDecimal incomeDelta,
			final BigDecimal expenseDelta,
			final long countDelta) {
		final UserStatistics stats = userStatisticsRepository.findById(userId)
				.orElseGet(() -> UserStatistics.builder()
						.userId(userId)
						.totalIncome(BigDecimal.ZERO)
						.totalExpenses(BigDecimal.ZERO)
						.netBalance(BigDecimal.ZERO)
						.transactionCount(0L)
						.build());

		stats.setTotalIncome(stats.getTotalIncome().add(incomeDelta));
		stats.setTotalExpenses(stats.getTotalExpenses().add(expenseDelta));
		stats.setNetBalance(stats.getTotalIncome().subtract(stats.getTotalExpenses()));
		stats.setTransactionCount(Math.max(0, stats.getTransactionCount() + countDelta));
		userStatisticsRepository.save(stats);
	}

	private ReportTransaction toEntity(final TransactionEvent event) {
		return ReportTransaction.builder()
				.transactionId(event.getTransactionId())
				.userId(event.getUserId())
				.categoryId(event.getCategoryId())
				.categoryName(event.getCategoryName())
				.amount(event.getAmount())
				.type(event.getType())
				.date(event.getDate())
				.currency(event.getCurrency())
				.description(event.getDescription())
				.build();
	}

	private TransactionEvent fromEntity(final ReportTransaction tx) {
		return TransactionEvent.builder()
				.transactionId(tx.getTransactionId())
				.userId(tx.getUserId())
				.categoryId(tx.getCategoryId())
				.categoryName(tx.getCategoryName())
				.amount(tx.getAmount())
				.type(tx.getType())
				.date(tx.getDate())
				.currency(tx.getCurrency())
				.description(tx.getDescription())
				.build();
	}
}
