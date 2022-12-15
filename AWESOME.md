## 우테코 최종 코딩테스트 가이드라인

### 목차

## 0. 요구사항 읽고 기능 목록 작성 (중요한건 **bold**~)

    - 게임형: `ApplicationStatus` (크게크게) VS 옵션 선택형: `MainOption` (명시적으로)
    - 각각 `enum` 이름 생각해놓기
    - 아래에 따라 `View` 작성하면서 기능 목록 써도 좋음

- [1. 초기 설정](#1.-초기-설정)

## 1. `Controller` 로직 결정

- [`BridgeGame`] 다양하게 상황에 따라 게임이 얽히고 설켜 복잡한 플로우차트 => [`Supplier` 로직](#로직-supplier)으로!
    - 플로우차트를 먼저 구상한 뒤에 그것과 똑같이 만들면 안정적임!!!
    - `GameVariableRepository` 같은 클래스에 `시도 회수, 성공 여부` 등을 함께 관리하면 더 효율적!
- [`PairMatching`] 시작은 간단하나, 각각 무거운 로직 + [`Repository`](#repository)랑 같이 사용하면 효과적 => [`Controller` 로직](#로직-controller)
  으로!
    - 각각의 `Controller`에서도 각각 성질에 따라서 눈치껏 다른 로직 진행
- [`Runnable` 로직](#로직-runnable)
    - controller를 나누는 것과 비슷하나 한 클래스에 넣을 수 있음 (짧은 내용)

## 로직 supplier

- 복잡한 플로우차트 적합

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
        gameGuide.put(ApplicationStatus.CREATE_BRIDGE, this::createBridge);
    }

    private ApplicationStatus createBridge() {
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

## 로직 controller

- 시작은 간단하나, 각각이 무거운 `controller`일 경우 분리
- 미리 분리하는게 복잡도를 줄여서 편안함
- 이 경우, 검증 로직이 포함되므로 `MainOption`은 분리해서 `public enum`으로 다루자!

- `Controllable` 인터페이스 생성

``` 
@FunctionalInterface
public interface Controllable {
    void process();
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
            progress(mainOption);
        } while (mainOption.isPlayable());
    }

    public void progress(MainOption mainOption) {
        try {
            controllers.get(mainOption).process();
        } catch (IllegalArgumentException exception) {
            outputView.printExceptionMessage(exception);
        }
    }

}
```

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

## 로직 runnable

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
        progress(ApplicationStatus.CREW_LOADING);
    }

    public void progress(ApplicationStatus applicationStatus) {
        try {
            controllers.get(applicationStatus).run();
        } catch (IllegalArgumentException exception) {
            outputView.printExceptionMessage(exception);
        }
    }

    private void crewLoading() {
    }
}
```