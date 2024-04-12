package co.gatedaccess.web.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document("join_community_request")
public class JoinCommunityRequest {
    @Id
    String id;
    @DBRef
    Member member;
    @DBRef
    Community community;
    @Field("created_at")
    Date createdAt;
    @Field("accepted_at")
    Date acceptedAt;
    @Field("rejected_at")
    Date rejectAt;

    public String getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Community getCommunity() {
        return community;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getAcceptedAt() {
        return acceptedAt;
    }

    public Date getRejectAt() {
        return rejectAt;
    }

    public void setAcceptedAt(Date acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public void setRejectAt(Date rejectAt) {
        this.rejectAt = rejectAt;
    }

    public static final class Builder {
        private String id;
        private Member member;
        private Community community;
        private Date createdAt;
        private Date acceptedAt;
        private Date rejectAt;

        public Builder() {
        }

        public static Builder aJoinCommunityRequest() {
            return new Builder();
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withMember(Member member) {
            this.member = member;
            return this;
        }

        public Builder withCommunity(Community community) {
            this.community = community;
            return this;
        }

        public Builder withCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withAcceptedAt(Date acceptedAt) {
            this.acceptedAt = acceptedAt;
            return this;
        }

        public Builder withRejectAt(Date rejectAt) {
            this.rejectAt = rejectAt;
            return this;
        }

        public JoinCommunityRequest build() {
            JoinCommunityRequest joinCommunityRequest = new JoinCommunityRequest();
            joinCommunityRequest.community = this.community;
            joinCommunityRequest.rejectAt = this.rejectAt;
            joinCommunityRequest.id = this.id;
            joinCommunityRequest.createdAt = this.createdAt;
            joinCommunityRequest.acceptedAt = this.acceptedAt;
            joinCommunityRequest.member = this.member;
            return joinCommunityRequest;
        }
    }
}
