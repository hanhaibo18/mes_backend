package com.bsjx.mes.pdm.repository;

import com.bsjx.mes.pdm.entity.PdmStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PdmStepRepository extends JpaRepository<PdmStep, String> {
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "delete from PdmStep s where s.opId=?1 and s.dataGroup=?2")
    int deleteByOpId(String opId, String dataGroup);
}
