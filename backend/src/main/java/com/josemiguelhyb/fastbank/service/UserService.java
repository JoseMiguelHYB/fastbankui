// SERVICIO (Interfaz)
package com.josemiguelhyb.fastbank.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.josemiguelhyb.fastbank.model.User;

public interface UserService {
	List<User> getAlllUsers();
	Optional<User> getUserById(Long id);
	Page<User> getUsers(Pageable pageable);
	User createUser(User user);
	User updateUser(Long id, User updates);
	void deleteUser(Long id);
	/**
	 * Elimina un usuario. Si cascade=true, elimina previamente sus cuentas asociadas.
	 */
	void deleteUser(Long id, boolean cascade);
	/**
	 * Re-hashea contraseñas que no estén en formato BCrypt.
	 * Devuelve el número de usuarios actualizados.
	 */
	int rehashInsecurePasswords();
}
