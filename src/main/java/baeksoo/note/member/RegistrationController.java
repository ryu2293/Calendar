package baeksoo.note.member.controller;

import baeksoo.note.member.Member;
import baeksoo.note.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String signup() {
        return "signup.html";
    }

    @PostMapping("/signupAct")
    public String signupAct(String username, String email, String displayName,
                            String password, String rePw, Model model, RedirectAttributes redirectAttributes) {

        String error = validateInput(username, email, displayName, password, rePw);
        if (error != null) {
            model.addAttribute("error", error);
            return "signup.html";
        }

        String encodedPassword = passwordEncoder.encode(password);
        Member member = new Member();
        member.setUsername(username);
        member.setEmail(email);
        member.setDisplayName(displayName);
        member.setPassword(encodedPassword);
        memberRepository.save(member);

        redirectAttributes.addFlashAttribute("success", "티끌모아의 회원이 되신 것을 환영합니다!");
        return "redirect:/login";
    }

    private String validateInput(String username, String email, String displayName, String password, String rePw) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                rePw == null || rePw.trim().isEmpty() ||
                displayName == null || displayName.trim().isEmpty() ||
                email == null || email.trim().isEmpty()) {
            return "빈칸을 모두 채우세요!";
        }

        if (!password.equals(rePw)) {
            return "비밀번호가 동일하지 않습니다.";
        }

        if (password.length() < 6) {
            return "비밀번호는 6자 이상 작성해야합니다.";
        }

        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$";
        if (!Pattern.matches(pattern, password)) {
            return "비밀번호는 문자, 숫자, 특수기호(@$!%*#?&)를 포함해야 합니다.";
        }

        if (memberRepository.findByUsername(username).isPresent()) {
            return "이미 존재하는 아이디입니다.";
        }

        return null;
    }
}
