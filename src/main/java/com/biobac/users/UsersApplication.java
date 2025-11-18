package com.biobac.users;

import com.biobac.users.entity.Permission;
import com.biobac.users.entity.Role;
import com.biobac.users.entity.User;
import com.biobac.users.repository.PermissionRepository;
import com.biobac.users.repository.PositionRepository;
import com.biobac.users.repository.RoleRepository;
import com.biobac.users.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.biobac.users.utils.PermissionDictionary;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class UsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersApplication.class, args);
    }

    @Bean
    CommandLineRunner seedRolesAndPermissions(RoleRepository roleRepository, PermissionRepository permissionRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, PositionRepository positionRepository) {
        return args -> {
            List<String> entities = new ArrayList<>(PermissionDictionary.ENTITY_RU.keySet());
            List<String> operations = new ArrayList<>(PermissionDictionary.OPERATION_RU.keySet());

            Set<Permission> allPermissions = new HashSet<>();

            for (var entry : PermissionDictionary.SPECIAL_PERMISSIONS.entrySet()) {
                String name = entry.getKey();
                String title = entry.getValue();

                Permission perm = permissionRepository.findByName(name)
                        .orElseGet(() -> {
                            Permission p = new Permission();
                            p.setName(name);
                            p.setTitle(title);
                            return permissionRepository.save(p);
                        });

                // update title if needed
                if (perm.getTitle() == null || !perm.getTitle().equals(title)) {
                    perm.setTitle(title);
                    permissionRepository.save(perm);
                }

                allPermissions.add(perm);
            }

            for (String entity : entities) {
                for (String op : operations) {
                    String permName = entity + "_" + op;
                    String title = PermissionDictionary.OPERATION_RU.get(op) + " " + PermissionDictionary.ENTITY_RU.getOrDefault(entity, entity);

                    Permission perm = permissionRepository.findByName(permName)
                            .orElseGet(() -> {
                                Permission p = new Permission();
                                p.setName(permName);
                                p.setTitle(title);
                                return permissionRepository.save(p);
                            });

                    // Update title if missing or outdated
                    if (perm.getTitle() == null || !perm.getTitle().equals(title)) {
                        perm.setTitle(title);
                        permissionRepository.save(perm);
                    }

                    allPermissions.add(perm);
                }
            }

            // ===== GLOBAL ROLES =====
            // SUPER_ADMIN -> all permissions
            Role superAdmin = roleRepository.findByName("ROLE_SUPER_ADMIN").orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_SUPER_ADMIN");
                return roleRepository.save(r);
            });
            superAdmin.setPermissions(new HashSet<>(allPermissions));
            roleRepository.save(superAdmin);

            // ADMIN -> all permissions (same as super admin for now)
            Role globalAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_ADMIN");
                return roleRepository.save(r);
            });
            globalAdmin.setPermissions(new HashSet<>(allPermissions));
            roleRepository.save(globalAdmin);

            // ===== ENTITY-SPECIFIC ROLES =====
            for (String entity : entities) {
                Set<Permission> entityAllPerms = allPermissions.stream()
                        .filter(p -> p.getName().startsWith(entity + "_"))
                        .collect(Collectors.toSet());

                Set<Permission> entityManagerPerms = entityAllPerms.stream()
                        .filter(p -> !p.getName().endsWith("_DELETE"))
                        .collect(Collectors.toSet());

                Set<Permission> entityUserPerms = entityAllPerms.stream()
                        .filter(p -> p.getName().endsWith("_READ"))
                        .collect(Collectors.toSet());

                // ROLE_<ENTITY>_ADMIN
                Role entityAdmin = roleRepository.findByName("ROLE_" + entity + "_ADMIN").orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_" + entity + "_ADMIN");
                    return roleRepository.save(r);
                });
                entityAdmin.setPermissions(entityAllPerms);
                roleRepository.save(entityAdmin);

                // ROLE_<ENTITY>_MANAGER
                Role entityManager = roleRepository.findByName("ROLE_" + entity + "_MANAGER").orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_" + entity + "_MANAGER");
                    return roleRepository.save(r);
                });
                entityManager.setPermissions(entityManagerPerms);
                roleRepository.save(entityManager);

                // ROLE_<ENTITY>_USER
                Role entityUser = roleRepository.findByName("ROLE_" + entity + "_USER").orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_" + entity + "_USER");
                    return roleRepository.save(r);
                });
                entityUser.setPermissions(entityUserPerms);
                roleRepository.save(entityUser);
            }

            // ===== DEFAULT ADMIN USER SEEDING =====
            User adminUser = userRepository.findByUsername("biobacadmin@admin.com").orElse(null);
            if (adminUser == null) {
                adminUser = new User();
                adminUser.setUsername("biobacadmin@admin.com");
                adminUser.setFirstname("JOHNY");
                adminUser.setLastname("SILVERHAND");
                adminUser.setPhoneNumber("1231231234");
                adminUser.setEmail("biobacadmin@admin.com");
                adminUser.setPassword(passwordEncoder.encode("password"));
                adminUser.setActive(true);
            }

            adminUser.setPermissions(new HashSet<>(allPermissions));

            userRepository.save(adminUser);
        };
    }
}
