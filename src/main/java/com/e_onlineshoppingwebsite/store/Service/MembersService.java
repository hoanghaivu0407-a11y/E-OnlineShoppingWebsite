
package com.e_onlineshoppingwebsite.store.Service;

import java.util.List;
import java.util.Optional;

import com.e_onlineshoppingwebsite.store.Model.Member;

public interface MembersService {

    List<Member> getAllMembers();

    Member getMemberById(Long id);

    List<Member> searchByKeyword(String keyword);

    List<Member> findByRoles(List<String> roles);

    List<Member> searchByKeywordAndRoles(
            String keyword, List<String> roles);

    List<Member> findByStatus(String status);

    List<Member> searchMembersWithPagination(
            String keyword, int page, int size);

    long countMembers(String keyword);

    List<Member> searchCustomers(String keyword);

    List<Member> searchEmployees(String keyword);

    long countCustomers();

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    void save(Member member);

    void update(Member member);

    void updateStatus(Long id, String status);

    void deleteMember(Long id);
}
