package com.cxy.doc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 表示当前接口是在哪个更新版本
 */
@Target({ElementType.METHOD})
public @interface JDocVersion {
    float value() default 1.0f;
}
