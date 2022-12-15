#### BridgeGame에서 Diagram

- StringJoiner 활용

``` 
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(formatDiagram(upDiagram));
        result.append(formatDiagram(downDiagram));

        return result.toString();
    }

    private String formatDiagram(List<String> diagrams) {
        StringJoiner stringJoiner = new StringJoiner(" | ", "[ ", " ]\n");
        for (String diagram : diagrams) {
            stringJoiner.add(diagram);
        }
        return stringJoiner.toString();
    }
```

## ResultFormatter

- 결과값을 출력하는 과정이 복잡할 때
- 결과값을 formatting하는 일을 model이 하는 것은 부적절, view에서 하기에도 클 수 있다.


- 이외의 `REGEX`를 활용한 `formatting`

```
   private enum Regex {
        CASH_PRIZE_REGEX("\\B(?=(\\d{3})+(?!\\d))"),
        DECIMAL_FORMAT("#,##0.0");

        private final String regex;

        Regex(String regex) {
            this.regex = regex;
        }
    }

    // 12345 => 12,345
    public static String formatRewardRate(BigDecimal rewardRate) {
        return new DecimalFormat(Regex.DECIMAL_FORMAT.regex).format(rewardRate);
    }

    // 324329209.35823 => 324,329,209.4
    private static String formatCashPrize(int cashPrize) {
        return String.valueOf(cashPrize).replaceAll(Regex.CASH_PRIZE_REGEX.regex, ",");
    }
```
