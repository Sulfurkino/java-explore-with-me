package ru.practicum.ewm.category;

public final class CategoryMapper {

	private CategoryMapper() {
	}

	public static Category toCategory(NewCategoryDto dto) {
		Category category = new Category();
		category.setName(dto.getName());
		return category;
	}

	public static CategoryDto toCategoryDto(Category category) {
		return new CategoryDto(category.getId(), category.getName());
	}
}
