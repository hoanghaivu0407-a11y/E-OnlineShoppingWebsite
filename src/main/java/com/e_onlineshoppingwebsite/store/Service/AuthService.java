package com.e_onlineshoppingwebsite.store.Service;

import com.e_onlineshoppingwebsite.store.Model.Member;

public interface AuthService {

    Member register(Member member);

    Member login(String email, String password);

    void changePassword(
            Long userId,
            String oldPassword,
            String newPassword);

    void forgotPassword(String email);

    void resetPassword(
            String token,
            String newPassword);
}