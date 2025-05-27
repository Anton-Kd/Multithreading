import java.util.*;
import java.util.concurrent.*;

public class Main {
    static final ExecutorService threadPool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }
        List<Future<String>> futures = new ArrayList<>();
        Callable<String> myCallable = null;
        Future<String> task = null;
        long startTs = System.currentTimeMillis(); // start time

        for (String text : texts) {

            myCallable = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                return (text.substring(0, 100) + " -> " + maxSize);

            };
//        В цикле отправьте в пул потоков задачи на исполнение,
//        получив в ответ на каждую отправку Future, которые войдут в список.
            task = threadPool.submit(myCallable);
            futures.add(task);
        }
        threadPool.shutdown();

        int number = 0;
        int currentNumber;
//        После цикла с отправкой задач на исполнение пройдитесь циклом по Future и у каждого вызовите get для
//        ожидания и получения результата, который вы обработаете для получения ответа на задачу.
        for (Future<String> future : futures) {
            String result = future.get();
            System.out.println(result);
            currentNumber = Integer.parseInt(result.substring(result.length() - 2));
            if (currentNumber >= number) {
                number = currentNumber;
            }
        }
        System.out.printf("Максимальноу число повтрений <а> %d раз\n", number);

        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}