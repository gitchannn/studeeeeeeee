import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DuplicateTest {
    public static void main(String[] args) {
        List<String> list1 = new ArrayList<>(List.of("은기", "지훈"));
        List<String> list2 = new ArrayList<>(List.of("지훈", "은기"));

        System.out.println(list1.equals(list2));

        Set<String> set1 = new HashSet<>(List.of("은기", "지훈"));
        Set<String> set2 = new HashSet<>(List.of("지훈", "은기"));

        System.out.println(set1.equals(set2));
    }
}
