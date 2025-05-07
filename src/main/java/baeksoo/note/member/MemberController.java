package baeksoo.note.member;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그인 페이지
    @GetMapping("/login")
    public String login(){
        return "login.html";
    }

    // 회원가입 페이지
    @GetMapping("/signup")
    public String signup(){
        return "signup.html";
    }

    // 회원가입 api
    @PostMapping("/signupAct")
    public String signup(String username, String email, String displayName, String password, String rePw, Model model, RedirectAttributes redirectAttributes){
        // 공백, 부적절한 ID 및 PW 등 오류 확인
        String error = null;
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()
                || rePw == null || rePw.trim().isEmpty() || displayName == null || displayName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
        ) {
            error = "빈칸을 모두 채우세요!";
        }
        // 비밀번호 일치 여부 확인
        if(!password.equals(rePw)){
            error = "비밀번호가 동일하지 않습니다.";
        }
        if(password.length() < 6){
            error = "비밀번호는 6자 이상 작성해야합니다.";
        }
        // 비밀번호 문자 및 특수기호 포함.
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$";
        if (!Pattern.matches(passwordPattern, password)) {
            error = "비밀번호는 문자, 숫자, 특수기호(@$!%*#?&)를 포함해야 합니다.";
        }
        if(error != null){
            model.addAttribute("error", error);
            return "signup.html";
        }

        // 비밀번호 암호화
        String hash = passwordEncoder.encode(password);
        Member member = new Member();
        member.setUsername(username);
        member.setEmail(email);
        member.setDisplayName(displayName);
        member.setPassword(hash);

        memberRepository.save(member);

        redirectAttributes.addFlashAttribute("success", "티끌모아의 회원이 되신 것을 환영합니다!");
        return "redirect:/login";
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
}
