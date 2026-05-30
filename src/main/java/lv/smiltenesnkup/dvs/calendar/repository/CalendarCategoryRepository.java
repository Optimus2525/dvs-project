package lv.smiltenesnkup.dvs.calendar.repository;

import lv.smiltenesnkup.dvs.calendar.model.CalendarCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Nodrošina datu bāzes operācijas kalendāra kategorijām.
 */
@Repository
public interface CalendarCategoryRepository extends JpaRepository<CalendarCategory, Long> {

    /**
     * Atrod visas aktīvās kalendāra kategorijas.
     */
    List<CalendarCategory> findAllByActiveTrueOrderByNameAsc();


}