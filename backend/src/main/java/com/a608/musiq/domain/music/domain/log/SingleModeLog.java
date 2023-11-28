package com.a608.musiq.domain.music.domain.log;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import com.a608.musiq.domain.music.data.Difficulty;
import com.a608.musiq.domain.music.data.DifficultyConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleModeLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull
	@Column
	private String year;

	@NotNull
	@Column
	@Convert(converter = DifficultyConverter.class)
	private Difficulty difficulty;

	@Column
	private double exp;

	@Column
	private String ip;

	@NotNull
	@Column
	private UUID memberId;

	@NotNull
	@Column
	private String nickname;

	@Column
	private int round;

	@NotNull
	@Column
	private LocalDateTime startedAt;

	@Column
	private LocalDateTime endedAt;

	@Column
	private int playTime;

	public static SingleModeLog from(String year, Difficulty difficulty, UUID memberId, String nickname) {
		return SingleModeLog.builder()
			.year(year)
			.difficulty(difficulty)
			.memberId(memberId)
			.nickname(nickname)
			.startedAt(LocalDateTime.now())
			.build();
	}

	public void addIp(String ip) {
		this.ip = ip;
	}

	public void addAdditionalInformation(int round, double exp) {
		this.endedAt = LocalDateTime.now();
		calculatePlayTime();

		this.round = round;
		this.exp = exp;
	}

	private void calculatePlayTime() {
		this.playTime = (int)Duration.between(startedAt, endedAt).getSeconds();
	}
}
