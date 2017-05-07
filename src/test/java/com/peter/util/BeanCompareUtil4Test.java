package com.peter.util;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used to compare two JavaBean.
 *
 * @author yzhang2@telenav.cn
 * @version 0.2
 * @date 2011-7-1
 */
public class BeanCompareUtil4Test extends ClassUtil4Test {

    private static final String CLASS_PROPERTY = "class";
    private static final String SIZE_METHOD = "size";
    private Set<Object> repeatCompareMonitor = new HashSet<Object>();
    private SetCompareUtil4Test setComparator = new SetCompareUtil4Test();
    private String[] escapeProperties = null;

    public void compareBean(Object actual, Object expected, String[] escapeProperties) throws Exception {
        this.escapeProperties = escapeProperties;
        compareBean(actual, expected);
    }

    /**
     * This method is used to compare the primitive properties and List
     * collection properties of two java beans.
     *
     * @param actual
     * @param expected
     * @throws Exception
     */
    public void compareBean(Object actual, Object expected) throws Exception {
        compareBean(actual, expected, "");
    }

    @SuppressWarnings("unchecked")
    public void compareBean(Object actual, Object expected, String beanName) throws Exception {

        // Guard clauses for null parameter value
        if (actual == null && expected == null) {
            return;
        } else if (actual == null || expected == null) {
            assertThat("compare property [" + beanName + "]", actual, equalTo(expected));
            return;
        }

        // Guard clauses for different class type
        guaranteeClassType(actual, expected, beanName);

        // Guard clauses for primitive class type
        if (isPrimitiveType(actual.getClass())) {
            log.info("========= Compare property : " + beanName + " =========");
            assertThat("compare property [" + beanName + "]", actual, equalTo(expected));
            return;
        }

        if (isEnum(actual.getClass())) {
            log.info("========= Compare property : " + beanName + " =========");
            assertThat("compare property [" + beanName + "]", actual.toString(), equalTo(expected.toString()));
            return;
        }

        // Get all the properties from the actual bean
        Map<String, String> properties = BeanUtils.describe(actual);
        properties.remove(CLASS_PROPERTY);
        for (String property : properties.keySet()) {

            if (isEscapedProperty(property, beanName)) {
                continue;
            }

            Class<? extends Object> propertyType = PropertyUtils.getPropertyType(actual, property);
            if (isPrimitiveType(propertyType)) {
                comparePrimitiveProperty(actual, expected, property, beanName);

                // compare indexed property (List or Array)
            } else if (isList(propertyType) || propertyType.isArray()) {
                compareIndexedProperty(actual, expected, property, beanName);

                // compare map property
            } else if (isMap(propertyType)) {
                compareMapProperty(actual, expected, property, beanName);

                // compare set property
            } else if (isSet(propertyType)) {
                assertThat("Set property [" + buildFullProperyName(beanName, property) + "] is eqaul!",
                        setComparator.compareSet(
                                (Set<Object>) PropertyUtils.getProperty(actual, property),
                                (Set<Object>) PropertyUtils.getProperty(expected, property)),
                        equalTo(true));
            } else if (isUtilDate(propertyType)) {
                compareUtilDateProperty(actual, expected, property, beanName);
                // Recursive calling to compare the primitive fields of complex type property.
            } else {
                if (!isRepeatCompare(PropertyUtils.getProperty(actual, property))) {
                    compareBean(PropertyUtils.getProperty(actual, property),
                            PropertyUtils.getProperty(expected, property),
                            buildFullProperyName(beanName, property));
                }
            }
        }
    }

