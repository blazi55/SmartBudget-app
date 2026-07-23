package smartbudget.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import smartbudget.dto.TransactionDto;
import smartbudget.enitity.Transaction;

import java.util.List;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface TransactionMapper {

	@Mapping(target = "categoryDto", source = "category")
	@Mapping(target = "userId", source = "user.id")
	TransactionDto toDto(Transaction transaction);

	List<TransactionDto> toDtoList(List<Transaction> transactions);
}
