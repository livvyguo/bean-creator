import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import com.google.common.base.Enums;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.StandardSystemProperty;
import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.common.io.BaseEncoding;
import org.junit.Test;

/**
 * @author guozheng
 * @date 2017/06/21
 */
public class TestMain {

    @Test
    public void test() {
        StandardSystemProperty javaVersion = StandardSystemProperty.JAVA_VERSION;

        System.out.println(javaVersion);

        String encode = BaseEncoding.base64().encode("women".getBytes());

        System.out.println(encode);

        byte[] decode = BaseEncoding.base64().decode("d29tZW4=");

        System.out.println(new String(decode));
    }


    @Test
    public void poet() throws Exception {
        Optional<Gender> man = Enums.getIfPresent(Gender.class, "man");

        System.out.println(man);
        ConcurrentMap<String, Object> concurrentMap =
            new MapMaker()
                .concurrencyLevel(2)
                .initialCapacity(32)
                .weakValues()
                .makeMap();
        CacheBuilderSpec.parse("");

        LoadingCache<String, String> build = CacheBuilder.newBuilder()
            .concurrencyLevel(10)
            .expireAfterWrite(1, TimeUnit.MILLISECONDS)
            .refreshAfterWrite(3, TimeUnit.MILLISECONDS)
            .removalListener(new RemovalListener<String, String>() {
                @Override
                public void onRemoval(RemovalNotification<String, String> notification) {
                    System.out.println("remove : " + notification);


                }
            })
            .ticker(Ticker.systemTicker())
            .initialCapacity(100)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    System.out.println("get key" + key);

                    return ImmutableMap.of("1", "1", "2", "2").getOrDefault(key,
                        "null");
                }
            });

        String s = build.get("1");
        System.out.println(s);
        TimeUnit.MICROSECONDS.sleep(1500);
        String s1 = build.get("2");
        System.out.println(s1);
        TimeUnit.MICROSECONDS.sleep(1500);
        String s3 = build.get("3");
        System.out.println(s3);
        TimeUnit.MICROSECONDS.sleep(1500);
        String s4 = build.get("3");
        build.get("1");
        build.get("2");
        System.out.println(s4);

        CacheLoader<Object, String> from = CacheLoader.from(() -> "livvy");

        String livvy = from.load("livvy");

        System.out.println(livvy);

        CacheLoader<Integer, String> nnnn = CacheLoader.from(
            (Function<Integer, String>)input -> IntStream.range(0, 100).boxed().collect(
                Collectors.toMap(v -> v, v -> "val-" + v)).getOrDefault(input, "NNNN"));

        CacheBuilder.newBuilder()
            .concurrencyLevel(10)
            .expireAfterWrite(1, TimeUnit.MILLISECONDS)
            .refreshAfterWrite(3, TimeUnit.MILLISECONDS)
            .removalListener(new RemovalListener<String, String>() {
                @Override
                public void onRemoval(RemovalNotification<String, String> notification) {
                    System.out.println("remove : " + notification);


                }
            })
            .ticker(Ticker.systemTicker())
            .initialCapacity(100)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    System.out.println("get key" + key);

                    return ImmutableMap.of("1", "1", "2", "2").getOrDefault(key,
                        "null");
                }
            });

    }

    @Test
    public void eventBus() {
        //EventBus eventBus = new EventBus();

        //ComparisonChain.start().compare(BigDecimal.ONE, BigDecimal.ONE).result();

        List<Double> collect = DoubleStream.iterate(0D, v -> v + 0.5D).limit(21).boxed().collect(
            Collectors.toList());

        System.out.println(collect);
    }

    public enum Gender{

        MAN(1,"man"),
        WOMEN(2,"women"),
        ;

        private final Integer value;
        private final String name;

        Gender(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
}
