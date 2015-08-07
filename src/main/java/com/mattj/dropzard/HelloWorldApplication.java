package com.mattj.dropzard;

import com.google.common.collect.ImmutableList;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import com.mattj.dropzard.resources.HelloWorldResource;
import com.mattj.dropzard.health.TemplateHealthCheck;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {
    public static void main(String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(HelloWorldConfiguration configuration,
                    Environment environment) {

        Map<String, Object> kafkaConfig = new HashMap<>();
        kafkaConfig.put("metadata.broker.list", "localhost:9092");
        kafkaConfig.put("bootstrap.servers", ImmutableList.of("localhost:9092"));
        kafkaConfig.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        kafkaConfig.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        kafkaConfig.put("request.required.acks", "1");
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(kafkaConfig);
        final HelloWorldResource resource = new HelloWorldResource(
                configuration.getTemplate(),
                configuration.getDefaultName(),
                kafkaProducer
        );
        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }

}
