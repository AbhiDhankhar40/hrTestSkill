package com.test.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.test.demo.model.GroupMaster;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMasterRepository extends JpaRepository<GroupMaster, Long> {

    Optional<GroupMaster> findByGroupCode(String groupCode);

    boolean existsByGroupCode(String groupCode);

  @Query("""
    SELECT ug.group
    FROM UserGroupMapping ug
    WHERE ug.user.id = :userId
""")
List<GroupMaster> findGroupsByUserId(Long userId);


}
