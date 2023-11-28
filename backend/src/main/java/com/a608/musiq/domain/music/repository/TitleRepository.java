package com.a608.musiq.domain.music.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.a608.musiq.domain.music.domain.Title;

public interface TitleRepository extends JpaRepository<Title, Integer> {

	List<Title> findAllByMusicId(@Param("musicId") Integer musicId);
}
