package ru.annikonenkov.ejbpack.calc;

import javax.ejb.Local;

@Local
public interface ICalck {

    int summ(int i, int j);

    int difference(int i, int j);

    String getFullInfo();

}
