package 우테코_프리코스;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Lambda {

    public static void main(String[] args) {
        Supplier<Integer> supplier = () -> (int) (Math.random() * 100) + 1;
        Consumer<Integer> consumer = i -> System.out.print(i + ", ");
        Predicate<Integer> predicate = i -> i % 2 == 0;
        Function<Integer, Integer> function = i -> i / 10 * 10;

        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add(supplier.get());
        }
        System.out.println(list);

        for (int i : list) {
            if (predicate.test(i)) {
                consumer.accept(i);
            }
        }
        System.out.println();

        List<Integer> newList = new ArrayList<>();
        for (int i : list) {
            newList.add(function.apply(i));
        }
        System.out.println(newList);

    }

}
