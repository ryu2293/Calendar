package baeksoo.note.MontlyStatistics;

import baeksoo.note.Calendar.Calendar;
import baeksoo.note.Calendar.CalendarRepositoy;
import baeksoo.note.member.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MonthlyStatisticsController {
    private final CalendarRepositoy calendarRepositoy;

    @GetMapping("/statistics")
    public String showStatisticsPage() {
        return "statistics";
    }

    @GetMapping("/api/statistics")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> getMonthlyStatistics(
            @RequestParam String month,
            Authentication auth) {

        var user = (CustomUser) auth.getPrincipal();
        List<Calendar> entries = calendarRepositoy.findByMemberIdAndMonth(user.id, month);

        int totalIncome = 0;
        int totalExpense = 0;
        int incomeCount = 0;
        int expenseCount = 0;

        for (Calendar entry : entries) {
            int amount = Integer.parseInt(entry.getAmount());
            if ("수입".equals(entry.getType())) {
                totalIncome += amount;
                incomeCount++;
            } else if ("지출".equals(entry.getType())) {
                totalExpense += amount;
                expenseCount++;
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("month", month);
        stats.put("totalIncome", totalIncome);
        stats.put("totalExpense", totalExpense);
        stats.put("incomeCount", incomeCount);
        stats.put("expenseCount", expenseCount);

        return stats;
    }

}
