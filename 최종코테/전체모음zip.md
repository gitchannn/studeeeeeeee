## 목차

- [SPEEDY](#speedy)
- [Controller 로직](#로직-controller)
- [Enum 관리](#enum)
- [Repository 사용법](#repository)
- [ResultFormatter](#result-formatter)
- [Big Decimal 사용법](#big-decimal)
- [FileReader 사용법](#file-reader)
- [검증로직들](#검증로직들)
- [깃허브](#깃허브)

## speedy

### START

### 기능 목록을 작성할 때

- 복잡한 문제라면 **플로우차트**를 그린다
- 어떤 클래스가 어떤 데이터를 담아야 하는지 **코드 짜기 _전에_ 생각한다!!!**
- 가능하면 출력해야 하는 내용을 **`view`에 복붙**하면서 작성하자!!! => 집착은 X

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

#### Application

```
public class Application {
    public static void main(String[] args) {
        MainController mainController = new MainController(InputView.getInstance(), OutputView.getInstance());
        mainController.play();
    }
}
```

#### MainController

- 일단은 전체 `MainController`에 만들고 나중에 필요하면 다른 Controller를 만들어서 분리하자
- 게임에 필요한 다른 변수들이 많으면 `MainVariable` 클래스 생성을 고려한다.

- [Controller 로직](#로직-controller)를 참고하자

##### 아무런 리팩터링도 고려하지 않은 간단 ver.

```
public class MainController {
    private final InputView inputView;
    private final OutputView outputView;

    public MainController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void process() {
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

## 로직 controller

## 우테코 최종 코딩테스트 가이드라인 for Controller 로직

### 목차

## 요구사항 읽고 기능 목록 작성 (중요한건 **bold**~)

    - 게임형: `ApplicationStatus` (크게크게) VS 옵션 선택형: `MainOption` (명시적으로)
    - 각각 `enum` 이름 생각해놓기
    - 아래에 따라 `View` 작성하면서 기능 목록 써도 좋음

### Application 만들어놓기

``` 
public class Application {
    public static void main(String[] args) {
            MainController mainController = new MainController(InputView.getInstance(), OutputView.getInstance());
            mainController.play();
    }
}
```

### `Controller` 로직 결정

- [`Supplier` 로직](#1.-로직-supplier) => [`BridgeGame`]처럼 다양하게 상황에 따라 게임이 얽히고 설켜 **복잡한 플로우차트**
    - 플로우차트를 먼저 구상한 뒤에 그것과 똑같이 만들면 안정적임!!!
    - `GameVariableRepository` 같은 클래스에 `시도 회수, 성공 여부` 등을 함께 관리하면 더 효율적!
- [`Controller` 로직](#2.-로직-controller) => [`PairMatching`]처럼 시작은 간단하나, 각각 무거운 로직 +
    - [`Repository`](#repository)랑 같이 사용하면 효과적
      으로!
    - 각각의 `Controller`에서도 각각 성질에 따라서 눈치껏 다른 로직 진행
- [`Runnable` 로직](#3.-로직-runnable)
    - controller를 나누는 것과 비슷하나 한 클래스에 넣을 수 있음 (짧은 내용)

---

## 1. 로직 supplier

- **복잡한 플로우차트** 적합 (예. BridgeGame)

``` 
public class MainController {
    private final InputView inputView;
    private final OutputView outputView;
    private final Map<ApplicationStatus, Supplier<ApplicationStatus>> gameGuide;

    public MainController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.gameGuide = new EnumMap<>(ApplicationStatus.class); // 밑에 status 있음
        initializeGameGuide();
    }
    
    private void initializeGameGuide() {
        gameGuide.put(ApplicationStatus.CREATE_BRIDGE, this::createBridge);
    }
    
    public void play() {
        ApplicationStatus applicationStatus = progress(ApplicationStatus.CREATE_BRIDGE); // 초기 설정
        while (applicationStatus.playable()) {
            applicationStatus = process(applicationStatus);
        }
    }

    public ApplicationStatus process(ApplicationStatus applicationStatus) {
        try {
            return gameGuide.get(applicationStatus).get();
        } catch (NullPointerException exception) { 
            return ApplicationStatus.APPLICATION_EXIT;
        }
    }

    private ApplicationStatus createBridge() {
    // 입력 하나 틀리면 다시 입력 => inputView에서 해결
    // 입력 검증하다가 틀리면 다시 입력 => Supplier를 리턴하는 방식
    }


    private enum ApplicationStatus {
        CREATE_BRIDGE,
        APPLICATION_EXIT;

        public boolean playable() {
            return this != APPLICATION_EXIT;
        }
    }

}
```

---

## 2. 로직 controller

- 시작에서 **큰 옵션**을 받아 간단하나, 각각이 무거운 `controller`일 경우 분리
- *미리 분리*하는게 복잡도를 줄여서 편안함
- 이 경우, 검증 로직이 포함되므로 `MainOption`은 분리해서 `public enum`으로 다루자!

- Controllable 인터페이스 생성

``` 
@FunctionalInterface
public interface Controllable {
    void process();
}
```

- 하위 Controller 만들기 (예시) **implements**

``` 
public class OrderRegistrationController implements Controllable {

    private final InputView inputView;
    private final OutputView outputView;

    public OrderRegistrationController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    @Override
    public void process() {
     // 작성 => 이 controller도 눈치껏 controller 로직을 적용해도 좋음! 
    }

}
```

- MainOption 생성

```
public enum MainOption {

    ORDER_REGISTRATION("1"),
    PAYMENT("2"),
    APPLICATION_EXIT("3");

    private final String command;

    MainOption(String command) {
        this.command = command;
    }

    public boolean isPlayable() {
        return this != APPLICATION_EXIT;
    }

    public static MainOption from(String command) {
        return Arrays.stream(MainOption.values())
                .filter(option -> option.command.equals(command))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NO_MAIN_OPTION.getMessage()));
    }
    
    //  "해당 메인 옵션이 존재하지 않습니다."

}
```

- MainController에서 Controller switch 조절

``` 

public class MainController {
private final Map<MainOption, Controllable> controllers;
private final InputView inputView;
private final OutputView outputView;

    public MainController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.controllers = new EnumMap<>(MainOption.class);
        initializeControllers();
    }

    private void initializeControllers() {
        controllers.put(MainOption.STATION_MANAGEMENT, new StationManagementController(inputView, outputView));
    }

    public void service() {
        new InitializingController().process();
        MainOption mainOption;
        do {
            outputView.printMainScreen();
            mainOption = inputView.readMainOption();
            process(mainOption);
        } while (mainOption.isPlayable());
    }

    public void process(MainOption mainOption) {
        try {
            controllers.get(mainOption).process();
        } catch (IllegalArgumentException exception) {
            outputView.printExceptionMessage(exception);
        }
    }
}
```

## 3. 로직 runnable

``` 

public class MainController {
private final InputView inputView;
private final OutputView outputView;
private final Map<ApplicationStatus, Runnable> gameGuide;

    public MainController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.gameGuide = new EnumMap<>(ApplicationStatus.class);
        initializeControllers();
    }

    private void initializeControllers() {
        gameGuide.put(ApplicationStatus.CREW_LOADING, this::crewLoading);
    }

    public void service() {
        process(ApplicationStatus.CREW_LOADING);
    }

    public void process(ApplicationStatus applicationStatus) {
        try {
            controllers.get(applicationStatus).run();
        } catch (IllegalArgumentException exception) {
            outputView.printExceptionMessage(exception);
        }
    }

    private void crewLoading() {
    // 코드 작성
    }
}
```

---

## repository

``` 

public class Crews {

    private Crews() {
    } // 선택

    private static final List<Crew> crews = new ArrayList<>(); // 잦은 삭제면 LinkedList 고려

    public static List<Crew> crews() {
        return Collections.unmodifiableList(crews);
    }

    public static void addCrew(Crew crew) {
        crews.add(crew);
    }

    public static boolean deleteCrewByName(String name) {
        return crews.removeIf(line -> Objects.equals(line.getName(), name));
    }

    public static void deleteAll() {
        crews.clear();
    }

    public static Crew findCrewByName(String name) {
        return crews.stream()
                .filter(crew -> crew.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException());
    }

}

```

## enum

## Enum 관리

``` 
public enum BridgeSign {
    UP("U", 1), DOWN("D", 0);

    private final String sign;
    private final int number;

    BridgeSign(String sign, int number) {
        this.sign = sign;
        this.number = number;
    }
    public static BridgeSign from(int number) {
        return Arrays.stream(BridgeSign.values())
                .filter(element -> element.number == number)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.~~.getMessage()));
    }

    public static String numberToSign(int number) {
        return from(number).sign;
    }

}
```

- 변환하는 곳

``` 
    public List<String> makeBridge(int size) {
        return IntStream
                .generate(bridgeNumberGenerator::generate)
                .limit(size)
                .mapToObj(BridgeSign::numberToSign)
                .collect(Collectors.toList());
    }
```

## result formatter

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

## big decimal

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
        // 퍼센트는 미리 곱하기
        // 몇째 자리까지 왔으면 좋겠다는걸 두번째 숫자에!
    }
```

## repository 저장소 개념 static 레포지토리

``` 
public class Crews {
    
    private Crews() {
    } // 선택

    private static final List<Crew> crews = new ArrayList<>();

    public static List<Crew> crews() {
        return Collections.unmodifiableList(crews);
    }

    public static void addCrew(Crew crew) {
        crews.add(crew);
    }

    public static boolean deleteCrewByName(String name) {
        return crews.removeIf(line -> Objects.equals(line.getName(), name));
    }

    public static void deleteAll() {
        crews.clear();
    }

    public static Crew findCrewByName(String name) {
        return crews.stream()
                .filter(crew -> crew.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException());
    }

}
```

## file reader

#### 주의사항

1. try, catch로 꼭 `IOException` 잡아줘야함 => 안잡으면 에러
2. 파일 경로 조심 + 오타 조심
3. 한줄씩 읽을거면 `BufferedReader` 아니면 다른거 (구글링)

``` 
try {
            File backendCrews = new File("src/main/resources/backend-crew.md");
            File frontendCrews = new File("src/main/resources/frontend-crew.md");

            BufferedReader backendCrewsReader = new BufferedReader(new FileReader(backendCrews));
            BufferedReader frontendCrewsReader = new BufferedReader(new FileReader(frontendCrews));

            String backendCrew;
            while ((backendCrew = backendCrewsReader.readLine()) != null) {
                System.out.println(backendCrew);
            }
            System.out.println();

            String frontendCrew;
            while ((frontendCrew = frontendCrewsReader.readLine()) != null) {
                System.out.println(frontendCrew);
            }
        } catch (IOException exception) {
            outputView.printExceptionMessage(exception);
            throw new RuntimeException(exception);
        }
  ```

## 검증로직들

- 정규식으로 숫자만 받기

```
private static final Pattern MONEY_REGEX = Pattern.compile("^[0-9]*$");
if (!MONEY_REGEX.matcher(input).matches()) {
            throw new IllegalArgumentException(ERROR_PURCHASE_TYPE);
        }
```

- 1부터 9까지의 자연수

```
"^[1-9]+$"
```

- R, Q 외의 문자는 안됨

```
String moveCommandRegex = "^([RQ])$";

      if (!Pattern.matches(moveCommandRegex, restartCommand)) {
          throw new IllegalArgumentException(InputErrorText.ERROR_RESTART_COMMAND.errorText());
      }
```

- 369 게임에서 3, 6, 9가 들어간 글자 골라내기

```
private static int calculateCurrentNumberClaps(int currentNumber) {
      int originalLength = String.valueOf(currentNumber).length();
      return originalLength - String.valueOf(currentNumber).replaceAll("[369]", "").length();
  }
```

- 중복 검사

```
public static void isDistinct(String input) {
  long count = input.chars()
      .distinct()
      .count();
  if (count != ConstVariable.SIZE.getValue()) {
      throw new IllegalArgumentException();
  }
}
 ```

## 깃허브

1. Fork 후에 `eunkeeee/프로젝트이름`을 local로 `clone`하기

```
cd {저장할 경로 입력}
git clone https://github.com/eunkeeee/java-baseball.git
```

2. 브랜치 생성 및 전환

```
git branch {생성할 브랜치 이름}
git checkout {전환할 브랜치 이름}
git checkout -b {생성 후 전환할 브랜치 이름}
```

3. 정상적 commit & push

```
// 모든 파일 스테이징
git add . 

// 스테이징된 파일 전부 커밋하기
git commit -m "feat(GameController): 기능 잘 구현

git push origin eunkeeee
```

4. add 취소

```
git reset HEAD README.md  // README 파일을 Unstaged 상태로 변경
git reset // 모든 파일을 스테이징 취소
git checkout . // 커밋되지 않은 모든 로컬 변경 사항을 되돌림
git checkout [some_dir|file.txt] // 커밋되지 않은 변경 사항을 특정 파일이나 디렉터리로만 되돌림
```

5. commit 취소

```
// [방법 1] commit을 취소하고 해당 파일들은 staged 상태로 워킹 디렉터리에 보존
$ git reset --soft HEAD^

// [방법 2] commit을 취소하고 해당 파일들은 unstaged 상태로 워킹 디렉터리에 보존
$ git reset --mixed HEAD^ // 기본 옵션
$ git reset HEAD^ // 위와 동일
$ git reset HEAD~2 // 마지막 2개의 commit을 취소

// [방법 3] commit을 취소하고 해당 파일들은 unstaged 상태로 워킹 디렉터리에서 삭제
$ git reset --hard HEAD^
```

- reset 옵션
  – soft : index 보존(add한 상태, staged 상태), 워킹 디렉터리의 파일 보존. 즉 모두 보존.
  – mixed : index 취소(add하기 전 상태, unstaged 상태), 워킹 디렉터리의 파일 보존 (기본 옵션)
  – hard : index 취소(add하기 전 상태, unstaged 상태), 워킹 디렉터리의 파일 삭제. 즉 모두 취소.

6. commit 메세지 변경

```
git commit --amend
```

7. commit 로그와 수정된 파일 함께 보기

```
git log 
// q를 눌러 나가기
```

8. 특정 파일만 commit & push

```
// 작업한 파일 목록 확인하기
git status

// 기존파일의 변경내역 확인하기
git diff

// 특정 파일 Staging하기
git add <경로 및 file명> <경로 및 file명> <경로 및 file명> ...

// 특정 파일 Commit하기
git commit -m "커밋 메세지" {파일이름}

// 위의 두 개 한꺼번에 하기
git commit -am "커밋 메세지" {파일이름}

// 특정 파일 unstaged 상태 만들기
git restore --staged {파일이름}
```
