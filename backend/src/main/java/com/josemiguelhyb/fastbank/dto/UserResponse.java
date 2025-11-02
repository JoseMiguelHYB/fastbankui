package com.josemiguelhyb.fastbank.dto;

public class UserResponse {
	private Long id;
	private String name;
	private String email;
	private String password;
	private String createdAt;
	private String updatedAt;
	
	public UserResponse(Long id, String name, String email, String password, String createdAt, String updatedAt) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	// Getters
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}
}
