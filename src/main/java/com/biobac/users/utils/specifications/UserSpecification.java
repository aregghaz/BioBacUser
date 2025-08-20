package com.biobac.users.utils.specifications;

import com.biobac.users.entity.Permission;
import com.biobac.users.entity.Role;
import com.biobac.users.entity.User;
import com.biobac.users.request.FilterCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.biobac.users.utils.SpecificationUtil.*;


public class UserSpecification {
    public static Specification<User> buildSpecification(Map<String, FilterCriteria> filters) {
        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            if (filters != null) {
                for (Map.Entry<String, FilterCriteria> entry : filters.entrySet()) {
                    String field = entry.getKey();
                    Path<?> path;
                    FilterCriteria criteria = entry.getValue();
                    Predicate predicate = null;

                    if (field.equalsIgnoreCase("roleName")) {
                        Join<User, Role> roleJoin = root.join("roles");
                        path = roleJoin.get("id");
                    } else if (field.equalsIgnoreCase("permissionName")) {
                        Join<User, Role> roleJoin = root.join("roles");
                        Join<Role, Permission> permJoin = roleJoin.join("permissions");
                        path = permJoin.get("id");
                    } else {
                        path = root.get(field);
                    }

                    switch (criteria.getOperator()) {
                        case "equals" -> predicate = buildEquals(cb, path, criteria.getValue());
                        case "notEquals" -> predicate = buildNotEquals(cb, path, criteria.getValue());
                        case "contains" -> {
                            if (criteria.getValue() instanceof Iterable<?>) {
                                CriteriaBuilder.In<Object> inClause = cb.in(path);
                                for (Object val : (Iterable<?>) criteria.getValue()) {
                                    inClause.value(val);
                                }
                                predicate = inClause;
                            } else {
                                predicate = cb.like(cb.lower(path.as(String.class)),
                                        criteria.getValue().toString().toLowerCase().trim().replaceAll("\\s+", " "));
                            }
                        }
                        case "greaterThanOrEqualTo" ->
                                predicate = buildGreaterThanOrEqualTo(cb, path, criteria.getValue());
                        case "lessThanOrEqualTo" -> predicate = buildLessThanOrEqualTo(cb, path, criteria.getValue());
                        case "between" -> predicate = buildBetween(cb, path, criteria.getValue());
                    }

                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
