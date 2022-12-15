#### 1. Stream을 통해서 변환 (1 => U, 0 => D)

- Enum 설정

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
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NO_SUCH_BRIDGE_SIGN.getMessage()));
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

