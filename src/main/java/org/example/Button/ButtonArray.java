package org.example.Button;

import static org.example.Button.ButtonName.*;

public interface ButtonArray {
    String[][] userMenu = {
            {EXIT, SAVAT},
            {WOMEN, MENS},
            {DETISKI, PANTALON},
            {MAYKALAR, NEW_GOODS, SALES_GOODS},
    };
    String[][] blocUser = {
            {CHAT_WITH_ADMIN,EXIT}
    };
    String[][] firstMenu = {
            {ORDER_GOODS},
            {SAVAT, CHAT_WITH_ADMIN}
    };

    String[][] adminMarkup = {
            {ADD_GOODS, DELETE_GOODS},
            {ACTIVE_USERS}
    };
    String[][] exit = {
            {EXIT, SAVAT}
    };
    String[][] exitadmin = {
            {EXIT}
    };
    String[][] womens = {
            {EXIT, SAVAT},
            {QIMMAT, ARZON},
            {PADRASKOVI,TOPIK},
            {MERS, ALL_WOMEN_GOODS}
    };
    String[][] detski = {
            {EXIT, SAVAT},
            {UGIL_BOLA, QIZ_BOLA_LASTCHKA, QIZ_BOLA_SHORTIK}
    };
//    String[][] padraskovi = {
//            {QIMMAT, ARZON},
//            {EXIT}
//    };

    String[][] adminMenu = {
            {EXIT, ADD_QIMMAT, ADD_ARZON},
            {ADD_MENS, ADD_PANTALON, ADD_MAYKALAR},
            {ADD_TOPIK, ADD_MERS, ADD_PADRASKOVI},
            {ADD_UGIL_BOLA, ADD_QIZ_BOLA_LASTCHKA, ADD_QIZ_BOLA_SHORTIK},
            {ADD_SALES_GOODS, ADD_ALL_WOMEN_GOODS, ADD_NEW_GOODS},
    };
    String[][] adminDeleteMenu = {
            {EXIT, DELETE_QIMMAT, DELETE_ARZON},
            {DELETE_MENS, DELETE_PANTALON, DELETE_MAYKALAR},
            {DELETE_TOPIK, DELETE_PADRASKOVI, DELETE_MERS},
            {DELETE_UGIL_BOLA, DELETE_QIZ_BOLA_LASTCHKA, DELETE_QIZ_BOLA_SHORTIK},
            {DELETE_SALES_GOODS, DELETE_ALL_WOMEN_GOODS, DELETE_NEW_GOODS},
    };

}