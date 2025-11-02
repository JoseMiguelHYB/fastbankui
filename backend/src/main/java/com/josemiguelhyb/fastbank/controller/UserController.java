// CONTROLADOR

package com.josemiguelhyb.fastbank.controller;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.josemiguelhyb.fastbank.dto.CreateUserRequest;
import com.josemiguelhyb.fastbank.dto.UserResponse;
import com.josemiguelhyb.fastbank.mapper.UserMapper;
import com.josemiguelhyb.fastbank.model.User;
import com.josemiguelhyb.fastbank.service.UserService;

@RestController
@RequestMapping("/api/users")
// Este controlador expone los endpoints REST para usuarios usados por el frontend Angular.
// Endpoints principales:
//  - GET  /api/users       -> devuelve List<UserResponse> (mapeado por UserMapper)
//  - GET  /api/users/{id}  -> devuelve UserResponse por id
//  - POST /api/users       -> crea un usuario a partir de CreateUserRequest
//
// Nota para la demo/local: el frontend Angular consume estos endpoints desde
// `frontend/src/app/user/user.ts` y renderiza la tabla en `frontend/src/app/user/user.html`.
// Si cambias la estructura de DTOs (CreateUserRequest/UserResponse) recuerda actualizar
// también `frontend` para que espere/mande las mismas propiedades (name,email,password).
public class UserController {
	
	private final UserService userService;

	// No hace falta @Autowired porque hay solo un constructor
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	// Endpoint de prueba
	@GetMapping("/test")
	public String testEndpoint() {
        return "✅ El módulo de usuarios de FastBank funciona correctamente.";
	}
	
	// Obtener todos los usuarios
	// Mapea cada entidad `User` a `UserResponse` con `UserMapper`.
	// Angular: llamada Http GET en `frontend/src/app/user/user.ts` -> loadUsers()
	@GetMapping
	public List<UserResponse> getAllUsers() {
		return userService.getAlllUsers().stream()
				.map(UserMapper::toResponse)
				.collect(Collectors.toList());
	}
	
	// Obtener un usuario por ID
	/***@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		Optional<User> user = userService.getUserById(id);
		return user.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}***/
	
	
	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
		return userService.getUserById(id)
				.map(user -> ResponseEntity.ok(UserMapper.toResponse(user)))
				.orElse(ResponseEntity.notFound().build());
	}   
    
	/**
	 * Crear un nuevo usuario.
	 * Recibe JSON con la forma de `CreateUserRequest` (name,email,password).
	 * El servicio aplicará las validaciones (ej. email único) en `UserServiceImpl`.
	 *
	 * Atención: en un entorno real no deberíamos devolver ni mostrar la contraseña en claro.
	 * Aquí se deja así para facilitar pruebas locales/demos; en producción hay que hashearla
	 * y no devolverla nunca en `UserResponse`.
	 *
	 * Frontend: la función `createUser()` en `frontend/src/app/user/user.ts` hace POST a este endpoint.
	 */
	@PostMapping
	public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		User newUser = userService.createUser(user);
		return ResponseEntity.ok(UserMapper.toResponse(newUser));    
	}   
}