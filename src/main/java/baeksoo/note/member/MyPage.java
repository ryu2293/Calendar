package baeksoo.note.member;

import baeksoo.note.admin.Admin;
import baeksoo.note.admin.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MyPage {

    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;

    // 문의 내역 페이지
    @GetMapping("/myInquiry")
    @PreAuthorize("isAuthenticated()")
    public String myInquiry(Model model, @AuthenticationPrincipal CustomUser userDetails) {
        Optional<Member> optionalMember = memberRepository.findById(userDetails.id);
        if (optionalMember.isEmpty()) return "error.html";

        Member member = optionalMember.get();
        List<Admin> myInquiries = adminRepository.findByMember(member);

        model.addAttribute("list", myInquiries);
        return "myInquiry.html";
    }

    // 답변 확인 페이지
    @GetMapping("/inquiry/{id}")
    @PreAuthorize("isAuthenticated()")
    public String inquiryDetail(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUser userDetails) {
        Optional<Admin> optional = adminRepository.findById(id);
        if (optional.isEmpty()) return "error.html";

        Admin inquiry = optional.get();
        if (!inquiry.getMember().getId().equals(userDetails.id)) {
            return "error.html";
        }

        model.addAttribute("admin", inquiry);
        return "inquiryDetail.html";
    }
}
