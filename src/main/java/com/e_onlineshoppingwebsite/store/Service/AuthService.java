package com.e_onlineshoppingwebsite.store.Service;

import com.e_onlineshoppingwebsite.store.Dto.LoginRequest;
import com.e_onlineshoppingwebsite.store.Dto.LoginResponse;
import com.e_onlineshoppingwebsite.store.Dto.MemberResponse;
import com.e_onlineshoppingwebsite.store.Dto.RegisterRequest;

public interface AuthService {

    MemberResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    void changePassword(
            Long userId,
            String oldPassword,
            String newPassword);

    void forgotPassword(String email);

    void resetPassword(String token, String newPassword);
}