package com.a608.musiq.domain.member.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a608.musiq.domain.member.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member , UUID> {

	@Query("select count(m.loginId) = 0 "
		+ "from Member m "
		+ "where m.loginId = :loginId "
		+ "and m.deleted = false")
	boolean findByLoginIdNotExists(@Param("loginId") String loginId);

	@Query("select m "
		+ "from Member m "
		+ "where m.loginId = :loginId "
		+ "and m.deleted = false")
	Optional<Member> findByLoginId(@Param("loginId") String loginId);
}
