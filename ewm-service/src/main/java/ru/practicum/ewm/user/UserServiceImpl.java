package ru.practicum.ewm.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.OffsetPageRequest;
import ru.practicum.ewm.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	@Transactional
	public UserDto create(NewUserRequest request) {
		return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(request)));
	}

	@Override
	public List<UserDto> getUsers(List<Long> ids, int from, int size) {
		OffsetPageRequest page = new OffsetPageRequest(from, size);
		List<User> users = (ids == null || ids.isEmpty())
				? userRepository.findAll(page).getContent()
				: userRepository.findAllByIdIn(ids, page);
		return users.stream().map(UserMapper::toUserDto).toList();
	}

	@Override
	@Transactional
	public void delete(Long userId) {
		getById(userId);
		userRepository.deleteById(userId);
	}

	@Override
	public User getById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
	}
}
