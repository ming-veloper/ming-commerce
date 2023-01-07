package com.ming.mingcommerce.member.repository;

import com.ming.mingcommerce.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
