package ucb.judge.ujsubmissions.config

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqConfig {
    @Bean
    fun submission2Exchange(): DirectExchange {
        return DirectExchange("submission2Exchange")
    }

    @Bean
    fun submission2Queue(): DirectExchange {
        return DirectExchange("submission2Queue")
    }

    @Bean
    fun submission2Binding(submission2Queue: DirectExchange, submission2Exchange: DirectExchange): Binding {
        return BindingBuilder
            .bind(submission2Queue)
            .to(submission2Exchange)
            .with("submission2RoutingKey")
    }

    @Bean
    fun converter(): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun amqpTemplate(connectionFactory: ConnectionFactory): AmqpTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = converter()
        return rabbitTemplate
    }
}