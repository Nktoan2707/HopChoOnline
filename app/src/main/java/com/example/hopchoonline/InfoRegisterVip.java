package com.example.hopchoonline;

public class InfoRegisterVip {
    String numberBank;
    String fullNameBank;
    String idCard;
    String idUser;
    String registeredDate;
    String expiredDate;

    public InfoRegisterVip(String numberBank, String fullNameBank, String idCard, String idUser, String registeredDate, String expiredDate) {
        this.numberBank = numberBank;
        this.fullNameBank = fullNameBank;
        this.idCard = idCard;
        this.idUser = idUser;
        this.registeredDate = registeredDate;
        this.expiredDate = expiredDate;
    }

    public InfoRegisterVip(String numberBank, String fullNameBank, String idCard, String idUser) {
        this.numberBank = numberBank;
        this.fullNameBank = fullNameBank;
        this.idCard = idCard;
        this.idUser = idUser;
        this.registeredDate = registeredDate;
        this.expiredDate = expiredDate;
    }

    public String getNumberBank() {
        return numberBank;
    }

    public void setNumberBank(String numberBank) {
        this.numberBank = numberBank;
    }

    public String getFullNameBank() {
        return fullNameBank;
    }

    public void setFullNameBank(String fullNameBank) {
        this.fullNameBank = fullNameBank;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getRegisteredDate() { return registeredDate; }

    public void setRegisteredDate(String registeredDate) { this.registeredDate = registeredDate; }

    public String getExpiredDate() { return expiredDate; }

    public void setExpiredDate(String expiredDate) { this.expiredDate = expiredDate; }
}
