package me.duvu.esoftorder.web.rest.vm;

import java.io.Serializable;

public class TokenVM implements Serializable {
    private String access_token;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokenVM tokenVM = (TokenVM) o;

        return getAccess_token() != null ? getAccess_token().equals(tokenVM.getAccess_token()) : tokenVM.getAccess_token() == null;
    }

    @Override
    public int hashCode() {
        return getAccess_token() != null ? getAccess_token().hashCode() : 0;
    }
}
