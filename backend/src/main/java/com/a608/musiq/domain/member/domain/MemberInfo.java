package com.a608.musiq.domain.member.domain;

import java.util.UUID;

import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.a608.musiq.domain.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE frame SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class MemberInfo extends BaseTimeEntity {

	@Id
	private UUID id;

	@Size(max = 30)
	@Column
	private String nickname;

	@NotNull
	@Column
	private Double exp;

	@NotNull
	@Builder.Default
	@ColumnDefault("false")
	@Column
	private Boolean deleted = Boolean.FALSE;

	public void gainExp(double exp) {
		this.exp += exp;
	}
}
