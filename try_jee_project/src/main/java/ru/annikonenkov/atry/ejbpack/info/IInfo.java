package ru.annikonenkov.atry.ejbpack.info;

import javax.ejb.Local;

@Local
public interface IInfo {

    public String getInfoEJBContext();

    public String getInfoSecurityContext();

    public String getInfoSessionContext();

}
