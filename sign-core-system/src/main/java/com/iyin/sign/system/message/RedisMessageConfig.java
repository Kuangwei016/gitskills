//package com.iyin.sign.system.message;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.listener.PatternTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
//
///**
// * @ClassName: RedisMessageConfig
// * @Description: redis消息中间件
// * @Author: luwenxiong
// * @CreateDate: 2019/8/9 15:47
// * @UpdateUser: Administrator
// * @UpdateDate: 2019/8/9 15:47
// * @Version: 0.0.1
// */
//@Configuration
//public class RedisMessageConfig {
//
//    @Autowired
//    MessageReceiver receiver;
//
//    /**
//     * redis消息监听器容器
//     * 可以添加多个监听不同话题的redis监听器，只需要把消息监听器和相应的消息订阅处理器绑定，该消息监听器
//     * 通过反射技术调用消息订阅处理器的相关方法进行一些业务处理
//     * @param connectionFactory
//     * @param messageListenerAdapter
//     * @return
//     */
//    @Bean
//    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        //订阅了一个叫chat 的通道
//        container.addMessageListener(messageListenerAdapter, new PatternTopic("register"));
//        //这个container 可以添加多个 messageListener
//        return container;
//    }
//
//
//
//    /**
//     * 消息监听器适配器，绑定消息处理器，利用反射技术调用消息处理器的业务方法
//     * @param receiver
//     * @return
//     */
//    @Bean
//    MessageListenerAdapter messageListenerAdapter(MessageReceiver receiver) {
//        //这个地方 是给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“receiveMessage”
//        //也有好几个重载方法，这边默认调用处理器的方法 叫handleMessage 可以自己到源码里面看
//        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver, "receiveMessage");
//        return messageListenerAdapter;
//    }
//
//    /**redis 读取内容的template */
//    @Bean
//    StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
//        return new StringRedisTemplate(connectionFactory);
//    }
//}
