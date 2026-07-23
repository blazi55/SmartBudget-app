package smartbudget.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import smartbudget.dto.CategoryReportDto;
import smartbudget.dto.DailyReportDto;
import smartbudget.dto.MonthlyReportDto;
import smartbudget.dto.TrendsReportDto;
import smartbudget.service.ReportService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@GetMapping("/daily")
	public ResponseEntity<List<DailyReportDto>> daily(
			@RequestParam final Long userId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate to) {
		log.info("Daily report for userId={}, from={}, to={}", userId, from, to);
		return ResponseEntity.ok(reportService.getDailyReport(userId, from, to));
	}

	@GetMapping("/monthly")
	public ResponseEntity<List<MonthlyReportDto>> monthly(
			@RequestParam final Long userId,
			@RequestParam(required = false) final String from,
			@RequestParam(required = false) final String to) {
		log.info("Monthly report for userId={}, from={}, to={}", userId, from, to);
		return ResponseEntity.ok(reportService.getMonthlyReport(userId, from, to));
	}

	@GetMapping("/categories")
	public ResponseEntity<List<CategoryReportDto>> categories(
			@RequestParam final Long userId,
			@RequestParam(required = false) final String yearMonth) {
		log.info("Category report for userId={}, yearMonth={}", userId, yearMonth);
		return ResponseEntity.ok(reportService.getCategoryReport(userId, yearMonth));
	}

	@GetMapping("/trends")
	public ResponseEntity<TrendsReportDto> trends(@RequestParam final Long userId) {
		log.info("Trends report for userId={}", userId);
		return ResponseEntity.ok(reportService.getTrends(userId));
	}
}
