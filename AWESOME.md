## 우테코 최종 코딩테스트 가이드라인

### 목차

-
    0. 요구사항 읽고 기능 목록 작성 (중요한건 **bold**~)

    - 게임형: `ApplicationStatus` (크게크게) VS 옵션 선택형: `MainOption` (명시적으로)
    - 각각 `enum` 이름 생각해놓기
    - 아래에 따라 `View` 작성하면서 기능 목록 써도 좋음
- [1. 초기 설정](#1.-초기-설정)

## 1. 초기 설정

- 다양하게 상황에 따라 게임이 얽히고 설켜 진행됨 => [`Supplier` 로직](#로직-supplier)으로!

## 로직 supplier

``` 
public class MainController {
    private final InputView inputView;
    private final OutputView outputView;
    private final Map<ApplicationStatus, Supplier<ApplicationStatus>> gameGuide;

    public MainController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.gameGuide = new EnumMap<>(ApplicationStatus.class);
        initializeGameGuide();
    }

    public void service() {
        ApplicationStatus applicationStatus = progress(ApplicationStatus.CREATE_BRIDGE); // 초기 설정
        while (applicationStatus.playable()) {
            applicationStatus = progress(applicationStatus);
        }
    }

    public ApplicationStatus progress(ApplicationStatus applicationStatus) {
        try {
            return gameGuide.get(applicationStatus).get();
        } catch (NullPointerException exception) { 
        // 전부 다 해당해놓기 전에는 발생하므로 냅두자
        // 여기서 IllegalArgumentException도 처리해도 되지만, 한 Stauts에 여러 input이 있으면 꼬일 수 있음 (InputView에서 최대한 처리)
            return ApplicationStatus.APPLICATION_EXIT;
        }
    }

    private void initializeGameGuide() {
        gameGuide.put(ApplicationStatus.GAME_START, this::startGame);
    }

    private ApplicationStatus startGame() {
    }


    private enum ApplicationStatus {
        GAME_START,
        APPLICATION_EXIT;

        public boolean playable() {
            return this != APPLICATION_EXIT;
        }
    }

}
```