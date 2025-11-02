// Aquí la capa es la que contiene la lógica de negocio
package com.josemiguelhyb.fastbank.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Es está
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.josemiguelhyb.fastbank.model.User;
import com.josemiguelhyb.fastbank.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}	
	
	@Override
	public List<User> getAlllUsers() {
		List<User> all = userRepository.findAll();
		boolean changed = false;
		for (User u : all) {
			String p = u.getPassword();
			if (p != null && !isBcrypt(p)) {
				u.setPassword(passwordEncoder.encode(p));
				changed = true;
			}
		}
		if (changed) {
			userRepository.saveAll(all);
		}
		return all;
	}

	@Override
	public Page<User> getUsers(Pageable pageable) {
		Page<User> page = userRepository.findAll(pageable);
		boolean changed = false;
		for (User u : page.getContent()) {
			String p = u.getPassword();
			if (p != null && !isBcrypt(p)) {
				u.setPassword(passwordEncoder.encode(p));
				changed = true;
			}
		}
		if (changed) {
			userRepository.saveAll(page.getContent());
		}
		return page;
	}

	@Override
	public Optional<User> getUserById(Long id) {
		Optional<User> opt = userRepository.findById(id);
		if (opt.isPresent()) {
			User u = opt.get();
			String p = u.getPassword();
			if (p != null && !isBcrypt(p)) {
				u.setPassword(passwordEncoder.encode(p));
				userRepository.save(u);
			}
		}
		return opt;
	}

	@Override
	@Transactional // Solo aquí, porque modifica la BD se hace en una operación ÚNICA
	public User createUser(User user) {
		// Lógica de negocio:
		// 1. Verificar si ya existe un usuario con ese email
		Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
		if(existingUser.isPresent()) {
			// Devolver 409 CONFLICT de forma consistente con update/delete
			throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
		}
		// 2. Hashear contraseña con BCrypt
		if (user.getPassword() != null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		// 3. Guardar el usuario si pasa las validaciones
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public User updateUser(Long id, User updates) {
		User existing = userRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

		if (updates.getEmail() != null) {
			Optional<User> byEmail = userRepository.findByEmail(updates.getEmail());
			if (byEmail.isPresent() && !byEmail.get().getId().equals(id)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
			}
			existing.setEmail(updates.getEmail());
		}
		if (updates.getName() != null) {
			existing.setName(updates.getName());
		}
		if (updates.getPassword() != null && !updates.getPassword().isBlank()) {
			existing.setPassword(passwordEncoder.encode(updates.getPassword()));
		}
		return userRepository.save(existing);
	}

	@Override
	@Transactional
	public void deleteUser(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
		}
		userRepository.deleteById(id);
	}

	@Override
	@Transactional
	public int rehashInsecurePasswords() {
		int updated = 0;
		List<User> all = userRepository.findAll();
		for (User u : all) {
			String p = u.getPassword();
			if (p != null && !isBcrypt(p)) {
				u.setPassword(passwordEncoder.encode(p));
				updated++;
			}
		}
		if (updated > 0) {
			userRepository.saveAll(all);
		}
		return updated;
	}

	private boolean isBcrypt(String value) {
		// BCrypt típico de 60 chars: $2a$10$...
		return value != null && value.matches("^\\$2[aby]?\\$\\d{2}\\$[A-Za-z0-9./]{53}$");
	}
}
