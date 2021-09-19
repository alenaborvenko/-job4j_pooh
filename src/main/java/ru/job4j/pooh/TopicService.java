package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> topics = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>>> subscriptions =
            new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Resp resp;
        topics.putIfAbsent(req.nameQueue(), new ConcurrentLinkedQueue<>());
        subscriptions.putIfAbsent(req.nameQueue(), new ConcurrentHashMap<>());
        if (req.method().equals("POST")) {
            topics.get(req.nameQueue()).add(req.text());
            subscriptions.get(req.nameQueue()).values().forEach(strings -> strings.add(req.text()));
            resp = new Resp("Topic created", 201);
        } else {
            subscriptions.get(req.nameQueue()).putIfAbsent(req.id(), new ConcurrentLinkedQueue<>(topics.get(req.nameQueue())));
            String text = subscriptions.get(req.nameQueue()).get(req.id()).poll();
            int status = 200;
            if (text == null) {
                status = 404;
                text = "Message not found";
            }
            resp = new Resp(text, status);
        }
        return resp;
    }
}
