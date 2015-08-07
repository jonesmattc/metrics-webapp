package com.mattj.dropzard.resources;

import com.mattj.dropzard.core.Saying;
import com.google.common.base.Optional;
import com.codahale.metrics.annotation.Timed;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {
    private final String template;
    private final String defaultName;
    private final KafkaProducer<String, String> kafkaProducer;
    private final AtomicLong counter;

    public HelloWorldResource(String template, String defaultName, KafkaProducer<String, String> kafkaProducer) {
        this.template = template;
        this.defaultName = defaultName;
        this.kafkaProducer = kafkaProducer;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.or(defaultName));
        ProducerRecord<String, String> pr = new ProducerRecord<>("test", 0, null, "this is a test");
        kafkaProducer.send(pr);
        return new Saying(counter.incrementAndGet(), value);
    }
}
