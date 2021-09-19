package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Resp resp;
        queue.putIfAbsent(req.nameQueue(), new ConcurrentLinkedQueue<>());
        if ("POST".equals(req.method())) {
            queue.get(req.nameQueue()).add(req.text());
            resp = new Resp("Message created", 201);
        } else {
            String text = queue.get(req.nameQueue()).poll();
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
