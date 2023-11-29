package org.example;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicInteger;

public class EndSubscriber2<T> implements Flow.Subscriber<T> {
    // 多少消息需要消费
    private final AtomicInteger howMuchMessagesToConsume;
    private Flow.Subscription subscription;
    // 保存消费过的消息
    public List<T> consumedElements = new LinkedList<>();

    public EndSubscriber2(Integer howMuchMessagesToConsume) {
        this.howMuchMessagesToConsume = new AtomicInteger(howMuchMessagesToConsume);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(T item) {
        howMuchMessagesToConsume.decrementAndGet(); // 减一
        System.out.println("Got : " + item);
        consumedElements.add(item);
        if (howMuchMessagesToConsume.get() > 0) {
            subscription.request(1);
        }
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("Done");
    }
}