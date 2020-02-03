package org.jacksontest;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Owner extends User {
    @NonNull
    protected Date date;

    public Owner(String name, Date date) {
        super(name);
        this.date = date;
    }
}
