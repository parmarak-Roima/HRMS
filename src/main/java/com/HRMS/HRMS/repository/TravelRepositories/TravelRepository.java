package com.HRMS.HRMS.repository.TravelRepositories;

import com.HRMS.HRMS.entity.Enums.TravelStatus;
import com.HRMS.HRMS.entity.TravelEntities.Travel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Long> {
    List<Travel> findByCreatedById(Long hrId);

        @Transactional
        @Modifying
        @Query("UPDATE Travel t SET t.status = :newStatus WHERE t.endDate < :today AND t.status = :oldStatus ")
        int updateStatus(TravelStatus oldStatus , TravelStatus newStatus , LocalDate today);
}

