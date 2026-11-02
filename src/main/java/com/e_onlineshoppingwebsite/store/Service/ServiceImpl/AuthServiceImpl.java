
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.Member;
import com.e_onlineshoppingwebsite.store.Model.Role;
import com.e_onlineshoppingwebsite.store.Repository.MemberRepository;
import com.e_onlineshoppingwebsite.store.Repository.RoleRepository;
import com.e_onlineshoppingwebsite.store.Service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member register(Member member) {

        if (member == null || member.getEmail() == null
                || member.getPasswordHash() == null
                || member.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException("Thiếu thông tin đăng ký");
        }

        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        if (member.getPhone() != null
                && memberRepository.existsByPhone(member.getPhone())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }

        Role customer = roleRepository.findByRoleName("customer")
                .orElseThrow(() -> new IllegalStateException("Chưa có vai trò customer"));

        member.setUserId(null);
        member.setRole(customer);
        member.setStatus("active");

        member.setPasswordHash(
                passwordEncoder.encode(member.getPasswordHash()));

        return memberRepository.save(member);
    }

    @Override
    public Member login(String email, String password) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email hoặc mật khẩu không đúng"));

        if (!"active".equals(member.getStatus())
                || !passwordEncoder.matches(
                        password, member.getPasswordHash())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng");
        }

        return member;
    }

    @Override
    public void changePassword(
            Long userId,
            String oldPassword,
            String newPassword) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(
                oldPassword, member.getPasswordHash())) {
            throw new IllegalArgumentException("Mật khẩu cũ không đúng");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Mật khẩu mới phải có ít nhất 8 ký tự");
        }

        member.setPasswordHash(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    @Override
    public void forgotPassword(String email) {
        throw new UnsupportedOperationException(
                "Chưa triển khai hệ thống gửi email và lưu token đặt lại mật khẩu");
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        throw new UnsupportedOperationException(
                "Chưa triển khai cơ chế xác thực token đặt lại mật khẩu");
    }
}
