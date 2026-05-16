package com.fashionstore.service;

import com.fashionstore.model.User;
import java.util.Map;

/**
 * Enterprise Service Facade for Customer Support and Moderation
 */
public interface SupportService {
    Map<String, Object> getDashboardData(User user);
    Map<String, Object> getTickets(User user, int page, int limit, String status, String priority, String category, String assignedTo);
    Map<String, Object> getTicketDetails(User user, int ticketId);
    Map<String, Object> getUsersNeedingSupport(User user, int page, int limit, String issueType);
    Map<String, Object> getFraudCases(User user, int page, int limit, String status, String riskLevel);
    Map<String, Object> getModerationQueue(User user, int page, int limit, String action);
    Map<String, Object> getEscalatedCases(User user, int page, int limit, String escalationLevel);
    Map<String, Object> getAnalytics(User user, String period, String metric);
    Map<String, Object> getKnowledgeBase(User user, int page, int limit, String category, String search);
    Map<String, Object> getAgents(User user);
    Map<String, Object> getSLAMetrics(User user, String period);
    
    Map<String, Object> createTicket(User user, Map<String, Object> data);
    Map<String, Object> updateTicket(User user, int ticketId, Map<String, Object> data);
    Map<String, Object> assignTicket(User user, int ticketId, int assignedTo, String notes);
    Map<String, Object> closeTicket(User user, int ticketId, String resolution, String customerSatisfaction, String notes);
    Map<String, Object> escalateTicket(User user, int ticketId, String escalationLevel, String reason, String notes);
    Map<String, Object> moderateUser(User user, int targetUserId, String action, String reason, String duration, String notes);
    Map<String, Object> investigateFraud(User user, int caseId, String action, String notes);
    Map<String, Object> resolveFraudCase(User user, int caseId, String resolution, String action, String notes);
    Map<String, Object> addNote(User user, int ticketId, String note, String noteType, boolean isInternal);
    Map<String, Object> createCannedResponse(User user, Map<String, Object> data);
    Map<String, Object> updateSLASettings(User user, Map<String, Object> data);
}
