package com.a608.musiq.domain.music.repository;

import java.util.Optional;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a608.musiq.domain.music.dto.queryDto.FindAnswerDto;
import com.a608.musiq.domain.music.domain.Music;

@Repository
public interface MusicRepository extends JpaRepository<Music, Integer> {
    @Query("SELECT m FROM Music m where m.year = :year")
    List<Music> findAllByYear(@Param("year") String year);

	@Query("select new com.a608.musiq.domain.music.dto.queryDto.FindAnswerDto(m.title) from Music m where m.id = :musicId")
	Optional<FindAnswerDto> findTitleById(@Param("musicId") Integer musicId);
}
