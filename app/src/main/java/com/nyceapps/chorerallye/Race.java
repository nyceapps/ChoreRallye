package com.nyceapps.chorerallye;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lugosi on 06.02.17.
 */

public class Race {
    Date dateStarted;
    List<RaceItem> raceItems;

    public Race() {
        dateStarted = new Date();
        raceItems = new ArrayList<>();
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date pDateStarted) {
        dateStarted = pDateStarted;
    }

    public List<RaceItem> getRaceItems() {
        return raceItems;
    }

    public void setRaceItems(List<RaceItem> pRaceItems) {
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
