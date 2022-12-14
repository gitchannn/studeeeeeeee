#### 1. Runnable로 나누기

``` 
public class MainController {
    private final InputView inputView;
    private final OutputView outputView;
    private final Map<ApplicationStatus, Runnable> controllers;

    public MainController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.controllers = new EnumMap<>(ApplicationStatus.class);
        initializeControllers();
    }

    private void initializeControllers() {
        controllers.put(ApplicationStatus.CREW_LOADING, this::crewLoading);
        controllers.put(ApplicationStatus.PAIR_MATCHING, this::pairMatching);
        controllers.put(ApplicationStatus.PAIR_SEARCHING, this::pairSearching);
        controllers.put((ApplicationStatus.PAIR_INITIALIZING, this::pairInitializing);
        controllers.put(ApplicationStatus.APPLICATION_EXIT, this::exitApplication);
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

    private void pairMatching() {
    }

    private void pairSearching() {
    }

    private void pairInitializing() {

    }

    private void exitApplication() {

    }

}
```