package com.example.findit.model;

import com.example.findit.dao.ClaimRequestDAO;
import com.example.findit.dao.ItemReportDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class AppDataStore {
    private static final ObservableList<ItemReport> ITEM_REPORTS = FXCollections.observableArrayList();
    private static final ObservableList<ClaimRequest> CLAIM_REQUESTS = FXCollections.observableArrayList();
    private static final ObservableList<ItemMatch> MATCH_SUGGESTIONS = FXCollections.observableArrayList();
    private static final ItemReportDAO ITEM_REPORT_DAO = new ItemReportDAO();
    private static final ClaimRequestDAO CLAIM_REQUEST_DAO = new ClaimRequestDAO();

    static {
        refreshAll();
    }

    private AppDataStore() {
    }

    public static ObservableList<ItemReport> getItemReports() {
        return ITEM_REPORTS;
    }

    public static ObservableList<ClaimRequest> getClaimRequests() {
        return CLAIM_REQUESTS;
    }

    public static ObservableList<ItemMatch> getMatchSuggestions() {
        return MATCH_SUGGESTIONS;
    }

    public static ItemReport addItemReport(String type, String itemName, String category, String date,
                                           String location, String reportedBy, String contact,
                                           String description, String imagePath) {
        ItemReport report = ITEM_REPORT_DAO.insert(type, itemName, category, date, location,
                reportedBy, contact, description, imagePath);
        ITEM_REPORTS.add(0, report);
        refreshMatchSuggestions();
        return report;
    }

    public static ClaimRequest addClaimRequest(ItemReport item, String claimantName, String studentNumber,
                                               String contactInfo, String proofDescription) {
        if (item == null || !"Found".equalsIgnoreCase(item.getType())) {
            throw new IllegalArgumentException("Only found items can be claimed.");
        }

        ClaimRequest request = CLAIM_REQUEST_DAO.insert(item, claimantName, studentNumber,
                contactInfo, proofDescription);
        CLAIM_REQUESTS.add(0, request);
        refreshMatchSuggestions();
        return request;
    }

    public static ClaimRequest confirmMatch(ItemMatch match) {
        ClaimRequest existing = findAutoMatchClaim(match);
        if (existing != null) {
            if (!"Approved".equalsIgnoreCase(existing.getStatus())) {
                CLAIM_REQUEST_DAO.updateStatus(existing, "Approved");
                existing.setStatus("Approved");
            }
            match.setStatus("Confirmed");
            refreshMatchSuggestions();
            return existing;
        }

        ItemReport lostItem = match.getLostItem();
        ItemReport foundItem = match.getFoundItem();
        String proof = "Auto-generated from match suggestion between lost report #"
                + lostItem.getId() + " and found report #" + foundItem.getId() + ".";

        ClaimRequest request = CLAIM_REQUEST_DAO.insert(
                foundItem,
                lostItem.getReportedBy(),
                autoMatchStudentNumber(match),
                lostItem.getContact(),
                proof
        );
        CLAIM_REQUEST_DAO.updateStatus(request, "Approved");
        request.setStatus("Approved");
        CLAIM_REQUESTS.add(0, request);
        match.setStatus("Confirmed");
        refreshMatchSuggestions();
        return request;
    }

    public static void deleteItemReport(ItemReport item) {
        ITEM_REPORT_DAO.delete(item);
        ITEM_REPORTS.remove(item);
        CLAIM_REQUESTS.removeIf(claim -> claim.getItem().getId() == item.getId());
        refreshMatchSuggestions();
    }

    public static void updateClaimStatus(ClaimRequest request, String status) {
        CLAIM_REQUEST_DAO.updateStatus(request, status);
        request.setStatus(status);
        refreshMatchSuggestions();
    }

    public static void deleteClaimRequest(ClaimRequest request) {
        CLAIM_REQUEST_DAO.delete(request);
        CLAIM_REQUESTS.remove(request);
        refreshMatchSuggestions();
    }

    public static void refreshAll() {
        try {
            ITEM_REPORTS.setAll(ITEM_REPORT_DAO.findAll());
            CLAIM_REQUESTS.setAll(CLAIM_REQUEST_DAO.findAll());
            refreshMatchSuggestions();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    public static long countItemsByType(String type) {
        return ITEM_REPORTS.stream()
                .filter(item -> item.getType().equalsIgnoreCase(type))
                .count();
    }

    public static long countClaimsByStatus(String status) {
        return CLAIM_REQUESTS.stream()
                .filter(claim -> claim.getStatus().equalsIgnoreCase(status))
                .count();
    }

    public static long countMatches() {
        return MATCH_SUGGESTIONS.size();
    }

    private static void refreshMatchSuggestions() {
        ObservableList<ItemMatch> matches = FXCollections.observableArrayList();
        for (ItemReport lostItem : ITEM_REPORTS) {
            if (!"Lost".equalsIgnoreCase(lostItem.getType())) {
                continue;
            }
            for (ItemReport foundItem : ITEM_REPORTS) {
                if (!"Found".equalsIgnoreCase(foundItem.getType())) {
                    continue;
                }
                if (isIdenticalReport(lostItem, foundItem)) {
                    ItemMatch match = new ItemMatch(
                            matchId(lostItem, foundItem),
                            lostItem,
                            foundItem,
                            "Pending"
                    );
                    if (findAutoMatchClaim(match) != null) {
                        match.setStatus("Confirmed");
                    }
                    matches.add(match);
                }
            }
        }
        MATCH_SUGGESTIONS.setAll(matches);
    }

    private static boolean isIdenticalReport(ItemReport lostItem, ItemReport foundItem) {
        return normalized(lostItem.getItemName()).equals(normalized(foundItem.getItemName()))
                && normalized(lostItem.getCategory()).equals(normalized(foundItem.getCategory()));
    }

    private static String normalized(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    private static int matchId(ItemReport lostItem, ItemReport foundItem) {
        return Math.abs((lostItem.getId() + ":" + foundItem.getId()).hashCode());
    }

    private static ClaimRequest findAutoMatchClaim(ItemMatch match) {
        String autoStudentNumber = autoMatchStudentNumber(match);
        return CLAIM_REQUESTS.stream()
                .filter(claim -> claim.getItem().getId() == match.getFoundItem().getId())
                .filter(claim -> autoStudentNumber.equals(claim.getStudentNumber()))
                .findFirst()
                .orElse(null);
    }

    private static String autoMatchStudentNumber(ItemMatch match) {
        return "MATCH-" + match.getLostItem().getId() + "-" + match.getFoundItem().getId();
    }

    public static void updateItemDetails(ItemReport item, String newName, String newLocation, String newDescription) {
        ITEM_REPORT_DAO.updateDetails(item, newName, newLocation, newDescription);
        refreshAll(); 
    }

    public static void updateClaimDetails(ClaimRequest request, String newContact, String newProof) {
        CLAIM_REQUEST_DAO.updateDetails(request, newContact, newProof);
        refreshAll();
    }
}
