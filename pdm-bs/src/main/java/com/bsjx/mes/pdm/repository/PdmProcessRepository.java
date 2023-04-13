package com.bsjx.mes.pdm.repository;

import com.bsjx.mes.pdm.entity.PdmProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdmProcessRepository extends JpaRepository<PdmProcess, String> {
}
