package com.example.hwada.Model.userAddress;

public class Suggestion {
    private Data data;
    private String unrestrictedValue;
    private String value;

    public Data getData() {
        return data;
    }

    public void setData(Data value) {
        this.data = value;
    }

    public String getUnrestrictedValue() {
        return unrestrictedValue;
    }

    public void setUnrestrictedValue(String value) {
        this.unrestrictedValue = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Suggestion{" +
                "data=" + data +
                ", unrestrictedValue='" + unrestrictedValue + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
