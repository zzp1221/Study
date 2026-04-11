package com.project.test.entity;

public enum Authority {
    USER("user", 0),
    TEACHER("teacher", 1),
    ADMIN("admin", 2);

    private final String value;
    private final int level;

    Authority(String value, int level) {
        this.value = value;
        this.level = level;
    }

    public String getValue() {
        return value;
    }

    public int getLevel() {
        return level;
    }

    public static Authority fromValue(String value) {
        for (Authority authority : Authority.values()) {
            if (authority.value.equals(value)) {
                return authority;
            }
        }
        throw new IllegalArgumentException("Unknown authority: " + value);
    }

    public boolean hasPermission(Authority targetAuthority) {
        return this.level >= targetAuthority.level;
    }

    public boolean hasPermission(String targetAuthorityValue) {
        Authority targetAuthority = fromValue(targetAuthorityValue.toLowerCase());
        return this.level >= targetAuthority.level;
    }
}
