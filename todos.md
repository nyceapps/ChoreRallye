* Hauptmenü
  * Rallye starten beenden
  * Icons
* History bearbeiten
  * http://www.vogella.com/tutorials/AndroidListView/article.html#arrayAdapter
* Rallye-Einstellungen in der Datenbank
  * Länge des Rennens
  * x% bis zum Gewinn (Standard: 67%)
  * %-Vorsprung, z. B. +5%
  * Einfach-Gewinner
  * Geschenk oder Pott
    * Bei Pott: Höhe des Potts
* Neu-Berechnung des aktuellen Standes bei
  * Erstmal verhindern, wenn schon im Gebrauch
  * Bei Entfernen eines Members oder Chores
  * Veränderung des Wertes eines Chores
* Notifications
* Toast auch in den anderen Apps anzeigen
* Statistiken
  * Diagramme
  * Druck
* Freie Chores
  * Bewertung durch Abstimmung
* Komplette Anzeige von Titel im Chores-Grid
* Übersetzungen
  * https://developer.android.com/studio/write/translations-editor.html
  * https://developer.android.com/distribute/tools/localization-checklist.html
  * https://developer.android.com/guide/topics/resources/localization.html
* Photo für Members / Chores auf Shift5 (Android 4.4.2) nicht möglich
  * https://www.google.de/webhp?sourceid=chrome-instant&ion=1&espv=2&ie=UTF-8#q=android+4.4.2+MediaStore.ACTION_IMAGE_CAPTURE+gallery+crashes
  * http://stackoverflow.com/questions/12952859/capturing-images-with-mediastore-action-image-capture-intent-in-android
* Icons
  * https://developer.android.com/guide/practices/ui_guidelines/icon_design_menu.html
  * https://developer.android.com/guide/practices/ui_guidelines/icon_design_launcher.html
* App-Store-Beta
  * https://support.google.com/googleplay/android-developer/answer/3131213?hl=de
* Backup per JSON-Datei
* RaceItem um Infos ergänzen: Welche anonyme User-Id hat's erfasst
* Effekt für die Bilder?
* Icon-Rechte klären
* Fortschritt: Rennen der Männchen
  * Spur der Mänchen, graue Linie?
  * schwarze Linie 0%, 100%
  * rote Linie bei konfigurierten %
  * Background-Task für die Bewegung
    * https://developer.android.com/reference/android/animation/ValueAnimator.html
* "Bitte warten"-Dialog bis die DB-Listener fertig sind
  * ProgressDialog dialog = ProgressDialog.show(context, "Loading", "Please wait...", true);
* Trennlinien UI
