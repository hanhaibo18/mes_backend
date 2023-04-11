package com.bsjx.mes.pdm.repository;

import com.bsjx.mes.pdm.entity.PdmLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdmLogRepository extends JpaRepository<PdmLog, String> {
}
