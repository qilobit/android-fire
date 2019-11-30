package com.qilobit.apppruebas.Models;

public class People {
    private String Name;
    private String Phone;

    public People(String name, String phone){
        this.Name = name;
        this.Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public String getPhone() {
        return Phone;
    }
}
