
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.Member;
import com.e_onlineshoppingwebsite.store.Repository.MemberRepository;
import com.e_onlineshoppingwebsite.store.Service.MembersService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MembersServiceImpl implements MembersService {

        private final MemberRepository memberRepository;

        @Override
        public List<Member> getAllMembers() {
                return memberRepository.findAll();
        }

        @Override
        public Member getMemberById(Long id) {
                return memberRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        }

        @Override
        public List<Member> searchByKeyword(String keyword) {
                return memberRepository.searchByKeyword(
                                keyword == null ? "" : keyword);
        }

        @Override
        public List<Member> findByRoles(List<String> roles) {
                if (roles == null || roles.isEmpty()) {
                        return List.of();
                }

                return memberRepository.findByRole_RoleNameIn(roles);
        }

        @Override
        public List<Member> searchByKeywordAndRoles(
                        String keyword, List<String> roles) {

                if (roles == null || roles.isEmpty()) {
                        return List.of();
                }

                return memberRepository.searchByKeywordAndRoles(
                                keyword == null ? "" : keyword, roles);
        }

        @Override
        public List<Member> findByStatus(String status) {
                return memberRepository.findByStatus(status);
        }

        @Override
        public List<Member> searchMembersWithPagination(
                        String keyword, int page, int size) {

                if (page < 0 || size <= 0) {
                        throw new IllegalArgumentException("Phân trang không hợp lệ");
                }

                return memberRepository.searchWithPagination(
                                keyword == null ? "" : keyword,
                                PageRequest.of(page, size))
                                .getContent();
        }

        @Override
        public long countMembers(String keyword) {
                if (keyword == null || keyword.isBlank()) {
                        return memberRepository.count();
                }

                return memberRepository.searchByKeyword(keyword).size();
        }

        @Override
        public List<Member> searchCustomers(String keyword) {
                return searchByKeywordAndRoles(
                                keyword, List.of("customer"));
        }

        @Override
        public List<Member> searchEmployees(String keyword) {
                return searchByKeywordAndRoles(
                                keyword, List.of("admin", "employee"));
        }

        @Override
        public long countCustomers() {
                return memberRepository.countByRole_RoleName("customer");
        }

        @Override
        public Optional<Member> findByEmail(String email) {
                return memberRepository.findByEmail(email);
        }

        @Override
        public boolean existsByEmail(String email) {
                return memberRepository.existsByEmail(email);
        }

        @Override
        public boolean existsByPhone(String phone) {
                return memberRepository.existsByPhone(phone);
        }

        @Override
        public void save(Member member) {
                throw new UnsupportedOperationException(
                                "Tạo tài khoản phải đi qua AuthService.register() "
                                                + "để mã hóa mật khẩu và gán vai trò an toàn");
        }

        @Override
        public void update(Member member) {

                if (member == null || member.getUserId() == null) {
                        throw new IllegalArgumentException("Thiếu ID người dùng");
                }

                Member old = getMemberById(member.getUserId());

                if (!old.getEmail().equals(member.getEmail())
                                && memberRepository.existsByEmail(member.getEmail())) {
                        throw new IllegalArgumentException("Email đã tồn tại");
                }

                if (member.getPhone() != null
                                && !member.getPhone().equals(old.getPhone())
                                && memberRepository.existsByPhone(member.getPhone())) {
                        throw new IllegalArgumentException("Số điện thoại đã tồn tại");
                }

                old.setFullname(member.getFullname());
                old.setEmail(member.getEmail());
                old.setPhone(member.getPhone());
                old.setGender(member.getGender());
                old.setDateOfBirth(member.getDateOfBirth());
                old.setAvatarUrl(member.getAvatarUrl());

                // Không cập nhật passwordHash, role hoặc status tại đây.
                memberRepository.save(old);
        }

        @Override
        public void updateStatus(Long id, String status) {

                if (!"active".equals(status)
                                && !"blocked".equals(status)) {
                        throw new IllegalArgumentException(
                                        "Trạng thái tài khoản không hợp lệ");
                }

                Member member = getMemberById(id);
                member.setStatus(status);

                memberRepository.save(member);
        }

        @Override
        public void deleteMember(Long id) {

                Member member = getMemberById(id);

                // Khóa tài khoản để giữ lịch sử đơn hàng.
                member.setStatus("blocked");

                memberRepository.save(member);
        }
}
