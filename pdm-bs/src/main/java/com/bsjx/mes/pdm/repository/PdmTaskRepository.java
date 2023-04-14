package com.bsjx.mes.pdm.repository;

import com.bsjx.mes.pdm.entity.PdmTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdmTaskRepository extends JpaRepository<PdmTask, Long> {
    Page<PdmTask> findByStatus(String status, Pageable pageable);
}
