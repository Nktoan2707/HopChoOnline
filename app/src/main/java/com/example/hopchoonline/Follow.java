package com.example.hopchoonline;

import java.util.Objects;

public class Follow {

    String idUser;
    String idUserFollowing;



    public Follow(String idUser, String idUserFollowing) {
        this.idUser = idUser;
        this.idUserFollowing = idUserFollowing;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdUserFollowing() {
        return idUserFollowing;
    }

    public void setIdUserFollowing(String idUserFollowing) {
        this.idUserFollowing = idUserFollowing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Follow follow = (Follow) o;
        return Objects.equals(idUser, follow.idUser) && Objects.equals(idUserFollowing, follow.idUserFollowing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, idUserFollowing);
    }
}
