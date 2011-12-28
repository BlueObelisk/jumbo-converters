package org.xmlcml.cml.converters.cif.dict.units;

import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.element.CMLEntry;

public enum UnitType {
    temperature,amount_concentration, mass_density, current, dimensionless, area, pressure, reciprocal_length,
    electric_potential_difference, volume, power, length, angular_velocity, time, angle, mass;

    public String toString() {
        return "unitType:" + this.name();
    }
    
    public void setUnitType(CMLEntry entry){
        CMLAttribute unitType = new CMLAttribute("unitType",this.toString());
        entry.addAttribute(unitType);
    }
}
