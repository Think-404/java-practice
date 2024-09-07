package org.originit.annotation;

import org.originit.converter.Converter;

public @interface ValueConverter {

    Class<? extends Converter> value();
}
