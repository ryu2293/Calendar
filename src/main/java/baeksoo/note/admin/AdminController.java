package baeksoo.note.admin;

import baeksoo.note.member.CustomUser;
import baeksoo.note.member.Member;
import baeksoo.note.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/write")
    public String write(){
        return "write.html";
    }

    @PostMapping("/writeAct")
    @PreAuthorize("isAuthenticated()")
    public String writeAct(Authentication auth, String title, String memo, Model model){
        if(auth == null){
            String message = "문의 접수를 위해 로그인이 필요합니다.";
            model.addAttribute("message", message);
            return "write.html";
        }
        var user = (CustomUser) auth.getPrincipal();

        Admin admin = new Admin();
        admin.setTitle(title);
        admin.setMemo(memo);

        Member member = new Member();
        member.setId(user.id);
        admin.setMember(member);

        adminRepository.save(admin);
        return "redirect:/";
    }

    @GetMapping("/list/{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('관리자')")
    public String list(Model model, @PathVariable Integer id){
        Page<Admin> list = adminRepository.findAll(PageRequest.of(id-1, 8));
        if(list.isEmpty() || list == null){
            return "error.html";
        }
        var result = list.get();
        model.addAttribute("list", result);
        model.addAttribute("pages", list.getTotalPages());
        return "list.html";
    }

    @GetMapping("/allMember/{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('관리자')")
    public String allMember(Model model, @PathVariable Integer id){
        Page<Member> members = memberRepository.findAll(PageRequest.of(id-1, 12));
        if(members.isEmpty() || members == null){
            return "error.html";
        }
        model.addAttribute("members", members.get());
        model.addAttribute("pages", members.getTotalPages());
        return "allMember.html";
    }

    @PostMapping("/admin/delete/{id}")
    @PreAuthorize("hasAuthority('관리자')")
    public String deleteMember(@PathVariable Long id, RedirectAttributes redirectAttributes){
        memberRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "회원탈퇴 되었습니다!");
        return "redirect:/allMember/1";
    }

    @GetMapping("/admin/answer/{id}")
    @PreAuthorize("hasAuthority('관리자')")
    public String showAnswerForm(@PathVariable Long id, Model model) {
        Optional<Admin> admin = adminRepository.findById(id);
        if (admin.isEmpty()) {
            return "error.html";
        }
        var result = admin.get();
        model.addAttribute("admin", result);
        return "answer.html";
    }

    @PostMapping("/admin/answerAct")
    @PreAuthorize("hasAuthority('관리자')")
    public String answer(Long id, String answer, RedirectAttributes redirectAttributes) {
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) {
            redirectAttributes.addFlashAttribute("message", "문의가 존재하지 않습니다.");
            return "redirect:/list/1";
        }
        admin.setAnswer(answer);
        adminRepository.save(admin);
        redirectAttributes.addFlashAttribute("message", "답변이 등록되었습니다.");
        return "redirect:/list/1";
    }


}
