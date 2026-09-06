package ru.practicum.ewm.category;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private EventRepository eventRepository;

	@InjectMocks
	private CategoryServiceImpl categoryService;

	@Test
	void deleteWhenCategoryHasEventsThrowsConflict() {
		Category category = new Category();
		category.setId(1L);
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(eventRepository.existsByCategoryId(1L)).thenReturn(true);

		assertThatThrownBy(() -> categoryService.delete(1L))
				.isInstanceOf(ConflictException.class)
				.hasMessage("The category is not empty");
		verify(categoryRepository, never()).deleteById(1L);
	}

	@Test
	void getUnknownCategoryThrowsNotFound() {
		when(categoryRepository.findById(27L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> categoryService.getCategory(27L))
				.isInstanceOf(NotFoundException.class)
				.hasMessageContaining("Category with id=27");
	}
}
