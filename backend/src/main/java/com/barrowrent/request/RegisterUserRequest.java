package com.barrowrent.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {

    
	@NotBlank(message="full name is required")
	@Size(max=100,message="name cannot exceed 100 characters")
	private String fullName;
	
	
	@NotBlank(message="email is required")
	@Email(message="please enter the valid email addresss")
	@Size(max=255)
	private String email;
	
	@NotBlank(message="password is required")
	@Size( min=8,max=72,message="password must be between 8 and 72 characters")
	private String password;
	
	@Size(max=20)
	private String phone;
	
	@Size(max=500)
	private String address;

}
