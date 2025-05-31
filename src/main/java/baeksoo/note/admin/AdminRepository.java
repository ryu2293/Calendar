package baeksoo.note.admin;

import baeksoo.note.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Page<Admin> findAll(Pageable pageable);
    List<Admin> findByMember(Member member);

}
