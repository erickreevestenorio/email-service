package com.exercise.email.model.response.error;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

import java.util.Locale;

public class LowerCaseClassNameResolver extends TypeIdResolverBase {
    /**
     * {@inheritDoc}
     */

    @Override
    public String idFromValue(Object value) {
        return value.getClass().getSimpleName().toLowerCase(Locale.getDefault());
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromValue(value);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
