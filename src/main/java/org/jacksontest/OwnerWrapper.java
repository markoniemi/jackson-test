package org.jacksontest;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerWrapper {
    @XmlElementWrapper(name = "owners")
    @XmlElement(name = "owner")
    List<Owner> owner;
}
