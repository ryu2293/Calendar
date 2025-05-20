package baeksoo.note.Calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CalendarRepositoy extends JpaRepository<Calendar, Long> {
    List<Calendar> findByMemberId(Long memberId);

    @Query("SELECT c FROM Calendar c WHERE c.memberId = :memberId AND FUNCTION('DATE_FORMAT', c.date, '%Y-%m') = :month")
    List<Calendar> findByMemberIdAndMonth(@Param("memberId") Long memberId, @Param("month") String month);
}
