
package com.psm.api.user.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CollectionTable;
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
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.psm.api.company.entity.CompanyEntity;

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
@DynamicUpdate
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name="tbl_user")
public class UserEntity implements UserDetails  {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int userIdx;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name="company_idx", nullable = true)
	private CompanyEntity companyIdx;
	
	@Column(nullable = false, unique = true, length = 60)
	private String userId;
	
	@Column(nullable = false, length = 60)
	private String name;
	
	
	@Column(nullable = false, length = 100)
	@JsonIgnore
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
	@CollectionTable(name="tbl_userRole")
	@Builder.Default
	private List<String> userRoles = new ArrayList<>();

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.userRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
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
	@JsonIgnore
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.userPw;
	}
	
}
