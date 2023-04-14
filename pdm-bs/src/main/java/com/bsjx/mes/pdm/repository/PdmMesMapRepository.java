package com.bsjx.mes.pdm.repository;

import com.bsjx.mes.pdm.entity.PdmMesMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdmMesMapRepository extends JpaRepository<PdmMesMap, String> {
}
