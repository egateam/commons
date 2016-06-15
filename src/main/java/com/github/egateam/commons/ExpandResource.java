/**
 * THE SOFTWARE IS PROVIDED "AS IS" WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 */

package com.github.egateam.commons;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ExpandResource {
    private final String value;

    public ExpandResource(String value) {
        this.value = value;
    }

    public String invoke() throws Exception {
        // http://stackoverflow.com/questions/5529532/how-to-get-a-test-resource-file
        URL url = Thread.currentThread().getContextClassLoader().getResource(value);
        if ( url != null ) {
            return new File(url.getPath()).toString();
        } else {
            throw new IOException(String.format("Resource file [%s] doesn't exist", value));
        }
    }

    public File invokeFile() throws Exception {
        // http://stackoverflow.com/questions/5529532/how-to-get-a-test-resource-file
        URL url = Thread.currentThread().getContextClassLoader().getResource(value);
        if ( url != null ) {
            return new File(url.getPath());
        } else {
            throw new IOException(String.format("Resource file [%s] doesn't exist", value));
        }
    }
}
