### 큰 숫자 다루기 `BigDecimal`

- 소수점, 반올림을 빡세게 요구할 때
- 너무 복잡한 수나, 아니면 돈을 다루는 경우에 바로 사용하자!
- 퍼센트를 구한 다음에 `1,000.3%` 꼴로 출력하자
- `ArithmeticException`을 조심하자!

- dividend, divisor, quotient
  ![img.png](../img.png)

- 생성은 무조건 셋 중 하나
    - `BigDecimal TWO_HUNDRED = new BigDecimal("200");`
    - `BigDecimal ZERO = BigDecimal.ZERO;`
    - `BigDecimal cashPrize = new BigDecimal(String.valueOf(winningRank.getCashPrize()))`

- `add`, `subtract`만 해놓으면 아무 변화 없음 받아줘야함!!!

``` 
 private void setRewardRate() {
        BigDecimal ticketBudget = BigDecimal.ZERO;
        BigDecimal totalCashPrize = BigDecimal.ZERO;
        for (PlayerNumber player : playerNumbers.getPlayerNumbers()) {
            WinningRank winningRank = WinningRank.from(calculateMatch(player), hasBonus(player));
            ticketBudget = ticketBudget.add(new BigDecimal(String.valueOf(LOTTO_PRICE))); // 받아줘!!!
            totalCashPrize = totalCashPrize.add(new BigDecimal(String.valueOf(winningRank.getCashPrize())));
        }
        rewardRate = calculateRewardRate(totalCashPrize, ticketBudget);
    }
```

- 소수점 아래 둘째 자리에서 **반올림** `1.35 => 1.4`

```
    private static BigDecimal getSetScale(BigDecimal rewardRate) {
        return rewardRate.setScale(1, RoundingMode.HALF_EVEN);
    }
```

- 퍼센트 구하기 `totalCashPrize / ticketBudget * 100` + 소수점 아래 둘째 자리에서 반올림

```
 private static BigDecimal calculateRewardRate(BigDecimal totalCashPrize, BigDecimal ticketBudget) {
        if (ticketBudget.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        return totalCashPrize.multiply(new BigDecimal("100")).divide(ticketBudget, 1, RoundingMode.HALF_EVEN);
    }
```