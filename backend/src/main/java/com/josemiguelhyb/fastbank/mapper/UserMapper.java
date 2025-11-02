package com.josemiguelhyb.fastbank.mapper;

import com.josemiguelhyb.fastbank.model.User;
import com.josemiguelhyb.fastbank.dto.UserResponse;

public class UserMapper {
	// Convierte la entidad JPA `User` a DTO `UserResponse` que enviamos al frontend.
	// Si en el futuro decidimos ocultar la contraseña, quitarla de UserResponse y actualizar aquí.
	public static UserResponse toResponse(User user) {
		return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPassword());
	}
}
