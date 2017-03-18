package com.nyceapps.chorerallye.race;

import com.nyceapps.chorerallye.member.MemberItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lugosi on 06.02.17.
 */

public class Race {
    private Date dateStarted;
    private List<RaceItem> raceItems;

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

    public boolean hasMember(String pMemberUid) {
        for (RaceItem raceItem : raceItems) {
            if (raceItem.getMemberUid().equals(pMemberUid)) {
                return true;
            }
        }

        return false;
    }

    public Set<String> removeMembers(String pMemberUid) {
        Set<String> removedRaceItems = new HashSet<>();

        for (int i = raceItems.size() - 1; i >= 0; i--) {
            RaceItem raceItem = raceItems.get(i);
            if (raceItem.getMemberUid().equals(pMemberUid)) {
                removedRaceItems.add(raceItem.getUid());
                raceItems.remove(i);
            }
        }

        return removedRaceItems;
    }

    public Set<String> updateMemberNames(String pMemberUid, String pMemberName) {
        Set<String> updatedRaceItems = new HashSet<>();

        for (RaceItem raceItem : raceItems) {
            if (raceItem.getMemberUid().equals(pMemberUid)) {
                updatedRaceItems.add(raceItem.getUid());
                raceItem.setMemberName(pMemberName);
            }
        }

        return updatedRaceItems;
    }

    public boolean hasChore(String pChoreUid) {
        for (RaceItem raceItem : raceItems) {
            if (raceItem.getChoreUid().equals(pChoreUid)) {
                return true;
            }
        }

        return false;
    }

    public Set<String> removeChores(String pChoreUid) {
        Set<String> removedRaceItems = new HashSet<>();

        for (int i = raceItems.size() - 1; i >= 0; i--) {
            RaceItem raceItem = raceItems.get(i);
            if (raceItem.getChoreUid().equals(pChoreUid)) {
                removedRaceItems.add(raceItem.getUid());
                raceItems.remove(i);
            }
        }

        return removedRaceItems;
    }

    public Set<String> updateChoreNames(String pChoreUid, String pChoreName) {
        Set<String> updatedRaceItems = new HashSet<>();

        for (RaceItem raceItem : raceItems) {
            if (raceItem.getChoreUid().equals(pChoreUid)) {
                updatedRaceItems.add(raceItem.getUid());
                raceItem.setChoreName(pChoreName);
            }
        }

        return updatedRaceItems;
    }

    public Set<String> updateChoreValues(String pChoreUid, int pChoreValue) {
        Set<String> updatedRaceItems = new HashSet<>();

        for (RaceItem raceItem : raceItems) {
            if (raceItem.getChoreUid().equals(pChoreUid)) {
                updatedRaceItems.add(raceItem.getUid());
                raceItem.setChoreValue(pChoreValue);
            }
        }

        return updatedRaceItems;
    }
}
