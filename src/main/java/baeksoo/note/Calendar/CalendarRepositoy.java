package baeksoo.note.Calendar;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CalendarRepositoy extends JpaRepository<Calendar, Long> {
    List<Calendar> findByMemberId(Long memberId);
}
