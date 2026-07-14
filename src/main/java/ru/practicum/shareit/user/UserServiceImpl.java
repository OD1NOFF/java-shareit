package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        if (userRepository.existsByEmailIgnoreCase(userDto.getEmail())) {
            throw new ConflictException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }
        User created = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(created);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User existing = getUserOrThrow(userId);

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            if (userRepository.existsByEmailIgnoreCaseAndIdNot(userDto.getEmail(), userId)) {
                throw new ConflictException("Пользователь с email " + userDto.getEmail() + " уже существует");
            }
            existing.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existing.setName(userDto.getName());
        }
        return UserMapper.toUserDto(userRepository.save(existing));
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toUserDto(getUserOrThrow(userId));
    }

    @Override
    public Collection<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        getUserOrThrow(userId);
        userRepository.deleteById(userId);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }
}