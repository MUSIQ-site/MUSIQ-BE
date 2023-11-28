package com.a608.musiq.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.a608.musiq.domain.member.domain.Visitor;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

}
