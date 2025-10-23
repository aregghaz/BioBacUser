package com.biobac.users;

import com.biobac.users.entity.Permission;
import com.biobac.users.entity.Role;
import com.biobac.users.entity.User;
import com.biobac.users.repository.PermissionRepository;
import com.biobac.users.repository.RoleRepository;
import com.biobac.users.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class UsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersApplication.class, args);
    }

    @Bean
    CommandLineRunner seedRolesAndPermissions(RoleRepository roleRepository, PermissionRepository permissionRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Map<String, String> entityRuMap = Map.ofEntries(
                    Map.entry("USER", "Пользователь"),
                    Map.entry("WAREHOUSE", "Склад"),
                    Map.entry("PRODUCT", "Продукт"),
                    Map.entry("INGREDIENT", "Ингредиент"),
                    Map.entry("INGREDIENT_GROUP", "Группа ингредиентов"),
                    Map.entry("RECIPE_ITEM", "Рецепт"),
                    Map.entry("PRODUCT_HISTORY", "История продукта"),
                    Map.entry("INGREDIENT_HISTORY", "История ингредиента"),
                    Map.entry("UNIT", "Единица измерения"),
                    Map.entry("UNIT_TYPE", "Тип единицы"),
                    Map.entry("POSITION", "Должность"),
                    Map.entry("PERMISSION", "Разрешение"),
                    Map.entry("COMPANY", "Компания"),
                    Map.entry("ATTRIBUTE", "Атрибут"),
                    Map.entry("ATTRIBUTE_GROUP", "Группа атрибутов"),
                    Map.entry("COMPANY_TYPE", "Тип компании"),
                    Map.entry("REGION", "Регион"),
                    Map.entry("COMPANY_SALE_TYPE", "Тип продаж компании"),
                    Map.entry("ASSET", "Актив"),
                    Map.entry("ASSET_CATEGORY", "Категория актива"),
                    Map.entry("ASSET_IMPROVEMENT", "Улучшение актива"),
                    Map.entry("DEPARTMENT", "Отдел"),
                    Map.entry("DEPRECIATION_RECORD", "Запись амортизации"),
                    Map.entry("EXPENSE_TYPE", "Тип расхода"),
                    Map.entry("INGREDIENT_BALANCE", "Баланс ингредиентов"),
                    Map.entry("INGREDIENT_DETAIL", "Деталь ингредиента"),
                    Map.entry("MANUFACTURE_PRODUCT", "Производимый продукт"),
                    Map.entry("PRODUCT_BALANCE", "Баланс продукта"),
                    Map.entry("PRODUCT_DETAIL", "Деталь продукта"),
                    Map.entry("PRODUCT_GROUP", "Группа продуктов"),
                    Map.entry("RECEIVE_EXPENSE", "Полученный расход"),
                    Map.entry("RECEIVE_INGREDIENT", "Полученный ингредиент"),
                    Map.entry("WAREHOUSE_GROUP", "Группа складов"),
                    Map.entry("WAREHOUSE_TYPE", "Тип склада")
            );

            Map<String, String> operationRuMap = Map.of(
                    "READ", "Просмотр",
                    "CREATE", "Создание",
                    "UPDATE", "Редактирование",
                    "DELETE", "Удаление"
            );

            List<String> entities = new ArrayList<>(entityRuMap.keySet());
            List<String> operations = List.of("READ", "CREATE", "UPDATE", "DELETE");

            Set<Permission> allPermissions = new HashSet<>();
            for (String entity : entities) {
                for (String op : operations) {
                    String permName = entity + "_" + op;
                    String title = operationRuMap.get(op) + " " + entityRuMap.getOrDefault(entity, entity);

                    Permission p = permissionRepository.findByName(permName)
                            .orElseGet(() -> {
                                Permission np = new Permission();
                                np.setName(permName);
                                np.setTitle(title);
                                return permissionRepository.save(np);
                            });

                    // Update title if missing or outdated
                    if (p.getTitle() == null || !p.getTitle().equals(title)) {
                        p.setTitle(title);
                        permissionRepository.save(p);
                    }

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
            adminUser.setPermissions(allPermissions);
            userRepository.save(adminUser);
        };
    }
}
