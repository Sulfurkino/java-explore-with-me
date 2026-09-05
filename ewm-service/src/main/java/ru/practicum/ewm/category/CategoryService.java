package ru.practicum.ewm.category;

import java.util.List;

public interface CategoryService {

	CategoryDto create(NewCategoryDto dto);

	CategoryDto update(Long catId, CategoryDto dto);

	void delete(Long catId);

	List<CategoryDto> getCategories(int from, int size);

	CategoryDto getCategory(Long catId);

	Category getById(Long catId);
}
