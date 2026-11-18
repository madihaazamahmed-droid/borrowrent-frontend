package com.barrowrent.entity;

import java.time.LocalDateTime;
import java.util.Locale;

import com.barrowrent.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Entity
@Table(name="users",uniqueConstraints= {
		@UniqueConstraint(
				name="uk_users_email",
				columnNames="email")
})
public class User {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)//lombok must not generate a setter for this filed
	private Long id;
	
	@NotBlank
	@Size(max=100)
	@Column(name="full_name",nullable=false,length=100)
	private String fullName;
	
	
	@NotBlank
	@Size(max=100)
	@Column(nullable=false,length=254)
	private String email;
	
	
	@NotBlank
	@ToString.Exclude//prevents lombok generated toSting() from printing the 
	//password hash
	@Column(name="password_hash",nullable=false,length=255)//why len=255 provides sufficeient 
	//space for encoded password
	private String passwordHash;
	
	@Size(max=20)
	@Column(length=20)
	private String Phone;
	
	@Size(max=500)
	@Column(length=500)
	private String address;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false,length=20)
	private UserRole role=UserRole.USER;
	
	
	@Setter(AccessLevel.NONE)
	@Column(name="created_at",nullable=false,updatable=false)
	private LocalDateTime createdAt; 
	
	@Setter(AccessLevel.NONE)
	@Column(name="updated_at",nullable=false)
	private LocalDateTime updatedAt;
	
	//constructor use by the service
	public User(String fullName,
			String email,
			String passwordHash,
			String phone,
			String address) {
		this.fullName=fullName;
		this.email=normalizeEmail(email);
		this.passwordHash=passwordHash;
		this.Phone=phone;
		this.address=address;
		this.role=UserRole.USER;
	}
	
	@PrePersist
	protected void onCreate() {
		LocalDateTime now=LocalDateTime.now();
		
		email=normalizeEmail(email);
		createdAt=now;
		updatedAt=now;
		 if(role==null) {
			 role=UserRole.USER;
		 }
	}
	
	@PreUpdate
	 protected void onUpdate() {
		 email=normalizeEmail(email);
		 updatedAt=LocalDateTime.now();
	 }
	private static String normalizeEmail(String email) {
		return email==null?null:email.trim().toLowerCase(Locale.ROOT);
	}

}
