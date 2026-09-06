package ru.practicum.ewm.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void createSavesUser() {
		NewUserRequest request = new NewUserRequest();
		request.setEmail("ivan@yandex.ru");
		request.setName("Ivan");
		User saved = new User();
		saved.setId(1L);
		saved.setEmail(request.getEmail());
		saved.setName(request.getName());
		when(userRepository.save(any(User.class))).thenReturn(saved);

		UserDto dto = userService.create(request);

		assertThat(dto.getId()).isEqualTo(1L);
		assertThat(dto.getEmail()).isEqualTo("ivan@yandex.ru");
	}

	@Test
	void getUsersWithoutIdsUsesPagination() {
		User user = new User();
		user.setId(1L);
		user.setEmail("a@b.ru");
		user.setName("A");
		when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(user)));

		List<UserDto> result = userService.getUsers(null, 0, 10);

		assertThat(result).hasSize(1);
	}

	@Test
	void deleteUnknownUserThrowsNotFound() {
		when(userRepository.findById(5L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.delete(5L))
				.isInstanceOf(NotFoundException.class)
				.hasMessageContaining("User with id=5");
	}

	@Test
	void deleteExistingUser() {
		User user = new User();
		user.setId(1L);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		userService.delete(1L);

		verify(userRepository).deleteById(1L);
	}
}
