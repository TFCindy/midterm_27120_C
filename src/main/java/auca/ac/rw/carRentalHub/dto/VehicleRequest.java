package auca.ac.rw.carRentalHub.dto;

import java.math.BigDecimal;

import auca.ac.rw.carRentalHub.model.enums.EVehicleStatus;

public class VehicleRequest {

    private String licensePlate;
    private String brand;
    private String model;
    private Integer year;
    private BigDecimal dailyRate;
    private EVehicleStatus status;

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public EVehicleStatus getStatus() {
        return status;
    }

    public void setStatus(EVehicleStatus status) {
        this.status = status;
    }
}

