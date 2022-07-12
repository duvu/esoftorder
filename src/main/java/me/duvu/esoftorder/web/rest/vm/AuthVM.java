package me.duvu.esoftorder.web.rest.vm;

import java.io.Serializable;

public class AuthVM implements Serializable {
    private String username;
    private String password;
    private Boolean rememberMe;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthVM authVM = (AuthVM) o;

        if (getUsername() != null ? !getUsername().equals(authVM.getUsername()) : authVM.getUsername() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(authVM.getPassword()) : authVM.getPassword() != null)
            return false;
        return getRememberMe() != null ? getRememberMe().equals(authVM.getRememberMe()) : authVM.getRememberMe() == null;
    }

    @Override
    public int hashCode() {
        int result = getUsername() != null ? getUsername().hashCode() : 0;
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getRememberMe() != null ? getRememberMe().hashCode() : 0);
        return result;
    }
}
