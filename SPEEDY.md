### START

- `controller` `model` `util` `view` 패키지 생성
- `util` 패키지 안에 `Util` 클래스 생성 (여러번 사용되는 것들)
- `util` 패키지 안에 `validator` 패키지 생성



#### Application

```
public class Application {
    public static void main(String[] args) {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();

        GameController gameController = new GameController(inputView, outputView);
        gameController.play();
    }
}
```

#### GameController

- 게임에 필요한 다른 변수들이 많으면 `GameVariable` 클래스 생성을 고려한다.

```
public class GameController {
    private final InputView inputView;
    private final OutputView outputView;

    public GameController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void play() {
        outputView.printGameStart();
    }
}
```

#### OutputView

```
public class OutputView {

    private enum ConsoleMessage {
        OUTPUT_GAME_START("게임을 시작합니다.");

        private final String message;

        ConsoleMessage(String message) {
            this.message = message;
        }
    }

  public void printGameStart() { 
    System.out.println(ConsoleMessage.OUTPUT_GAME_START.message);
  }

}
```

### InputView

```
public class InputView {

    private enum ConsoleMessage {
        INPUT_BUDGET("구입금액을 입력해 주세요.");

        private final String message;

        ConsoleMessage(String message) {
            this.message = message;
        }
    }

    public int readBudget() {
        System.out.println(ConsoleMessage.INPUT_BUDGET.message);
        String input = Util.removeSpace(Console.readLine());
        // validate
        return Integer.parseInt(input);
    }
}
```

### 출력 메세지 처리

#### ExceptionMessage

```
public enum ExceptionMessage {

    INVALID_NOT_NUMERIC("자연수만 입력 가능합니다."),
    INVALID_OUT_OF_INT_RANGE("입력 범위를 초과하였습니다.");

    public static final String BASE_MESSAGE = "[ERROR] %s";
    private final String message;

    ExceptionMessage(String message, Object... replaces) {
        this.message = String.format(BASE_MESSAGE, String.format(message, replaces));
    }

    public String getMessage() {
        return message;
    }
}
```

- 예외를 던지는 곳에서
  `throw new IllegalArgumentException(ExceptionMessage.~~.getMessage());`



## Util

- 필요한 것만 골라다 쓰자!

``` 
public class Util {

    public static String removeSpace(String input) {
        return input.replaceAll(Regex.SPACE.regex, Regex.NO_SPACE.regex);
    }

    public static String removeDelimiters(String input) {
        return input.replace(Regex.SQUARE_BRACKETS_START.regex, Regex.NO_SPACE.regex)
                .replace(Regex.SQUARE_BRACKETS_END.regex, Regex.NO_SPACE.regex);
    }

    public static List<String> splitByComma(String input) {
        return Arrays.asList(Util.removeSpace(input).split(Regex.COMMA.regex));
    }

    public static List<String> formatProductInfo(String input) {
        return Util.splitByComma(Util.removeDelimiters(Util.removeSpace(input)));
    }


    private enum Regex {
        SPACE(" "), NO_SPACE(""),
        SQUARE_BRACKETS_START("["), SQUARE_BRACKETS_END("]"),
        COMMA(",");

        private final String regex;

        Regex(String regex) {
            this.regex = regex;
        }
    }

    private Util() {
    }
}
```

## Validation

#### Validator 추상메서드 생성

```
public abstract class Validator {
    
    private enum Range{
        MIN_RANGE(3), MAX_RANGE(20);

        private final int value;

        Range(int value) {
            this.value = value;
        }
    }
    private static final Pattern NUMBER_REGEX = Pattern.compile("^[0-9]*$");

    abstract void validate(String input) throws IllegalArgumentException;

    static String removeSpace(String input) {
        return input.replaceAll(" ", "");
    }

       void validateNumeric(String input) {
        if (!NUMBER_REGEX.matcher(input).matches()) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_NOT_NUMERIC.getMessage());
        }
    }

    void validateRange(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_OUT_OF_INT_RANGE.getMessage(), exception);
        }
    }

    void validateNumberRange(String input) {
        int number = Integer.parseInt(input);
        if (number < Range.MIN_RANGE.value || number > Range.MIN_RANGE.value) {
            throw new IllegalArgumentException();
        }
    }

}
```

#### 자손 클래스로 구체화

```
public class BridgeSizeValidator extends Validator {
    private enum Range {
        MIN_RANGE(3), MAX_RANGE(20);

        private final int value;

        Range(int value) {
            this.value = value;
        }
    }

    private static final Pattern NUMBER_REGEX = Pattern.compile("^[0-9]*$");

    @Override
    public void validate(String input) throws IllegalArgumentException {
        validateNumeric(input);
        validateRange(input);
        validateNumberRange(input);
    }

```

