
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.Role;
import com.e_onlineshoppingwebsite.store.Repository.RoleRepository;
import com.e_onlineshoppingwebsite.store.Service.RolesService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RolesServiceImpl implements RolesService {

    private final RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleById(Integer id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò"));
    }

    @Override
    public Optional<Role> findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    @Override
    public boolean existsByRoleName(String roleName) {
        return roleRepository.existsByRoleName(roleName);
    }

    @Override
    public void save(Role role) {
        if (role == null || role.getRoleName() == null) {
            throw new IllegalArgumentException("Vai trò không hợp lệ");
        }

        if (roleRepository.existsByRoleName(role.getRoleName())) {
            throw new IllegalArgumentException("Tên vai trò đã tồn tại");
        }

        role.setRoleId(null);
        roleRepository.save(role);
    }

    @Override
    public void update(Role role) {
        if (role == null || role.getRoleId() == null) {
            throw new IllegalArgumentException("Thiếu ID vai trò");
        }

        Role old = getRoleById(role.getRoleId());

        if (!old.getRoleName().equals(role.getRoleName())
                && roleRepository.existsByRoleName(role.getRoleName())) {
            throw new IllegalArgumentException("Tên vai trò đã tồn tại");
        }

        old.setRoleName(role.getRoleName());
        old.setDescription(role.getDescription());

        roleRepository.save(old);
    }

    @Override
    public void deleteRole(Integer id) {
        Role role = getRoleById(id);
        roleRepository.delete(role);
    }
}
