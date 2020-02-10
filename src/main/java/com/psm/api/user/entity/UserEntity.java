
package com.psm.api.user.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Table(name="Tbl_user")
public class UserEntity implements UserDetails  {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int userIdx;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name="company_idx", nullable = true)
	private CompanyEntity companyIdx;
	
	
//	@JoinColumn(name = "company_idx", insertable = false, updatable = false)
//	@ManyToOne(targetEntity = CompanyEntity.class, fetch = FetchType.EAGER)
//	private CompanyEntity companyEntity;
//
//	@Column(name = "company_idx")
//	private int companyIdx;
	
	
	@Column(nullable = false, unique = true, length = 60)
	private String userId;
	
	@Column(nullable = false, length = 60)
	private String userName;
	
	@Column(nullable = false, length = 100)
	private String userPw;
	
	@Column(nullable = false, length = 20)
	private String userTel;
	
	@Column(nullable = false, length = 20)
	private String userPhone;
	
	@Column(nullable = false, length = 20)
	private String userEmail;
	
	@Column(nullable = false, columnDefinition = "datetime2 default getdate()")
	private Date createdDate;
	
	@Column(nullable=false, columnDefinition = "char(1) default 'N'")
	private String deletedYn;

	@ElementCollection(fetch = FetchType.EAGER)
	@Builder.Default
	private List<String> roles = new ArrayList<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Override
	public String getUsername() {
		return this.userId;
	}

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.userPw;
	}
	
}
