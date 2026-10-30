package com.e_onlineshoppingwebsite.store.Service;

import java.util.List;
import java.util.Optional;

import com.e_onlineshoppingwebsite.store.Model.Role;

public interface RolesService {

    List<Role> getAllRoles();

    Role getRoleById(Integer id);

    Optional<Role> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);

    void save(Role role);

    void update(Role role);

    void deleteRole(Integer id);
}