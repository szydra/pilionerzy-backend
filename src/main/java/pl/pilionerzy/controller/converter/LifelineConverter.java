package pl.pilionerzy.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.pilionerzy.model.Lifeline;

@Component
public class LifelineConverter implements Converter<String, Lifeline> {

    @Override
    public Lifeline convert(String source) {
        return Lifeline.valueOf(source.toUpperCase().replaceAll("-", "_"));
    }
}
