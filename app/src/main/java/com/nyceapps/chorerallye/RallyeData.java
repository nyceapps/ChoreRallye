package com.nyceapps.chorerallye;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bela on 08.02.17.
 */

public class RallyeData {
    private Map<String, Object> settings;
    private List<MemberItem> members;
    private List<ChoreItem> chores;
    private Race race;

    public RallyeData() {
        settings = new HashMap<>();
        members = new ArrayList<>();
        chores = new ArrayList<>();
        race = new Race();
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> pSettings) {
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