- 테스트 코드도 동시에 작성
    - `removeSpace`는 `inputView`에서 이미 행하고 나서 들어오는 것이기 때문에 여기서는 공백 제거를 테스트하면 안됨

```
class BudgetValidatorTest {

    private BudgetValidator budgetValidator;

    @BeforeEach
    void setUp() {
        budgetValidator = new BudgetValidator();
    }

    @Nested
    class invalidInputTest {

        @ParameterizedTest
        @ValueSource(strings = {"한글", "moonja", " -1000 ", "-2322190000"})
        @DisplayName("자연수가 아닌 입력의 경우 예외 처리한다.")
        void 자연수가_아닌_입력(String input) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> validator.validate(input))
                    .withMessageStartingWith(ExceptionMessage.INVALID_NOT_NUMERIC.getMessage());
        }

        
        @ParameterizedTest
        @ValueSource(strings = {"2222222222222222222222222222000", "1294013905724312349120948120000"})
        @DisplayName("int 범위를 초과한 입력의 경우 예외 처리한다.")
        void int_범위를_벗어난_입력(String input) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> validator.validate(input))
                    .withMessageStartingWith(ExceptionMessage.INVALID_OUT_OF_INT_RANGE.getMessage());
        }

    }

    @Nested
    class validInputTest {
        @ParameterizedTest
        @ValueSource(strings = {"222000", "22222000", " 1000"})
        void 정상_입력(String input) {
            assertThatCode(() -> budgetValidator.validate(input))
                    .doesNotThrowAnyException();
        }

    }
}
```

## Constants

- 다양한 자료형의 상수가 모여있다면 `Enum`을 활용하기 어려움
- 한 클래스가 아니라 여러 클래스에서 사용되는 상수의 경우 따로 클래스를 만들자!

``` 
public class Constants {

    public static final int NUMBER_COUNT = 6;
    public static final int MIN_RANGE = 1;
    public static final int MAX_RANGE = 45;
    public static final int LOTTO_PRICE = 1000;

    private Constants() {
    }
}

```

### `MapPractice` 을 key, value (키, 값) 순으로 출력하기

``` 
    Map<Integer, Integer> map = new HashMap<>();
    map.put(100, 1);
    map.put(200, 2);
    for (Map.Entry<Integer, Integer> element : map.entrySet()) {
        System.out.println(String.format("%d원 - %d개", element.getKey(), element.getValue()));
    }
```

# Format 형식 맞추기 형식 Formatting

## ResultFormatter

- 결과값을 출력하는 과정이 복잡할 때
- 결과값을 formatting하는 일을 model이 하는 것은 부적절, view에서 하기에도 클 수 있다.

### 큰 숫자 다루기 `BigDecimal`

- 소수점, 반올림을 빡세게 요구할 때
- 너무 복잡한 수나, 아니면 돈을 다루는 경우에 바로 사용하자!
- 퍼센트를 구한 다음에 `1,000.3%` 꼴로 출력하자
- `ArithmeticException`을 조심하자!

- dividend, divisor, quotient
  ![img.png](img.png)

- 소수점 아래 둘째 자리에서 반올림 `1.35 => 1.4`

``` 
    private static BigDecimal getSetScale(BigDecimal rewardRate) {
        return rewardRate.setScale(1, RoundingMode.HALF_EVEN);
    }
```

- 퍼센트 구하기 `totalCashPrize / ticketBudget * 100` + 소수점 아래 둘째 자리에서 반올림

``` 
 private static BigDecimal calculatePercent(BigDecimal totalCashPrize, BigDecimal ticketBudget) {
        if (ticketBudget.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        return totalCashPrize.multiply(new BigDecimal("100")).divide(ticketBudget, 1, RoundingMode.HALF_EVEN);
    }
```

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


### Enum 클래스 관리

#### Command 관리!!! (사용자가 입력한 옵션)

```
public enum MainOption {
    PAIR_MATCHING("1"),
    PAIR_SEARCHING("2"),
    PAIR_INITIALIZING("3"),
    QUIT("Q");

    private final String command;

    MainOption(String command) {
        this.command = command;
    }

    public static MainOption from(String command) {
        return Arrays.stream(MainOption.values())
                .filter(option -> option.command.equals(command))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NO_MAIN_OPTION.getMessage()));
    }
    
    // NO_MAIN_OPTION => "해당하는 메인 옵션이 존재하지 않습니다."

    public boolean continueMain() {
        return this != QUIT;
    }

}
```
