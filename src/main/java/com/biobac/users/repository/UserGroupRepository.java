package com.biobac.users.repository;

import com.biobac.users.entity.GroupType;
import com.biobac.users.entity.User;
import com.biobac.users.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    List<UserGroup> findByUserAndGroupType(User user, GroupType groupType);

    void deleteByUser(User user);
}
