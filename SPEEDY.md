### START

#### Application
```
public class Application {
    public static void main(String[] args) {
        // TODO: 프로그램 구현
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();

        LottoController lottoController = new LottoController(inputView, outputView);
        lottoController.play();
    }
}
```
#### Controller
```
public class LottoController {
    private final InputView inputView;
    private final OutputView outputView;

    public LottoController(InputView inputView, OutputView outputView) {
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
`throw new Illegal


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

#### Validator 인터페이스 생성

```
package lotto.util;

public interface Validator {
    void validate(String userInput) throws IllegalArgumentException;
}
```

#### 맞게 구현
```
public class BonusNumberValidator implements Validator {
    @Override
    public void validate(String input) throws IllegalArgumentException {
        validateNotNull(input);
        validateNumber(input);
        validateRange(input);
    }
}
```
