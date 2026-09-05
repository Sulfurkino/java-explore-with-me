package ru.practicum.ewm.category;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.OffsetPageRequest;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

	private static final String CONDITIONS_NOT_MET = "For the requested operation the conditions are not met.";

	private final CategoryRepository categoryRepository;
	private final EventRepository eventRepository;

	@Override
	@Transactional
	public CategoryDto create(NewCategoryDto dto) {
		return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(dto)));
	}

	@Override
	@Transactional
	public CategoryDto update(Long catId, CategoryDto dto) {
		Category category = getById(catId);
		category.setName(dto.getName());
		return CategoryMapper.toCategoryDto(categoryRepository.save(category));
	}

	@Override
	@Transactional
	public void delete(Long catId) {
		getById(catId);
		if (eventRepository.existsByCategoryId(catId)) {
			throw new ConflictException(CONDITIONS_NOT_MET, "The category is not empty");
		}
		categoryRepository.deleteById(catId);
	}

	@Override
	public List<CategoryDto> getCategories(int from, int size) {
		return categoryRepository.findAll(new OffsetPageRequest(from, size)).stream()
				.map(CategoryMapper::toCategoryDto)
				.toList();
	}

	@Override
	public CategoryDto getCategory(Long catId) {
		return CategoryMapper.toCategoryDto(getById(catId));
	}

	@Override
	public Category getById(Long catId) {
		return categoryRepository.findById(catId)
				.orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
	}
}
