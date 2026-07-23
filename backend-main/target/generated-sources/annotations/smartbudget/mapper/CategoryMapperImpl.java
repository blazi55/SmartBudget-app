package smartbudget.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import smartbudget.dto.CategoryDto;
import smartbudget.enitity.Category;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-23T13:42:15+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryDto toDto(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setId( category.getId() );
        categoryDto.setName( category.getName() );
        categoryDto.setType( category.getType() );
        categoryDto.setIcon( category.getIcon() );
        categoryDto.setColor( category.getColor() );

        return categoryDto;
    }
}
