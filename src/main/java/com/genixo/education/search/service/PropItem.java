package com.genixo.education.search.service;

public class PropItem {
    private String nameGroupType;
    private String namePropertyType;
    private String nameProperty;

    public PropItem(String nameGroupType, String namePropertyType, String nameProperty) {
        this.nameGroupType = nameGroupType;
        this.namePropertyType = namePropertyType;
        this.nameProperty = nameProperty;
    }

    public String getNameGroupType() {
        return nameGroupType;
    }

    public String getNamePropertyType() {
        return namePropertyType;
    }

    public String getNameProperty() {
        return nameProperty;
    }

    @Override
    public String toString() {
        return "Item{" +
                "nameGroupType='" + nameGroupType + '\'' +
                ", namePropertyType='" + namePropertyType + '\'' +
                ", nameProperty='" + nameProperty + '\'' +
                '}';
    }
}