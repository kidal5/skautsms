package com.example.skautsmssend

enum class StateOfGame(val text: String) {
    NOT_SEEN("NEMELO BY SE STAT, pokud jo, tak mi napis. CERT "),
    WRONG_CODE("Bohuzel, zadal jsi spatne tajne heslo, ktere je potreba k odemknuti treti faze. Zkus to prosim znovu. Heslo zadavej presne tak, jak jsi ho ziskal."),
    REGISTERED(
        "Vitej ve treti fazi hry. Pro uspesne splneni budes potrebovat cca. 2 hodiny kdekoliv pobliz lesa. Idelani stav je byt nekde na prochazce." +
                "V teto fazi ti bude poslano nekolik SMS s ruznym casovym rozestupem. Jakmile ti prijde SMS, tak spln ukol, ktery je v ni napsany. Celou fazi mas splnenou ay ti prijde zaverecna SMS. " +
                "Bude zretelne oznacena. Pokud ti nahodou prijde dalsi SMS a ty jsi jeste nesplnil predchozi ukol, tak to nevadi. Proste je spln postupne vsechny. "
    ),
    IN_PHASE_THROW("Pribeh, trem pomoci hodu z jedonoho mista 5 ruznych stromu."),
    IN_PHASE_BUILD("Pribeh, postav hezky domecek."),
    IN_PHASE_SOMETHING("Pribeh, udelej ukol, ktery jeste nevim co ma delat."),
    IN_PHASE_FINISHED(
        "Vyborne, uspesne jsi splnil treti fazi z nasi skautske hry. Uz jsi skoro na konci. Pro ziskani odemykaciho kodu do ctvrte a posledni faze dostanes nasledujci ukol. " +
                "Uvar doma s rodici nejake dobre jidlo, vyfotte se spolu a posli tuto fotku na tobe jiz znamy email 'hra@skaut.cz'. " +
                "Predmetem toho emailu bude 'KONEC FAZE 3' a uvnitr bude jako priloha zminena fotka."
    ),
    SEND_SMS_IN_GAME("Uz jsi aktualne zaregistrovan ve hre."),
    SEND_SMS_ALREADY_FINISHED(
        "Treti fazi jsi jiz uspesne dokoncil. Pokus jsi nahodou ztratil nebo zapomnel, co mas udelat dal, tak zde jeste jednou shrnuti. " +
                "Je potreba doma s rodici uvarit nejake dobre jidlo, vyfotit se spolu a poslat tuto fotku na tobe jiz znamy email 'hra@skaut.cz'. " +
                "Predmetem toho emailu bude 'KONEC FAZE 3' a uvnitr bude jako priloha zminena fotka."
    );

    companion object {
        fun isInside(text: String): Boolean {
            for (state in StateOfGame.values()) {
                if (text in state.text) return true
            }
            return false
        }
    }
}