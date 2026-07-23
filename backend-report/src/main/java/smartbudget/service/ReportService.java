package smartbudget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartbudget.dto.CategoryReportDto;
import smartbudget.dto.DailyReportDto;
import smartbudget.dto.MonthlyReportDto;
import smartbudget.dto.TrendsReportDto;
import smartbudget.entity.UserStatistics;
import smartbudget.repository.CategorySummaryRepository;
import smartbudget.repository.DailyExpenseRepository;
import smartbudget.repository.MonthlyBalanceRepository;
import smartbudget.repository.UserStatisticsRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

	private final DailyExpenseRepository dailyExpenseRepository;
	private final MonthlyBalanceRepository monthlyBalanceRepository;
	private final CategorySummaryRepository categorySummaryRepository;
	private final UserStatisticsRepository userStatisticsRepository;

	@Transactional(readOnly = true)
	public List<DailyReportDto> getDailyReport(final Long userId, final LocalDate from, final LocalDate to) {
		final LocalDate start = from != null ? from : LocalDate.now().minusDays(30);
		final LocalDate end = to != null ? to : LocalDate.now();

		return dailyExpenseRepository.findByUserIdAndDateBetweenOrderByDateAsc(userId, start, end)
				.stream()
				.map(d -> DailyReportDto.builder()
						.date(d.getDate())
						.income(d.getIncome())
						.expenses(d.getExpenses())
						.balance(d.getBalance())
						.build())
				.toList();
	}

	@Transactional(readOnly = true)
	public List<MonthlyReportDto> getMonthlyReport(final Long userId, final String from, final String to) {
		final String start = from != null ? from : YearMonth.now().minusMonths(11).toString();
		final String end = to != null ? to : YearMonth.now().toString();

		return monthlyBalanceRepository.findByUserIdAndYearMonthBetweenOrderByYearMonthAsc(userId, start, end)
				.stream()
				.map(m -> MonthlyReportDto.builder()
						.yearMonth(m.getYearMonth())
						.income(m.getIncome())
						.expenses(m.getExpenses())
						.balance(m.getBalance())
						.build())
				.toList();
	}

	@Transactional(readOnly = true)
	public List<CategoryReportDto> getCategoryReport(final Long userId, final String yearMonth) {
		if (yearMonth != null && !yearMonth.isBlank()) {
			return categorySummaryRepository.findByUserIdAndYearMonthOrderByTotalAmountDesc(userId, yearMonth)
					.stream()
					.map(this::toCategoryDto)
					.toList();
		}

		return categorySummaryRepository.findByUserIdOrderByTotalAmountDesc(userId)
				.stream()
				.collect(Collectors.toMap(
						smartbudget.entity.CategorySummary::getCategoryName,
						this::toCategoryDto,
						(a, b) -> CategoryReportDto.builder()
								.categoryName(a.getCategoryName())
								.yearMonth(null)
								.totalAmount(a.getTotalAmount().add(b.getTotalAmount()))
								.transactionCount(a.getTransactionCount() + b.getTransactionCount())
								.build()
				))
				.values()
				.stream()
				.peek(dto -> dto.setYearMonth(null))
				.sorted(Comparator.comparing(CategoryReportDto::getTotalAmount).reversed())
				.toList();
	}

	@Transactional(readOnly = true)
	public TrendsReportDto getTrends(final Long userId) {
		final UserStatistics stats = userStatisticsRepository.findById(userId)
				.orElse(UserStatistics.builder()
						.userId(userId)
						.totalIncome(BigDecimal.ZERO)
						.totalExpenses(BigDecimal.ZERO)
						.netBalance(BigDecimal.ZERO)
						.transactionCount(0L)
						.build());

		final List<MonthlyReportDto> monthlyTrend = getMonthlyReport(userId, null, null);
		final List<CategoryReportDto> topCategories = getCategoryReport(userId, null).stream()
				.limit(5)
				.toList();

		return TrendsReportDto.builder()
				.totalIncome(stats.getTotalIncome())
				.totalExpenses(stats.getTotalExpenses())
				.netBalance(stats.getNetBalance())
				.transactionCount(stats.getTransactionCount())
				.monthlyTrend(monthlyTrend)
				.topCategories(topCategories)
				.build();
	}

	private CategoryReportDto toCategoryDto(final smartbudget.entity.CategorySummary summary) {
		return CategoryReportDto.builder()
				.categoryName(summary.getCategoryName())
				.yearMonth(summary.getYearMonth())
				.totalAmount(summary.getTotalAmount())
				.transactionCount(summary.getTransactionCount())
				.build();
	}
}
