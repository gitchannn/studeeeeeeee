### START

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
#### Controller
```
public class GameController {
    private final InputView inputView;
    private final OutputView outputView;

    public GameController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void play() {
        try {
            // 여기에 작성
        } catch (IllegalArgumentException exception) {
            outputView.printExceptionMessage(exception);
        }
    }
}
```
#### OuputView
```
public class OutputView {

    public void printExceptionMessage(Exception exception) {
        System.out.println(exception.getMessage());
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

    ExceptionMessage(String message) {
        this.message = String.format(BASE_MESSAGE, message);
    }

    public String getMessage() {
        return message;
    }
}
```
- 예외를 던지는 곳에서
`throw new IllegalArgumentException(ExceptionMessage.~~.getMessage());`


### Console Message at INPUTVIEW
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
        String input = Console.readLine();
        return Integer.parseInt(input);
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
        String bridgeSize = removeSpace(input);
        validateNumeric(bridgeSize);
        validateRange(bridgeSize);
        validateNumberRange(bridgeSize);
    }

```

- 테스트 코드도 동시에 작성
```
class BudgetValidatorTest {

    private BudgetValidator budgetValidator;

    @BeforeEach
    void setUp() {
        budgetValidator = new BudgetValidator();
    }

    @Nested
    class invalidInputTest {

        @DisplayName("자연수가 아닌 입력의 경우 예외 처리한다.")
        @ParameterizedTest
        @ValueSource(strings = {"한글", "moonja", "   문자   wi t h 공    백   ", " -1000 ", "- 2 32 2190000"})
        void 자연수가_아닌_입력(String input) {
            assertThatThrownBy(() -> budgetValidator.validate(input))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ExceptionMessage.INVALID_NOT_NUMERIC.getMessage());
        }


        @DisplayName("int 범위를 초과한 입력의 경우 예외 처리한다.")
        @ParameterizedTest
        @ValueSource(strings = {"2222222222222222222222222222000", "1294013905724312349120948120000"})
        void int_범위를_벗어난_입력(String input) {
            assertThatThrownBy(() -> budgetValidator.validate(input))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ExceptionMessage.INVALID_OUT_OF_INT_RANGE.getMessage());
        }

    }

    @Nested
    class validInputTest {
        @ParameterizedTest
        @ValueSource(strings = {"222000", "22222000", " 1   0    0  0   "})
        void 정상_입력(String input) {
            assertThatCode(() -> budgetValidator.validate(input))
                    .doesNotThrowAnyException();
        }

    }
}
```
