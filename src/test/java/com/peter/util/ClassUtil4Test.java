/*  
 *	Copyright (c) 2011 TeleNav, Inc.
 *  All rights reserved
 */
package com.peter.util;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * This class is used to class type related judge.
 * 
 * @author yzhang2@telenav.cn
 * @date 2011-7-6
 * @version 0.1
 * 
 */
public class ClassUtil4Test {

    /**
     * Guarantee actual bean and expected bean are the same class type,
     * otherwise assert fail and show detail message with assertThat method.
     * 
     * @param actual
     * @param expected
     */
    public void guaranteeClassType(Object actual, Object expected, String beanName) {
        
        granteeNull(actual, expected, beanName);
        
        if (actual.getClass() != expected.getClass()) {
            assertThat(beanName + " class type: ", 
                    actual.getClass().getName(), 
                    equalTo(expected.getClass().getName()));
        }
    }
    
    public boolean isSameClassType(Object actual, Object expected) {
        if (actual.getClass() != expected.getClass()) {
            return false;
        }
        return true;
    }
    /**
     * To judge whether a class is primitive type or not.
     * 
     * <p>
     * java.lang.Class.isPrimitive() return true when class type is<br/>
     * int.clas/double.class/boolean.class/char.class/byte.class/short.class/
     * long.class/double.class. Their wrap class is not primitive.
     * </p>
     * 
     * @param clazz
     * @return whether the class is primitive type or primitive class type.
     * @see Boolean#TYPE
     * @see Character#TYPE
     * @see Byte#TYPE
     * @see Short#TYPE
     * @see Integer#TYPE
     * @see Long#TYPE
     * @see Float#TYPE
     * @see Double#TYPE
     * 
     * @see Number
     * @see Class#isPrimitive()
     */
    public boolean isPrimitiveType(Class<? extends Object> clazz) {
        if (clazz.isPrimitive() || String.class == clazz || Boolean.class == clazz
                || Character.class == clazz || Number.class == clazz.getSuperclass()) {
            return true;
        }
        return false;
    }

    /**
     * To judge whether a class is List Collection.
     * 
     * @param clazz
     * @return whether the class is List
     * @see List
     */
    public boolean isList(Class<? extends Object> clazz) {
        if (clazz == List.class) {
            return true;
        }
        for (Class<?> type : clazz.getInterfaces()) {
            if (type == List.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * To judge whether a class is Map Collection.
     * 
     * @param clazz
     * @return whether the class is Map
     * @see Map
     */
    public boolean isMap(Class<? extends Object> clazz) {
        if (clazz == Map.class) {
            return true;
        }
        for (Class<?> type : clazz.getInterfaces()) {
            if (type == Map.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * To judge whether a class is Set Collection.
     * 
     * @param clazz
     * @return whether the class is Set
     * @see Set
     */
    public boolean isSet(Class<? extends Object> clazz) {
        if (clazz == Set.class) {
            return true;
        }
        for (Class<?> type : clazz.getInterfaces()) {
            if (type == Set.class) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isUtilDate(Class<? extends Object> clazz) {
        return (clazz == Date.class);
    }
    
    public boolean isEnum(Class<? extends Object> clazz) {
        return (clazz.getSuperclass() == Enum.class);
    }
    
    public void granteeNull(Object actual, Object expected, String beanName) {
     // Guard clauses for null parameter value
        if (actual == null && expected == null) {
            return;
        } else if (actual == null || expected == null) {
            assertThat("compare property [" + beanName + "]", actual, equalTo(expected));
            return;
        }
    }
}