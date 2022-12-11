import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class MapPractice {
    public static void main(String args[]) {
        printByIteratingMap();

        BigDecimal rewardRate = calculatePercent(new BigDecimal("500000"), new BigDecimal("8000"));
        System.out.println(rewardRate); // 6250.0

        String formattedRate = formatRewardRate(new BigDecimal("324329209.35823"));
        System.out.println(formattedRate); // 324,329,209.4

        String formattedCashPrize = formatCashPrize(12345);
        System.out.println(formattedCashPrize); // 12,345

    }


    // 퍼센트 구하기
    private static BigDecimal calculatePercent(BigDecimal totalCashPrize, BigDecimal ticketBudget) {
        if (ticketBudget.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        return totalCashPrize.multiply(new BigDecimal("100")).divide(ticketBudget, 1, RoundingMode.HALF_EVEN);
    }

    //
    private enum Regex {
        CASH_PRIZE_REGEX("\\B(?=(\\d{3})+(?!\\d))"),
        DECIMAL_FORMAT("#,##0.0");

        private final String regex;

        Regex(String regex) {
            this.regex = regex;
        }
    }


    // 3,250.5 모양처럼 출력
    private static String formatCashPrize(int cashPrize) {
        return String.valueOf(cashPrize).replaceAll(Regex.CASH_PRIZE_REGEX.regex, ",");
    }

    // `REGEX`에 따라 `format`하기
    public static String formatRewardRate(BigDecimal rewardRate) {
        return new DecimalFormat(Regex.DECIMAL_FORMAT.regex).format(rewardRate);
    }


    //
    private static void printByIteratingMap() {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(100, 1);
        map.put(200, 2);
        for (Map.Entry<Integer, Integer> element : map.entrySet()) {
            System.out.println(String.format("%d원 - %d개", element.getKey(), element.getValue()));
        }
    }

}


