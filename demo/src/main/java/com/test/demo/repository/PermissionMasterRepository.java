package com.test.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.test.demo.model.PermissionMaster;
import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionMasterRepository extends JpaRepository<PermissionMaster, Long> {

    Optional<PermissionMaster> findByPermissionCode(String permissionCode);

    boolean existsByPermissionCode(String permissionCode);

    
@Query("""
    SELECT DISTINCT p
    FROM PermissionMaster p
    JOIN GroupPermissionMapping gpm ON gpm.permission = p
    JOIN UserGroupMapping ugm ON ugm.group = gpm.group
    LEFT JOIN FETCH p.subModules sm
    LEFT JOIN FETCH sm.module
    LEFT JOIN FETCH p.buttons b
    WHERE ugm.user.id = :userId
""")
List<PermissionMaster> findPermissionsByUserId(@Param("userId") Long userId);
}

