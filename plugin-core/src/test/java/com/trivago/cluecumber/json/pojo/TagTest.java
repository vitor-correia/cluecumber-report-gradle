package com.trivago.cluecumber.json.pojo;

import com.trivago.cluecumberCore.json.pojo.Tag;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TagTest {

    private Tag tag;

    @Before
    public void setup() {
        tag = new Tag();
    }

    @Test
    public void getUrlFriendlyNameSimpleNameTest() {
        tag.setName("someName");
        assertThat(tag.getUrlFriendlyName(), is("someName"));
    }

    @Test
    public void getUrlFriendlyNameComplexNameTest() {
        tag.setName("@this_is-@quite%complex");
        assertThat(tag.getUrlFriendlyName(), is("this_is-quite%complex"));
    }
}