package smartbudget.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import smartbudget.dto.TransactionDto;
import smartbudget.dto.createDto.CreateTransactionDto;
import smartbudget.dto.createDto.UpdateTransactionDto;
import smartbudget.service.TransactionService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;

	@PostMapping
	public ResponseEntity<TransactionDto> create(@RequestBody final CreateTransactionDto dto) {
		log.info("create transaction in controller: {}", dto);
		return ResponseEntity.ok(transactionService.create(dto));
	}

	@GetMapping("/{id}")
	public ResponseEntity<TransactionDto> get(@PathVariable final Long id) {
		log.info("get transaction in controller by id: {}", id);
		return ResponseEntity.ok(transactionService.getById(id));
	}

	@GetMapping
	public ResponseEntity<List<TransactionDto>> getAll(
			@RequestParam(required = false) final Long userId,
			@RequestParam(required = false) final Long categoryId) {
		log.info("get all transactions in controller by userId and categoryId: {}, {}", userId, categoryId);
		return ResponseEntity.ok(transactionService.getAll(userId, categoryId));
	}

	@PutMapping("/{id}")
	public ResponseEntity<TransactionDto> update(
			@PathVariable final Long id,
			@RequestBody final UpdateTransactionDto dto) {
		log.info("update transaction in controller by id: {}", id);
		return ResponseEntity.ok(transactionService.update(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable final Long id) {
		log.info("delete transaction in controller by id: {}", id);
		transactionService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/republish-events")
	public ResponseEntity<Map<String, Object>> republishEvents() {
		final int count = transactionService.republishAllEvents();
		return ResponseEntity.ok(Map.of("republished", count));
	}
}
