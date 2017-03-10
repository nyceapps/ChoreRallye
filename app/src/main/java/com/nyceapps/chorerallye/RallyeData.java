package com.nyceapps.chorerallye;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bela on 08.02.17.
 */

public class RallyeData {
    private Settings settings;
    private List<MemberItem> members;
    private List<ChoreItem> chores;
    private Race race;

    public RallyeData() {
        settings = new Settings();
        members = new ArrayList<>();
        chores = new ArrayList<>();
        race = new Race();
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings pSettings) {
        settings = pSettings;
    }

    public List<MemberItem> getMembers() {
        return members;
    }

    public void setMembers(List<MemberItem> pMembers) {
        members = pMembers;
    }

    public List<ChoreItem> getChores() {
        return chores;
    }

    public void setChores(List<ChoreItem> pChores) {
        chores = pChores;
    }

    public Race getRace() {
        return race;
    }
}
