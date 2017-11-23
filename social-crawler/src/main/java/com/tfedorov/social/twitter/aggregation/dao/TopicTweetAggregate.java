package com.tfedorov.social.twitter.aggregation.dao;

import java.math.BigInteger;

import com.tfedorov.social.utils.JsonDateSerializer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.base.BaseDateTime;

public class TopicTweetAggregate {

	@JsonProperty(value = "id")
	private BigInteger topicId;

	@JsonIgnore
	private BigInteger tweetId;

	@JsonProperty(value = "text")
	private String text;

	@JsonProperty(value = "from_user_id")
	private BigInteger fromUserId;

	@JsonProperty(value = "from_user")
	private String fromUser;

	@JsonIgnore
	private String profileImageUrl;

	@JsonIgnore
	private String profileImageUrlHttps;

	@JsonProperty(value = "createdAt")
	private BaseDateTime ceatedAt;

	@JsonProperty(value = "retweets")
	private long retweets;

	@JsonIgnore
	private long recentRetweets;

	@JsonIgnore
	private long followersSum;

	@JsonProperty(value = "reach")
	private long estimatedReach;

	@JsonIgnore
	private AGGREGATE_TYPE type;


	public TopicTweetAggregate(String text, BaseDateTime ceatedAt, BigInteger topicId, BigInteger tweetId, BigInteger fromUserId, String fromUser, long retweets, long estimatedReach,
			AGGREGATE_TYPE type) {
		this.topicId = topicId;
		this.tweetId = tweetId;
		this.text = text;
		this.fromUserId = fromUserId;
		this.fromUser = fromUser;
		this.ceatedAt = ceatedAt;
		this.retweets = retweets;
		this.estimatedReach = estimatedReach;
		this.type = type;
	}

    public TopicTweetAggregate(String text, BaseDateTime ceatedAt, BigInteger topicId, BigInteger tweetId, BigInteger fromUserId, long recentRetweets, long retweets, long estimatedReach,
         long followersSum) {
        this.topicId = topicId;
        this.tweetId = tweetId;
        this.text = text;
        this.fromUserId = fromUserId;
        this.ceatedAt = ceatedAt;
        this.recentRetweets = recentRetweets;
        this.retweets = retweets;
        this.estimatedReach = estimatedReach;
        this.followersSum = followersSum;
    }

	public TopicTweetAggregate(BigInteger topicId, BigInteger tweetId, String text, BigInteger fromUserId, String fromUser, String profileImageUrl,
			String profileImageUrlHttps, BaseDateTime ceatedAt, long retweets, long recentRetweets, long followersSum, long estimatedReach, AGGREGATE_TYPE type) {
		super();
		this.topicId = topicId;
		this.tweetId = tweetId;
		this.text = text;
		this.fromUserId = fromUserId;
		this.fromUser = fromUser;
		this.profileImageUrl = profileImageUrl;
		this.profileImageUrlHttps = profileImageUrlHttps;
		this.ceatedAt = ceatedAt;
		this.retweets = retweets;
		this.recentRetweets = recentRetweets;
		this.followersSum = followersSum;
		this.estimatedReach = estimatedReach;
		this.type = type;
	}

    public TopicTweetAggregate(BigInteger topicId, BigInteger tweetId, long retweets, long followersSum, AGGREGATE_TYPE type) {
        super();
        this.topicId = topicId;
        this.tweetId = tweetId;
        this.retweets = retweets;
        this.followersSum = followersSum;
        this.type = type;
    }

    public TopicTweetAggregate(BigInteger topicId, BigInteger tweetId, String text, BigInteger fromUserId, String fromUser, String profileImageUrl,
                               String profileImageUrlHttps, BaseDateTime ceatedAt, AGGREGATE_TYPE type) {
        super();
        this.topicId = topicId;
        this.tweetId = tweetId;
        this.text = text;
        this.fromUserId = fromUserId;
        this.fromUser = fromUser;
        this.profileImageUrl = profileImageUrl;
        this.profileImageUrlHttps = profileImageUrlHttps;
        this.ceatedAt = ceatedAt;
        this.type = type;
    }

    public TopicTweetAggregate(BigInteger topicId, BigInteger tweetId, String text, BigInteger fromUserId, String fromUser, String profileImageUrl,
                               String profileImageUrlHttps, BaseDateTime ceatedAt) {
        super();
        this.topicId = topicId;
        this.tweetId = tweetId;
        this.text = text;
        this.fromUserId = fromUserId;
        this.fromUser = fromUser;
        this.profileImageUrl = profileImageUrl;
        this.profileImageUrlHttps = profileImageUrlHttps;
        this.ceatedAt = ceatedAt;
    }
    
	public BigInteger getTopicId() {
		return topicId;
	}

	public void setTopicId(BigInteger topicId) {
		this.topicId = topicId;
	}

	@JsonProperty(value = "tweet_id")
	public String getTweetId() {
		return (tweetId == null) ? null : tweetId.toString();
	}

	public void setTweetId(BigInteger tweetId) {
		this.tweetId = tweetId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public BigInteger getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(BigInteger fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public String getProfileImageUrlHttps() {
		return profileImageUrlHttps;
	}

	public void setProfileImageUrlHttps(String profileImageUrlHttps) {
		this.profileImageUrlHttps = profileImageUrlHttps;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public BaseDateTime getCeatedAt() {
		return ceatedAt;
	}

	public void setCeatedAt(BaseDateTime ceatedAt) {
		this.ceatedAt = ceatedAt;
	}

	public long getRetweets() {
		return retweets;
	}

	public void setRetweets(long retweets) {
		this.retweets = retweets;
	}

	public long getEstimatedReach() {
		return estimatedReach;
	}

	public void setEstimatedReach(long estimatedReach) {
		this.estimatedReach = estimatedReach;
	}

	public long getRecentRetweets() {
		return recentRetweets;
	}

	public void setRecentRetweets(long recentRetweets) {
		this.recentRetweets = recentRetweets;
	}

	public long getFollowersSum() {
		return followersSum;
	}

	public void setFollowersSum(long followersSum) {
		this.followersSum = followersSum;
	}

	public AGGREGATE_TYPE getType() {
		return type;
	}

	public void setType(AGGREGATE_TYPE type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return new StringBuilder("TopicTweetAggregate [topicId=").append(topicId).append(", tweetId=").append(tweetId).append(", text=").append(text)
				.append(", fromUserId=").append(fromUserId).append(", fromUser=").append(fromUser).append(", profileImageUrl=").append(profileImageUrl)
				.append(", profileImageUrlHttps=").append(profileImageUrlHttps).append(", ceatedAt=").append(ceatedAt).append(", retweets=").append(retweets)
				.append(", recentRetweets=").append(recentRetweets).append(", followersSum=").append(followersSum).append(", estimatedReach=")
				.append(estimatedReach).append(", type=").append(type).append("]").toString();
	}

	public static enum AGGREGATE_TYPE {
		popular_tweets, intention_tweets, keyword_intention_tweets, latest_tweets
	}

}
