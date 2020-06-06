package com.tracking;

import java.rmi.UnknownHostException;
import static java.time.ZoneId.systemDefault;
import java.time.ZonedDateTime;
import static java.time.ZonedDateTime.ofInstant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 *
 * @author martin
 */
@Configuration
public class MongoConfiguration {

    @Autowired
    MongoDatabaseFactory mongoDbFactory;

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new DateToZonedDateTimeConverter());
        converters.add(new ZonedDateTimeToDateConverter());
        return new MongoCustomConversions(converters);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        MappingMongoConverter converter = new MappingMongoConverter(
                new DefaultDbRefResolver(mongoDbFactory), new MongoMappingContext());
        converter.setCustomConversions(customConversions());
        converter.afterPropertiesSet();
        return new MongoTemplate(mongoDbFactory, converter);
    }

    class DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {

        @Override
        public ZonedDateTime convert(Date source) {
            return source == null ? null : ofInstant(source.toInstant(), systemDefault());
        }
    }

    class ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {

        @Override
        public Date convert(ZonedDateTime source) {
            return source == null ? null : Date.from(source.toInstant());
        }
    }
}
