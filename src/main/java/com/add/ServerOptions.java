package com.add;

public enum ServerOptions implements SQLTable {
    INFO("info", String.class),
    INVITE_LOGGER_JOIN_MESSAGE("inviteLoggerJoinMessage", String.class),
    INVITE_LOGGER_LEAVE_MESSAGE("inviteLoggerLeaveMessage", String.class),
    BLACKLIST_OUTPUT_MESSAGE("blacklistOutputMessage", String.class),
    INVITE_LOGGER_ID("inviteLoggerId", Long.class),
    INVITE_LOGGER_FAKE_TIME("inviteLoggerFakeTime", Integer.class),
    AUDIT_ID("auditId", Long.class),
    AUTOMOD_ID("automodId", Long.class),
    AUTOMOD_AUTO_CORRECT("automodAutoCorrect", Boolean.class),
    TICKET_ID("ticketId", Long.class),
    MEMBER_COUNT_ID("memberCountId", Long.class),
    GOAL_ID("goalId", Long.class),
    MUTE_ROLE_ID("muteRoleId", Long.class),
    MUTE_MANAGED_BY_BOT("muteManagedByBot", Boolean.class),
    WARN_PUNISHMENT("warnPunishment", String.class),
    WARN_PUNISHMENT_TRIGGER("warnPunishmentTrigger", Integer.class),
    TICKET_TRANSCRIPT_ID("ticketTranscriptId", Long.class),
    SUGGESTION_APPROVED_ID("suggestionApprovedId", Long.class),
    SUGGESTION_CHANNEL_ID("suggestionId", Long.class),
    SUGGESTION_AUTO_SUGGEST("suggestionAutoSuggest", Boolean.class),
    LEVEL_INCREMENT("levelIncrement", String.class),
    BOT_ERROR_CHANNEL("botErrorChannel", Long.class);

    private final String TABLE = "serverInfo";
    private String name;
    private Class<?> type;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }

    private ServerOptions(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }
}