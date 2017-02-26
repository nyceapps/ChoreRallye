package com.nyceapps.chorerallye;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lugosi on 06.02.17.
 */

public class Race {
    List<RaceItem> raceItems;

    public Race() {
        raceItems = new ArrayList<>();
    }

    /*
    public RaceItem getRaceItem(String pUid) {
        if (raceItems != null) {
            for (RaceItem raceItem : raceItems) {
                if (raceItem.getUid().equals(pUid)) {
                    return raceItem;
                }
            }
        }

        return null;
    }
    */

    public void setRaceItem(List<RaceItem> pRaceItems) {
        raceItems = pRaceItems;
    }

    public int getPoints(MemberItem pMember) {
        int memberPoints = 0;
        String uid = pMember.getUid();

        for (RaceItem raceItem : raceItems) {
            if (raceItem.getMemberUid().equals(uid)) {
                memberPoints += raceItem.getChoreValue();
            }
        }

        return memberPoints;
    }

    public int getTotalPoints() {
        int totalPoints = 0;

        for (RaceItem raceItem : raceItems) {
            totalPoints += raceItem.getChoreValue();
        }

        return totalPoints;
    }
}
