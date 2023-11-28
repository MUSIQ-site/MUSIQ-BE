package com.a608.musiq.domain.music.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class UnableMusic {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Size(max = 50)
	@NotNull
	@Column
	private String title;

	@Size(max = 20)
	@NotNull
	@Column
	private String year;

	@Size(max = 20)
	@NotNull
	@Column
	private String singer;

	@Size(max = 255)
	@NotNull
	@Column
	private String url;

	@NotNull
	@Column
	private Integer runTime;

	@Size(max = 50)
	@NotNull
	@Column
	private String hint;
}
