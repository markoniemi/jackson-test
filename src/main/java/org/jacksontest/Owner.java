package org.jacksontest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Owner extends User {
    protected Long id;

    public Owner(String name, Long id) {
        super(name);
        this.id = id;
    }
}
