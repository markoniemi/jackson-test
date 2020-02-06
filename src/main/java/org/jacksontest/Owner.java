package org.jacksontest;

import java.time.LocalDate;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Owner extends User {
    @NonNull
    protected LocalDate date;

    public Owner(String name, LocalDate date) {
        super(name);
        this.date = date;
    }
}
