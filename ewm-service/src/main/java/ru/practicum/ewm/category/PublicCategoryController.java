package ru.practicum.ewm.category;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {

	private final CategoryService categoryService;

	@GetMapping
	public List<CategoryDto> getCategories(
			@RequestParam(defaultValue = "0") int from,
			@RequestParam(defaultValue = "10") int size) {
		return categoryService.getCategories(from, size);
	}

	@GetMapping("/{catId}")
	public CategoryDto getCategory(@PathVariable Long catId) {
		return categoryService.getCategory(catId);
	}
}
