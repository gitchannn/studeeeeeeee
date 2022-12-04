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

    NOT_NUMERIC("입력 범위를 초과했습니다."),
    NOT_IN_RANGE("1부터 45까지의 숫자만 입력 가능합니다.");
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

    void validateNumber(String input) {
        if (!NUMBER_REGEX.matcher(input).matches()) {
            throw new IllegalArgumentException();
        }
    }

    void validateInputRange(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException();
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
        validateNumber(bridgeSize);
        validateInputRange(bridgeSize);
        validateNumberRange(bridgeSize);
    }

    private void validateNumber(String input) {
        if (!NUMBER_REGEX.matcher(input).matches()) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_NOT_NUMERIC.getMessage());
        }
    }

    private void validateInputRange(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(ExceptionMessage.OUT_OF_INT_RANGE.getMessage());
        }
    }

    private void validateNumberRange(String input) {
        int number = Integer.parseInt(input);
        if (number < Range.MIN_RANGE.value || number > Range.MAX_RANGE.value) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_NOT_IN_RANGE.getMessage());
        }
    }
}

```

- 테스트 코드도 동시에 작성
```
class BridgeSizeValidatorTest {

    private BridgeSizeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BridgeSizeValidator();
    }

    @Nested
    class invalidInput {

        @DisplayName("자연수가 아닌 입력")
        @ParameterizedTest
        @ValueSource(strings = {"aaa", "문자", "아아아아", "아 아 아 ㅇ ㅏ", "-1", "-299"})
        void 자연수가_아닌_입력(String input) {
            assertThatThrownBy(() -> validator.validate(input))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ExceptionMessage.INVALID_NOT_NUMERIC.getMessage());
        }

        @DisplayName("int 범위를 초과한 입력")
        @ParameterizedTest
        @ValueSource(strings = {"1111111111111111111111111", "2183128312887721847281389"})
        void int_입력_범위_초과(String input) {
            assertThatThrownBy(() -> validator.validate(input))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ExceptionMessage.OUT_OF_INT_RANGE.getMessage());
        }

        @DisplayName("3 이상 20 이하의 값이 아니면 예외 처리한다.")
        @ParameterizedTest
        @ValueSource(strings = {"0", "1", "2", "21"})
        void 다리_길이_범위를_초과(String input) {
            assertThatThrownBy(() -> validator.validate(input))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ExceptionMessage.INVALID_NOT_IN_RANGE.getMessage());
        }
    }

    @Nested
    class validInputTest {
        @ParameterizedTest
        @ValueSource(strings = {"3", "4", "19", "20"})
        void 정상_입력(String input) {
            assertThatCode(() -> validator.validate(input))
                    .doesNotThrowAnyException();
        }
    }

}
```
