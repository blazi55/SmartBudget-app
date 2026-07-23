package smartbudget.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import smartbudget.dto.UserDto;
import smartbudget.dto.createDto.CreateUserDto;
import smartbudget.enitity.User;
import smartbudget.exception.NotFoundException;
import smartbudget.mapper.UserMapper;
import smartbudget.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public UserDto create(final CreateUserDto dto) {
		final User user = User.builder()
				.email(dto.getEmail())
				.passwordHash(passwordEncoder.encode(dto.getPassword()))
				.createdAt(LocalDateTime.now())
				.build();

		return userMapper.toDto(userRepository.save(user));
	}

	public UserDto getById(final Long id) {
		return userMapper.toDto(userRepository.findById(id)
				.orElseThrow(() -> {
					log.error("User not found by id {}", id);
					throw new NotFoundException("User not found");
				}));
	}

	public List<UserDto> getAll() {
		return userMapper.toDtoList(userRepository.findAll());
	}

	public void delete(final Long id) {
		if (!userRepository.existsById(id)) {
			log.error("User not found by id {}", id);
			throw new NotFoundException("User not found");
		}
		userRepository.deleteById(id);
	}
}
