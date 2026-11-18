package com.barrowrent.response;

import java.time.LocalDateTime;

import com.barrowrent.entity.User;

public record  UserResponse(
	     long id,
	     String fullName,
	     String email,
	     String phone,
	     String address,
	     String role,
	     LocalDateTime createdAt,
	     LocalDateTime updatedAt)
	{
	  public static UserResponse from(User user) {
		  return new UserResponse(
				  user.getId(),
	              user.getFullName(),
	              user.getEmail(),
	              user.getPhone(),
	              user.getAddress(),
	              user.getRole().name(),
	              user.getCreatedAt(),
	              user.getUpdatedAt()
	      );
	  }
	}
