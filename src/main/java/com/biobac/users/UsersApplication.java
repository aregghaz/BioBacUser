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

@SpringBootApplication
public class UsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersApplication.class, args);
	}

	@Bean
	CommandLineRunner seedRolesAndPermissions(RoleRepository roleRepository, PermissionRepository permissionRepository) {
		return args -> {
			// Define static permissions
			List<String> permissionNames = List.of(
					"USER_READ",
					"USER_CREATE",
					"USER_UPDATE",
					"USER_DELETE"
			);

			// Ensure permissions exist
			Set<Permission> allPermissions = new HashSet<>();
			for (String pname : permissionNames) {
				Permission p = permissionRepository.findByName(pname)
						.orElseGet(() -> {
							Permission np = new Permission();
							np.setName(pname);
							return permissionRepository.save(np);
						});
				allPermissions.add(p);
			}

			// ROLE_ADMIN with all permissions
			Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
				Role r = new Role();
				r.setName("ROLE_ADMIN");
				r.setPermissions(new HashSet<>(allPermissions));
				return roleRepository.save(r);
			});
			// Ensure admin has all permissions if created earlier without them
			if (adminRole.getPermissions() == null || adminRole.getPermissions().size() != allPermissions.size()) {
				adminRole.setPermissions(new HashSet<>(allPermissions));
				roleRepository.save(adminRole);
			}

			// ROLE_USER with read permission
			Permission readPerm = permissionRepository.findByName("USER_READ").orElse(null);
			Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
				Role r = new Role();
				r.setName("ROLE_USER");
				HashSet<Permission> perms = new HashSet<>();
				if (readPerm != null) perms.add(readPerm);
				r.setPermissions(perms);
				return roleRepository.save(r);
			});
			if (userRole.getPermissions() == null) {
				userRole.setPermissions(new HashSet<>());
			}
			if (readPerm != null && userRole.getPermissions().stream().noneMatch(p -> p.getName().equals("USER_READ"))) {
				userRole.getPermissions().add(readPerm);
				roleRepository.save(userRole);
			}
		};
	}
}
