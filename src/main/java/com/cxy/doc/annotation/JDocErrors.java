package com.cxy.doc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
public @interface JDocErrors {
    JDocReturn[] values() ;
}
