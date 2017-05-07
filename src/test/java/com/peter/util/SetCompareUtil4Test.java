/*  
 *	Copyright (c) 2011 TeleNav, Inc.
 *  All rights reserved
 */
package com.peter.util;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * This class is used to compare two JavaBean.
 *
 * @author yzhang2@telenav.cn
 * @version 0.2
 * @date 2011-7-1
 */
public class SetCompareUtil4Test extends ClassUtil4Test {

    private static final String CLASS_PROPERTY = "class";
    private static final String SIZE_METHOD = "size";
    private Set<Object> repeatCompareMonitor = new HashSet<Object>();

    public boolean compareSet(Set<Object> actualSet, Set<Object> expectedSet) throws Exception {

        if (actualSet.size() != expectedSet.size()) {
            return false;
        }

        int eqaulEntryCount = 0;

        // check whether entry of actual set all contained in expected set
        for (Object actualEntry : actualSet) {
            //boolean isActualEntryInExpectedSet = false;
            for (Object expectedEntry : expectedSet) {
                if (compareBean(actualEntry, expectedEntry)) {
                    expectedSet.remove(expectedEntry);
                    eqaulEntryCount++;
                    //isActualEntryInExpectedSet = true;
                    break;
                }
            }
            /*if (!isActualEntryInExpectedSet) {
                fail("Set entry " + actualEntry + "is not in expectedSet");
            }*/
        }
        return (eqaulEntryCount == actualSet.size() && expectedSet.size() == 0) ? true : false;
    }

    /**
     * This method is used to compare the primitive properties and List
     * collection properties of two java beans.
     *
     * @param actual
     * @param expected
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private boolean compareBean(Object actual, Object expected) throws Exception {

        // Guard clauses for null parameter value
        if (actual == null && expected == null) {
            return true;
        } else if (actual == null || expected == null) {
            return false;
        }

        // Guard clauses for different class type
        if (!isSameClassType(actual, expected)) {
            return false;
        }

        // Guard clauses for primitive class type
        if (isPrimitiveType(actual.getClass())) {
            return actual.equals(expected);
        }

        // Get all the properties from the actual bean
        Map<String, String> properties = BeanUtils.describe(actual);
        properties.remove(CLASS_PROPERTY);
        for (String property : properties.keySet()) {
            Class<? extends Object> propertyType = PropertyUtils.getPropertyType(actual, property);
            if (isPrimitiveType(propertyType) && !comparePrimitiveProperty(actual, expected, property)) {
                return false;
            } else if (isList(propertyType) && !compareIndexedProperty(actual, expected, property)
                    || propertyType.isArray() && !compareIndexedProperty(actual, expected, property)) {
                // compare indexed property (List or Array)
                return false;
            } else if (isMap(propertyType) && !compareMapProperty(actual, expected, property)) {
                // compare map property
                return false;
            } else if (isSet(propertyType)) {
                // compare Set size
                return compareSet((Set<Object>) PropertyUtils.getProperty(actual, property),
                        (Set<Object>) PropertyUtils.getProperty(expected, property));
            } else {
                if (!isRepeatCompare(PropertyUtils.getProperty(actual, property))
                        && !compareBean(PropertyUtils.getProperty(actual, property),
                        PropertyUtils.getProperty(expected, property))) {
                    // Recursive calling to compare the primitive fields of
                    // complex type property.
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Core method. All the root compare will do in this method.
     *
     * @param actual
     * @param expected
     * @param propertyName
     */
    private boolean comparePrimitiveProperty(Object actual, Object expected, String propertyName)
            throws Exception {
        return BeanUtils.getProperty(actual, propertyName)
                .equals(BeanUtils.getProperty(expected, propertyName));
    }

    /**
     * This method is used to compare the collection property specified
     * properyName.
     *
     * @param actual       actual bean.
     * @param expected     expected bean.
     * @param propertyName the property name of the collection property.
     * @throws Exception
     */
    private boolean compareIndexedProperty(Object actual, Object expected, String propertyName)
            throws Exception {

        int actualSize = 0;
        int expectedSize = 0;

        if (!isSameClassType(actual, expected)) {
            return false;
        }

        if (!PropertyUtils.getPropertyType(actual, propertyName).isArray()) {
            // get list's size
            actualSize = getCollectionPropertySize(actual, propertyName);
            expectedSize = getCollectionPropertySize(expected, propertyName);
        } else {
            // get array's size
            actualSize = Array.getLength(PropertyUtils.getProperty(actual, propertyName));
            expectedSize = Array.getLength(PropertyUtils.getProperty(expected, propertyName));
        }

        // Guard assert for collection size
        if (actualSize != expectedSize) {
            return false;
        }

        for (int i = 0; i < actualSize; i++) {
            if (!compareBean(PropertyUtils.getIndexedProperty(actual, propertyName, i),
                    PropertyUtils.getIndexedProperty(expected, propertyName, i))) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean compareMapProperty(Object actual, Object expected, String propertyName)
            throws Exception {

        if (!isSameClassType(actual, expected)) {
            return false;
        }

        // get map key set
        Set<String> autualMapKeys = ((Map) PropertyUtils.getProperty(actual, propertyName)).keySet();
        Set<String> expectedMapKeys = ((Map) PropertyUtils.getProperty(expected, propertyName)).keySet();

        // Guard assert for collection size
        if (autualMapKeys.size() != expectedMapKeys.size()) {
            return false;
        }

        for (String key : autualMapKeys) {
            if (!compareBean(PropertyUtils.getMappedProperty(actual, propertyName, key),
                    PropertyUtils.getMappedProperty(expected, propertyName, key))) {
                return false;
            }
        }
        return true;
    }


    /**
     * Use reflect to invoke the size() method of a collection property to get
     * the collection's size.
     *
     * @param bean
     * @param properyName
     * @return
     * @throws Exception
     */
    private int getCollectionPropertySize(Object bean, String properyName) throws Exception {
        return (Integer) MethodUtils.invokeMethod(PropertyUtils.getProperty(bean, properyName), SIZE_METHOD,
                null);
    }

    /**
     * Use instance to check whether it's a repeat compare.
     * <p>
     * For the complex type property, before every recursive calling, it will be
     * record in monitor.
     * </p>
     *
     * @param instance
     * @return estimate whether it is a repeat compare.
     */
    private boolean isRepeatCompare(Object instance) {
        if (!repeatCompareMonitor.contains(instance)) {
            repeatCompareMonitor.add(instance);
            return false;
        } else {
            return true;
        }
    }

}
