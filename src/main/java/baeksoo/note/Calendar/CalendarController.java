package baeksoo.note.Calendar;

import baeksoo.note.member.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarRepositoy calendarRepositoy;

    @GetMapping("/")
    public String hello() {
        return "index.html";
    }

    // 가계부 등록
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/transactions")
    // 예외 처리
    public String transactions(String date, String type, String amount, Authentication auth, RedirectAttributes redirectAttributes){
        if(auth == null){
            String message = "로그인이 필요합니다!";
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/login";
        }

        if(date == null || date.trim().isEmpty() || type == null || type.trim().isEmpty() || amount == null || amount.trim().isEmpty()){
            redirectAttributes.addFlashAttribute("message", "내역 저장에 실패하였습니다!");
            return "redirect:/";
        }
        // calendar 클래스에 수입/지출 내용 저장
        Calendar calendar = new Calendar();
        calendar.setDate(date);
        calendar.setType(type);
        calendar.setAmount(amount);
        var user = (CustomUser) auth.getPrincipal();
        calendar.setMemberId(user.id);
        calendarRepositoy.save(calendar);

        redirectAttributes.addFlashAttribute("message", "내역이 추가되었습니다!");

        return "redirect:/";

    }

    @GetMapping("/api/transactions")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public List<Map<String, Object>> getUserTransactions(Authentication auth) {
        // 로그인 한 정보의 수입/지출 내역 가져오기
        var user = (CustomUser) auth.getPrincipal();
        List<Calendar> entries = calendarRepositoy.findByMemberId(user.id);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Calendar entry : entries) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", entry.getId());
            map.put("title", entry.getType() + " " + entry.getAmount() + "원");
            map.put("start", entry.getDate());

            if("수입".equals(entry.getType())){
                map.put("color", "green");
            }
            else if("지출".equals(entry.getType())){
                map.put("color", "red");
            }

            result.add(map);
        }
        return result;
    }

    @DeleteMapping("/api/transactions/{id}")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public String delete(Authentication auth, @PathVariable Long id) {
        Optional<Calendar> result = calendarRepositoy.findById(id);
        if(result.isEmpty()){
            throw new RuntimeException("내역이 존재하지 않습니다.");
        }
        calendarRepositoy.deleteById(id);
        return "ok";
    }

}
