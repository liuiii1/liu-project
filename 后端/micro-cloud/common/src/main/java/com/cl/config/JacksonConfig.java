package com.cl.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Jackson 全局配置：所有 Double 金额字段序列化为两位小数
 * - 返回 JSON number 类型（不是 string），前端排序/计算不受影响
 * - 示例：48.8 * 3 = 146.39999999999998 → 输出 146.40
 */
@Configuration
public class JacksonConfig {

    /**
     * 自定义 Double 序列化器：保留两位小数，ROUND_HALF_UP 四舍五入
     */
    public static class MoneyDoubleSerializer extends JsonSerializer<Double> {
        @Override
        public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }
            BigDecimal bd = BigDecimal.valueOf(value);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            gen.writeNumber(bd);
        }
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Double.class, new MoneyDoubleSerializer());
        module.addSerializer(Double.TYPE, new MoneyDoubleSerializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
