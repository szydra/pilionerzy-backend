package pl.pilionerzy.mapping;

import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;

import java.util.IdentityHashMap;
import java.util.Map;

class LoopAvoidingContext {

    private final Map<Object, Object> knownInstances = new IdentityHashMap<>();

    @BeforeMapping
    @SuppressWarnings("unchecked")
    <T> T getMappedInstance(Object source, @TargetType Class<T> targetType) {
        return (T) knownInstances.get(source);
    }

    @BeforeMapping
    void storeMappedInstance(Object source, @MappingTarget Object target) {
        knownInstances.put(source, target);
    }
}
