package cz.aspone.drivers_score.driversscore.BO;

/**
 *  Created by ondrej.vondra on 18.01.2018.
 */
public enum Region {

    NotSpecified("NotSpecified", " "),

    HlavniMestoPraha("HlavniMestoPraha", "A"),

    Stredocesky("Stredocesky", "S"),

    Jihocesky("Jihocesky", "C"),

    Plzensky("Plzensky", "P"),

    Karlovarsky("Karlovarsky", "K"),

    Ustecky("Ustecky", "U"),

    Liberecky("Liberecky", "L"),

    Kralovehradecky("Kralovehradecky", "H"),

    Pardubicky("Pardubicky", "E"),

    Olomoucky("Olomoucky", "M"),

    Moravskoslezsky("Moravskoslezsky", "T"),

    Jihomoravsky("Jihomoravsky", "B"),

    Zlinsky("Zlinsky", "Z"),

    KrajVysocina("KrajVysocina", "J");

    private String Key;
    private String Value;

    Region(String key, String value) {
        Key = key;
        Value = value;
    }

    @Override
    public String toString() {
        return Value;
    }

    public static Region fromString(String x) {
        switch (x.toUpperCase()) {
            case "A":
                return HlavniMestoPraha;
            case "S":
                return Stredocesky;
            case "C":
                return Jihocesky;
            case "P":
                return Plzensky;
            case "K":
                return Karlovarsky;
            case "U":
                return Ustecky;
            case "L":
                return Liberecky;
            case "H":
                return Kralovehradecky;
            case "E":
                return Pardubicky;
            case "M":
                return Olomoucky;
            case "T":
                return Moravskoslezsky;
            case "B":
                return Jihomoravsky;
            case "Z":
                return Zlinsky;
            case "J":
                return KrajVysocina;
            default:
                return NotSpecified;
        }
    }

    public String getKey() {
        return Key;
    }
}
