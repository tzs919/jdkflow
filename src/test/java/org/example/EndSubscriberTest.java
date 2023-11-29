package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EndSubscriberTest {
    @Test
    public void whenSubscribeToIt_thenShouldConsumeAll()
            throws InterruptedException {

        // given
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
        EndSubscriber<String> subscriber = new EndSubscriber<>(5);
        publisher.subscribe(subscriber);
        List<String> items = List.of("a", "b", "c", "d", "e", "f");

        // when
        assertThat(publisher.getNumberOfSubscribers()).isEqualTo(1);
        items.forEach(publisher::submit);
        publisher.close();

        // then
        await().atMost(1000, TimeUnit.MILLISECONDS)
                .until(
                        () ->{
//                            assertThat(subscriber.consumedElements).containsExactlyElementsOf(items);
                            return subscriber.consumedElements.size()==(items.size());}
                );
    }
    @Test
    public void whenSubscribeAndTransformElements_thenShouldConsumeAll() {
        // given
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
        Function<String, String> dup = x -> x.concat(x);
        TransformProcessor<String, String> transformProcessor
                = new TransformProcessor<>(dup);
        EndSubscriber<String> subscriber = new EndSubscriber<>(6);
        List<String> items = List.of("1", "2", "3");
        List<String> expectedResult = List.of("11", "22", "33");
        // when
        publisher.subscribe(transformProcessor);
        transformProcessor.subscribe(subscriber);
        items.forEach(publisher::submit);
        publisher.close();

        await().atMost(1000, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> assertTrue(subscriber.consumedElements.containsAll(expectedResult)));
    }

    @Test
    public void whenRequestForOnlyOneElement_thenShouldConsumeOne(){
        // given
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
        EndSubscriber2<String> subscriber = new EndSubscriber2<>(1);
        publisher.subscribe(subscriber);
        List<String> items = List.of("1", "2", "3", "4", "5", "6");
        List<String> expected = List.of("1");

        // when
        assertEquals(publisher.getNumberOfSubscribers(),1);
        items.forEach(publisher::submit);
        publisher.close();

        // then
        await().atMost(1000, TimeUnit.MILLISECONDS)
                .untilAsserted(() ->
                        assertTrue(subscriber.consumedElements.containsAll(expected))
                );
    }

}
