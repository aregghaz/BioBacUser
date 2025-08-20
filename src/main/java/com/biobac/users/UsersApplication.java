package com.biobac.users;

import com.biobac.users.entity.Permission;
import com.biobac.users.entity.Role;
import com.biobac.users.repository.PermissionRepository;
import com.biobac.users.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication
public class UsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersApplication.class, args);
    }

    @Bean
    CommandLineRunner seedRolesAndPermissions(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        return args -> {
            // Define entities in the system
            List<String> entities = List.of(
                    "USER", "WAREHOUSE", "PRODUCT", "INGREDIENT",
                    "INGREDIENT_GROUP", "INGREDIENT_COMPONENT", "RECIPE_ITEM"
            );

            // Define operations
            List<String> operations = List.of("READ", "CREATE", "UPDATE", "DELETE");

            // Ensure all permissions exist
            Set<Permission> allPermissions = new HashSet<>();
            for (String entity : entities) {
                for (String op : operations) {
                    String permName = entity + "_" + op;
                    Permission p = permissionRepository.findByName(permName)
                            .orElseGet(() -> {
                                Permission np = new Permission();
                                np.setName(permName);
                                return permissionRepository.save(np);
                            });
                    allPermissions.add(p);
                }
            }

            // ===== GLOBAL ROLES =====
            // SUPER_ADMIN -> all permissions
            Role superAdmin = roleRepository.findByName("ROLE_SUPER_ADMIN").orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_SUPER_ADMIN");
                r.setPermissions(new HashSet<>(allPermissions));
                return roleRepository.save(r);
            });
            superAdmin.setPermissions(new HashSet<>(allPermissions));
            roleRepository.save(superAdmin);

            // ADMIN -> all permissions (same as superAdmin for now, but can restrict later)
            Role globalAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_ADMIN");
                r.setPermissions(new HashSet<>(allPermissions));
                return roleRepository.save(r);
            });
            globalAdmin.setPermissions(new HashSet<>(allPermissions));
            roleRepository.save(globalAdmin);

            // ===== ENTITY-SPECIFIC ROLES =====
            for (String entity : entities) {
                // Collect perms by operation
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
                    r.setPermissions(new HashSet<>(entityAllPerms));
                    return roleRepository.save(r);
                });
                entityAdmin.setPermissions(entityAllPerms);
                roleRepository.save(entityAdmin);

                // ROLE_<ENTITY>_MANAGER
                Role entityManager = roleRepository.findByName("ROLE_" + entity + "_MANAGER").orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_" + entity + "_MANAGER");
                    r.setPermissions(new HashSet<>(entityManagerPerms));
                    return roleRepository.save(r);
                });
                entityManager.setPermissions(entityManagerPerms);
                roleRepository.save(entityManager);

                // ROLE_<ENTITY>_USER
                Role entityUser = roleRepository.findByName("ROLE_" + entity + "_USER").orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_" + entity + "_USER");
                    r.setPermissions(new HashSet<>(entityUserPerms));
                    return roleRepository.save(r);
                });
                entityUser.setPermissions(entityUserPerms);
                roleRepository.save(entityUser);
            }
        };
    }
}
