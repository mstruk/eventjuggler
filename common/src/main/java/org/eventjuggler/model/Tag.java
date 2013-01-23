package org.eventjuggler.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@Entity
public class Tag {

    @Id
    private String tag;

    public Tag() {}

    public Tag(String value) {
        this.tag = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
