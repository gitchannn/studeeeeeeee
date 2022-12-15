### START

### 기능 목록을 작성할 때

- 복잡한 문제라면 **플로우차트**를 그린다
- 어떤 클래스가 어떤 데이터를 담아야 하는지 **코드 짜기 _전에_ 생각한다!!!**
- `Status` 만들 때는 최대한 뭉뚱그려서 **크게 크게 분리**한다!!!
- 가능하면 출력해야 하는 내용을 **`view`에 복붙**하면서 작성하자!!! => 집착은 X

- 문제의 분위기를 보고, Controller 형태를 결정한다 => `Controller로직`에서 설정!!!
- Controller 에러를 없애려면 `view`를 만들어야 함

#### 패키지 나누기

- `controller` `model` `util` `view` 패키지 생성
- `util` 패키지 안에 `Util` 클래스 생성 (여러번 사용되는 것들)
- `util` 패키지 안에 `validator` 패키지 생성

---

###### 그 다음, `view`부터 만들자!!!

#### OutputView

```
public class OutputView {

  private static final OutputView instance = new OutputView();

  public static OutputView getInstance(){
      return instance;
  }
  private OutputView() {
  }
  
    // static이면 이 위에 지우고 아래를 static으로 만들면됨
    
  public void printGameStart() {
    System.out.println(Message.OUTPUT_GAME_START.message);
  }

    public void printExceptionMessage(Exception exception) {
        System.out.println(exception.getMessage());
    }

  private enum Message {
        OUTPUT_GAME_START("게임을 시작합니다.");

        private final String message;

        Message(String message) {
            this.message = message;
        }
    }


}
```

### InputView

```
public class InputView {

     private static final InputView instance = new InputView();

    public static InputView getInstance(){
        return instance;
    }
    private InputView() {
    }
    
    // static이면 이 위에 지우고 아래를 static으로 만들면됨

    public int readBudget() {
        System.out.println(Message.INPUT_BUDGET.message);
        String input = Console.readLine();
       // String input = Util.removeSpace(Console.readLine());
        // validate
        return Integer.parseInt(input);
    }


     private enum Message {
        INPUT_BUDGET("구입금액을 입력해 주세요.");

        private final String message;

        Message(String message) {
            this.message = message;
        }
    }
}
```

---

# Controller & Application

!!! 먼저 `안관리 ver` 쓰고 아래에 `생명주기 관리 ver` 첨부하겠음

##### 생명주기 '안관리 ver'

#### Application

```
public class Application {
    public static void main(String[] args) {
        MainController mainController = new MainController(InputView.getInstance(), OutputView.getInstance());
        mainController.service();
    }
}
```

- 만약 View의 메서드가 `static`이라면??

``` 
public class Application {
    public static void main(String[] args) {
        MainController mainController = new MainController();
        mainController.service();
    }
}
```

#### MainController

- 일단은 전체 `MainController`에 만들고 나중에 필요하면 다른 Controller를 만들어서 분리하자
- 게임에 필요한 다른 변수들이 많으면 `MainVariable` 클래스 생성을 고려한다.

- `Controller로직.md`를 참고하자

##### 아무런 리팩터링도 고려하지 않은 간단 ver.

```
public class MainController {
    private final InputView inputView;
    private final OutputView outputView;

    public MainController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void service() {
        outputView.printGameStart();
    }
}
```

---

### 출력 메세지 처리

#### ExceptionMessage

- 고냥 모든 Message에 사용 가능
- 클래스 분리하지 않고 해당 클래스 안에서 `private`

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
- 같은 클래스 내면
  `throw new IllegalArgumentException(ExceptionMessage.~~.message);`

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

    private static final Pattern NUMBER_REGEX = Pattern.compile("^[0-9]*$");

    abstract void validate(String input) throws IllegalArgumentException;

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
        if (number < Range.MIN_RANGE.value || number > Range.MAX_RANGE.value) {
            throw new IllegalArgumentException();
        }
    }
    private enum Range{
        MIN_RANGE(3), MAX_RANGE(20);

        private final int value;

        Range(int value) {
            this.value = value;
        }
    }
}
```

#### 자손 클래스로 구체화

```
public class BridgeSizeValidator extends Validator {

    @Override
    public void validate(String input) throws IllegalArgumentException {
       // 검증 로직 작성
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
        @ValueSource(strings = {"한글", "moonja", "-1000", "-2322190000"})
        @DisplayName("자연수가 아닌 입력의 경우 예외 처리한다.")
        void 자연수가_아닌_입력(String input) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> validator.validate(input))
                    .withMessageStartingWith(ExceptionMessage.INVALID_NOT_NUMERIC.getMessage());
        }

        //   assertThatThrownBy(() -> budgetValidator.validate(input))
        //            .isInstanceOf(IllegalArgumentException.class)
        //            .hasMessage(ExceptionMessage.OUT_OF_RANGE.getMessage());


        @ParameterizedTest
        @ValueSource(strings = {"2222222222222222222222222222000", "1294013905724312349120948120000"})
        @DisplayName("int 범위를 초과한 입력의 경우 예외 처리한다.")
        void int_범위를_벗어난_입력(String input) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> budgetValidator.validate(input))
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
