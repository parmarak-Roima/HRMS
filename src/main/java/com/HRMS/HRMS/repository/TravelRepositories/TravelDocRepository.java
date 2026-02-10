package com.HRMS.HRMS.repository.TravelRepositories;

import com.HRMS.HRMS.entity.TravelEntities.TravelDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TravelDocRepository extends JpaRepository<TravelDoc,Long> {

    //all the docs for hr
    List<TravelDoc> findByTravelId(Long travelId);

    //shared docs
    List<TravelDoc> findByTravelIdAndOwnerIsNull(Long travelId);

    //for employee shared and specific
    @Query("SELECT d FROM TravelDoc d WHERE d.travel.id = :travelId AND (d.owner.id = :employeeId OR d.owner IS NULL)")
    List<TravelDoc> findByTravelIdAndEmployeeId(Long travelId, Long employeeId);

}
