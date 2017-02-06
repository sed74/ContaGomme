package com.marchesi.federico.contagomme.Utils;

/**
 * Created by federico.marchesi on 06/02/2017.
 */

public class EmailClass {
/*
    private String getEmailBodyHTML() {

        String emailContent = "";//"<html><body><br>";

        emailContent += "Gara di <b>" + mRaceName + "</b><br><br>";

        if (!mRaceDescr.isEmpty()) {
            emailContent += "\n\n" + mRaceDescr + "<br>";
        }
        for (TireBrands t :
                arrayTireBrands) {
            emailContent += "<p>";
            emailContent += "<b>" + t.getName() + "</b><br>";
            emailContent += "Anteriori: " + t.getTotFrontSelected() + "<br>";
            emailContent += "Posteriori: " + t.getTotRearSelected() + "<br>";
            emailContent += "</p>";
        }
        //emailContent += "</body></meta></html>";
//        Toast.makeText(this, emailContent, Toast.LENGTH_LONG).show();
        return emailContent;
    }

    private String getEmailBody(String subject, ArrayList<WheelList>) {

        String emailContent = "";

        emailContent += "Gara di " + mRaceName;
        emailContent += "\n\n";

        if (!mRaceDescr.isEmpty()) {
            emailContent += "\n\n" + mRaceDescr;
        }
        for (TireBrands t :
                arrayTireBrands) {
            emailContent += "\n\n" + t.getName();
            emailContent += "\n" + "Anteriori: " + t.getTotFrontSelected();
            emailContent += "\n" + "Posteriori: " + t.getTotRearSelected();
        }

        return emailContent;
    }

    private String getEmailBodyCSV() {

        String emailContent = "";

        emailContent += getResources().getString(R.string.race_name) + mRaceName;
        emailContent += "\n";
        emailContent += getResources().getString(R.string.brand) + TEXT_SEPARATOR;
        emailContent += getResources().getString(R.string.front_tire) + TEXT_SEPARATOR;
        emailContent += getResources().getString(R.string.rear_tire) + TEXT_SEPARATOR;

        for (TireBrands t : arrayTireBrands) {
            emailContent += "\n" + t.getName() + TEXT_SEPARATOR;
            emailContent += t.getTotFrontSelected() + TEXT_SEPARATOR;
            emailContent += t.getTotRearSelected() + TEXT_SEPARATOR;
        }

        return emailContent;
    }
*/
}
