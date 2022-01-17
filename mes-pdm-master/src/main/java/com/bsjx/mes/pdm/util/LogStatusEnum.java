package com.bsjx.mes.pdm.util;

public enum LogStatusEnum {
    INSERT("1"),ERROR("0"),UPDATE("2"),QUERY("3");;
    private String name;

    private LogStatusEnum(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return this.name;
    }
 }
