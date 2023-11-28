package com.a608.musiq.domain.websocket.domain.log;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
public class MultiModeCreateGameRoomLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull
	@Column
	private String roomManagerNickname;

	@NotNull
	@Column
	private String title;

	@NotNull
	@Column
	private String years;

	@NotNull
	@Column
	private Boolean isStarted;

	@NotNull
	@Column
	private LocalDateTime createdAt;

	public void gameStart() {
		this.isStarted = Boolean.TRUE;
	}
}
