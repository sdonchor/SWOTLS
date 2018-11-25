package application;

public class Entry {
    private final String attribute;
    private final String value;
    public Entry(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }
    public String getAttribute() {
        return attribute;
    }
    public String getValue() {
        return value;
    }
}