    /**
     * Core method. All the root compare will do in this method.
     *
     * @param actual
     * @param expected
     * @param propertyName
     */
    private void comparePrimitiveProperty(Object actual, Object expected, String propertyName, String beanName)
            throws Exception {

        log.info("========= Compare property : " + buildFullProperyName(beanName, propertyName) + " =========");
        assertThat("compare property [" + buildFullProperyName(beanName, propertyName) + "]",
                BeanUtils.getProperty(actual, propertyName),
                equalTo((BeanUtils.getProperty(expected, propertyName))));
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
    private void compareIndexedProperty(Object actual, Object expected, String propertyName, String beanName)
            throws Exception {

        int actualSize = 0;
        int expectedSize = 0;
        String fullProperyName = buildFullProperyName(beanName, propertyName);

        guaranteeClassType(PropertyUtils.getProperty(actual, propertyName),
                PropertyUtils.getProperty(expected, propertyName), fullProperyName);

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
        assertThat("compare property [" + fullProperyName + "] size comparing: ", actualSize, equalTo(expectedSize));

        for (int i = 0; i < actualSize; i++) {
            compareBean(PropertyUtils.getIndexedProperty(actual, propertyName, i),
                    PropertyUtils.getIndexedProperty(expected, propertyName, i),
                    buildFullProperyName(null, fullProperyName + "[" + i + "]"));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void compareMapProperty(Object actual, Object expected, String propertyName, String beanName)
            throws Exception {

        String fullProperyName = buildFullProperyName(beanName, propertyName);
        guaranteeClassType(PropertyUtils.getProperty(actual, propertyName),
                PropertyUtils.getProperty(expected, propertyName), fullProperyName);

        // get map key set
        Set<String> autualMapKeys = ((Map) PropertyUtils.getProperty(actual, propertyName)).keySet();
        Set<String> expectedMapKeys = ((Map) PropertyUtils.getProperty(expected, propertyName)).keySet();

        assertThat("compare property [" + fullProperyName + "] size comparing: ", autualMapKeys.size(),
                equalTo(expectedMapKeys.size()));

        for (String key : autualMapKeys) {
            compareBean(PropertyUtils.getMappedProperty(actual, propertyName, key),
                    PropertyUtils.getMappedProperty(expected, propertyName, key),
                    fullProperyName + "[" + key + "]");
        }
    }

    private void compareUtilDateProperty(Object actual, Object expected, String propertyName, String beanName)
            throws Exception {

        String fullProperyName = buildFullProperyName(beanName, propertyName);
        guaranteeClassType(PropertyUtils.getProperty(actual, propertyName),
                PropertyUtils.getProperty(expected, propertyName), fullProperyName);

        Date actualDate = (Date) PropertyUtils.getProperty(actual, propertyName);
        Date expectedDate = (Date) PropertyUtils.getProperty(expected, propertyName);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        log.info("========= Compare property : " + buildFullProperyName(beanName, propertyName) + " =========");
        assertThat("compare property [" + buildFullProperyName(beanName, propertyName) + "]",
                dateFormat.format(actualDate).toString(),
                equalTo(dateFormat.format(expectedDate).toString()));
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

    /**
     * Build a complete identifier for the comparing item.
     * <p>
     * For example, a instance of BeanA named ba has a property BeanB named bb,
     * bb has a String type property name then the complete identifier is
     * "ba.bb.name".
     * </p>
     * <p>
     * class BeanA {
     * <p>
     * //other property.....
     * BeanB bb;
     * <p>
     * //set and get methods
     * }
     * <p>
     * class BeanB {
     * <p>
     * String name;
     * //other property....
     * <p>
     * //set and get methods
     * }
     *
     * @param prefix
     * @param subItem
     * @return
     */
    private String buildFullProperyName(String prefix, String subItem) {
        StringBuffer buffer = new StringBuffer();
        if (prefix != null) {
            buffer.append(prefix).append(".");
        }
        buffer.append(subItem);
        return buffer.toString();
    }

    private boolean isEscapedProperty(String propertyName, String beanName) {
        if (escapeProperties == null || beanName.contains(".")) {
            return false;
        }
        for (int i = 0; i < escapeProperties.length; i++) {
            if (propertyName.equals(escapeProperties[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Due the subclass will override the getClass() method, so the method invoked in this class log.debug()
     * will bind log output to the subclass.
     */
    private final Log log = LogFactory.getLog(getClass());
}
