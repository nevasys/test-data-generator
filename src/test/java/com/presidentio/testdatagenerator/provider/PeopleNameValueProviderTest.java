/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.presidentio.testdatagenerator.provider;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.presidentio.testdatagenerator.cons.GenderConst;
import com.presidentio.testdatagenerator.cons.NameTypeConst;
import com.presidentio.testdatagenerator.cons.PropConst;
import com.presidentio.testdatagenerator.cons.TypeConst;
import com.presidentio.testdatagenerator.context.Context;
import com.presidentio.testdatagenerator.model.Field;

/**
 * Created by Vitaliy on 24.01.2015.
 */
public class PeopleNameValueProviderTest {

    @Test
    public void testNextValue() throws Exception {
        ValueProvider valueProvider = new PeopleNameProvider();
        valueProvider.init(Collections.<String, String>emptyMap());
        Assert.assertNotNull(valueProvider.nextValue(new Context(null, null, null), new Field(null, TypeConst.STRING, null)));
    }
    
    @Test // TDG-AC-TB01
    public void gender_is_male() throws Exception{
        ValueProvider valueProvider = new PeopleNameProvider();
        String name = generateName(valueProvider, GenderConst.MALE, NameTypeConst.ALL);
        Assert.assertTrue(isMaleName(valueProvider, name)); // make sure we have a male name
    }

    @Test // TDG-AC-TB02
    public void gender_is_female() throws Exception{
        ValueProvider valueProvider = new PeopleNameProvider();
        String name = generateName(valueProvider, GenderConst.FEMALE, NameTypeConst.ALL);
        Assert.assertTrue(isFemaleName(valueProvider, name)); // make sure we have a female name
    }

    @Test // TDG-AC-TB04
    public void gender_is_all() throws Exception{
        ValueProvider valueProvider = new PeopleNameProvider();
        String name = generateName(valueProvider, GenderConst.ALL, NameTypeConst.ALL);
        Assert.assertTrue(isFemaleName(valueProvider, name) || isMaleName(valueProvider, name));
    }

    @Test // TDG-AC-TB05
    public void gender_is_not_provided() throws Exception{
        ValueProvider valueProvider = new PeopleNameProvider();
        String name = generateName(valueProvider, null, NameTypeConst.ALL);
        Assert.assertTrue(isFemaleName(valueProvider, name) || isMaleName(valueProvider, name));
    }

    // TDG-AC-TB06
    @Test(expected = IllegalArgumentException.class)
    public void unknown_gender() throws Exception{
        generateName(new PeopleNameProvider(), "other", NameTypeConst.ALL);
    }
    
    @Test
    // TDG-AC-TB07
    public void first_name_only() throws Exception{
        ValueProvider valueProvider = new PeopleNameProvider();
        String name = generateName(valueProvider, GenderConst.ALL, NameTypeConst.FIRST);
        Assert.assertFalse(isLastNamePresent(valueProvider, name));
    }
    
    @Test
    // TDG-AC-TB08
    public void last_name_only() throws Exception{
        ValueProvider valueProvider = new PeopleNameProvider();
        String name = generateName(valueProvider, GenderConst.ALL, NameTypeConst.LAST);
        Assert.assertTrue(isLastNamePresent(valueProvider, name));
    }

    @Test
    // TDG-AC-TB09
    public void first_last_name_not_provided() throws Exception{
        ValueProvider valueProvider = new PeopleNameProvider();
        String name = generateName(valueProvider, GenderConst.ALL, null);
        Assert.assertTrue(isLastNamePresent(valueProvider, name));
    }    
    
    @Test
    // TDG-AC-TB10
    public void first_and_last_name() throws Exception{
        ValueProvider valueProvider = new PeopleNameProvider();
        String name = generateName(valueProvider, GenderConst.ALL, NameTypeConst.ALL);
        Assert.assertTrue(isLastNamePresent(valueProvider, name));
    }

    @Test(expected = IllegalArgumentException.class)
    // TDG-AC-TB11
    public void first_and_last_invalid() throws Exception{
        ValueProvider valueProvider = new PeopleNameProvider();
        String name = generateName(valueProvider, GenderConst.ALL, "other");
        Assert.assertTrue(isLastNamePresent(valueProvider, name));
    }
    
    private String generateName(ValueProvider valueProvider, String genderProperty, String firstLastNameProperty) {
        Map<String, String> props = new HashMap<>();
        
        if(genderProperty != null)
            props.put(PropConst.GENDER, genderProperty);
        
        if(firstLastNameProperty != null)
            props.put(PropConst.TYPE, firstLastNameProperty);
        
        valueProvider.init(props);
        return String.class.cast(valueProvider.nextValue(new Context(null, null, null), new Field(null, TypeConst.STRING, null)));
    }

    private boolean isMaleName(ValueProvider vp, String name) throws Exception{
        return isMaleOrFemaleName(vp, name, "Male");
    }

    private boolean isFemaleName(ValueProvider vp, String name) throws Exception{
        return isMaleOrFemaleName(vp, name, "Female");
    }

    private boolean isMaleOrFemaleName(ValueProvider vp, String name, String gender) throws Exception{
        String[] names = name.split(" ");
        Assert.assertTrue("names should not be empty",names.length > 0);
        return Arrays.stream(getFirstNames(vp, gender)).filter(names[0]::equals).findAny().isPresent();
    }
    
    private String[] getFirstNames(ValueProvider vp, String gender) throws Exception{
        //Access private method through reflection
        Method method = vp.getClass().getDeclaredMethod(String.format("get%sNames", gender));
        method.setAccessible(true);
        return String[].class.cast(method.invoke(vp)); 
    }
    
    private boolean isLastNamePresent(ValueProvider vp, String name) throws Exception{
        String[] names = name.split(" ");
        Assert.assertTrue("names should not be empty",names.length > 0);
        String n = names.length > 1 ? names[1] : names[0];
        return Arrays.stream(getLastNames(vp)).filter(n::equals).findAny().isPresent();
    }
    
    private String[] getLastNames(ValueProvider vp) throws Exception{
        //Access private method through reflection
        Method method = vp.getClass().getDeclaredMethod("getLastNames");
        method.setAccessible(true);
        return String[].class.cast(method.invoke(vp)); 
    }
}
