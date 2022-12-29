#### 주의사항
1. try, catch로 꼭 IOException 잡아줘야함
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
