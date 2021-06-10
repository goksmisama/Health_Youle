package com.youle.pojo;

import java.io.Serializable;

public class Map implements Serializable {
    private Integer id;//id 自增
    private String name;//机构名字
    private String telephone;//机构电话
    private String img;//机构对应图片存储路径
    private String address;//机构地址
    private String latitudeAndLongitude;//机构经纬度

    public Map() {
    }

    public Map(Integer id, String name, String telephone, String img, String address, String latitudeAndLongitude) {
        this.id = id;
        this.name = name;
        this.telephone = telephone;
        this.img = img;
        this.address = address;
        this.latitudeAndLongitude = latitudeAndLongitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitudeAndLongitude() {
        return latitudeAndLongitude;
    }

    public void setLatitudeAndLongitude(String latitudeAndLongitude) {
        this.latitudeAndLongitude = latitudeAndLongitude;
    }

    @Override
    public String toString() {
        return "Map{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", telephone='" + telephone + '\'' +
                ", img='" + img + '\'' +
                ", address='" + address + '\'' +
                ", latitudeAndLongitude='" + latitudeAndLongitude + '\'' +
                '}';
    }
}
