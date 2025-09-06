package org.minilms.minilms.domain;

public interface ProfileSummaryView {
    Long getMemberId();
    String getNickname();
    Long getCoursesCount();
    Double getAvgProgress();
    String getAvatarUrl();
}
