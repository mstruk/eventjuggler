package org.eventjuggler.rest;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Address {

    private Long id;

    private String name;
    private String imageId;
    private String street;
    private String streetNum;
    private String city;
    private String zip;
    private String country;
    private String latitude;
    private String longitude;

    public Address() {
    }

    public Address(org.eventjuggler.model.Address a) {
        this.id = a.getId();
        this.name = a.getName();
        this.street = a.getStreet();
        this.streetNum = a.getStreetNum();
        this.city = a.getCity();
        this.zip = a.getZip();
        this.country = a.getCountry();
        this.latitude = a.getLatitude();
        this.longitude = a.getLongitude();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageId() {
        return imageId;
    }

    public String getStreet() {
        return street;
    }

    public String getStreetNum() {
        return streetNum;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }

    public String getCountry() {
        return country;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

   public org.eventjuggler.model.Address toInternal() {
      final org.eventjuggler.model.Address address = new org.eventjuggler.model.Address();
      address.setCity(getCity());
      address.setCountry(getCountry());
      address.setImageId(getImageId());
      address.setLatitude(getLatitude());
      address.setLongitude(getLongitude());
      address.setName(getName());
      address.setStreet(getStreet());
      address.setStreetNum(getStreetNum());
      address.setZip(getZip());
      return address;
   }
}
