package com.e_onlineshoppingwebsite.store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

        Optional<Member> findByEmail(String email);

        boolean existsByEmail(String email);

        boolean existsByPhone(String phone);

        List<Member> findByStatus(String status);

        List<Member> findByRole_RoleNameIn(List<String> roles);

        @Query("""
                            SELECT m FROM Member m
                            WHERE LOWER(m.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))
                               OR LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                               OR m.phone LIKE CONCAT('%', :keyword, '%')
                        """)
        List<Member> searchByKeyword(@Param("keyword") String keyword);

        @Query("""
                            SELECT m FROM Member m
                            WHERE (
                                LOWER(m.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                OR LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                OR m.phone LIKE CONCAT('%', :keyword, '%')
                            )
                            AND m.role.roleName IN :roles
                        """)
        List<Member> searchByKeywordAndRoles(
                        @Param("keyword") String keyword,
                        @Param("roles") List<String> roles);

        @Query("""
                            SELECT m FROM Member m
                            WHERE LOWER(m.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))
                               OR LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                               OR m.phone LIKE CONCAT('%', :keyword, '%')
                        """)
        Page<Member> searchWithPagination(
                        @Param("keyword") String keyword,
                        Pageable pageable);

        long countByRole_RoleName(String roleName);

        long countByStatus(String status);
}