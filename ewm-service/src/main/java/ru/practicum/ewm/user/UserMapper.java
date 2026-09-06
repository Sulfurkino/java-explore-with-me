package ru.practicum.ewm.user;

public final class UserMapper {

	private UserMapper() {
	}

	public static User toUser(NewUserRequest request) {
		User user = new User();
		user.setEmail(request.getEmail());
		user.setName(request.getName());
		return user;
	}

	public static UserDto toUserDto(User user) {
		return new UserDto(user.getId(), user.getEmail(), user.getName());
	}

	public static UserShortDto toUserShortDto(User user) {
		return new UserShortDto(user.getId(), user.getName());
	}
}
