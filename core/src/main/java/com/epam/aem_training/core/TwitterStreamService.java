

package com.epam.aem_training.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

@Component(metatype = true, label = "Twitter stream service",
description = "Twitter stream service")
@Service(TwitterStreamService.class)
public class TwitterStreamService {

	private final int QUEUE_CAPACITY = 10000;

	@Property(label = "Filter query", description = "Filter query separated by commas")
	public static final String QUERY = "query";
	private String query;

	private String[] filters;
	
	private ArrayList<String> arrayList = new ArrayList<String>();
	
	private Thread task = new Thread(new Runnable() {
		@Override
		public void run() {
			try{
				StatusListener listener = new StatusListener(){
					public void onStatus(Status status) {
						//statuses.push(status);
						arrayList.add(status.getText());
					}
					public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
					public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
	
					public void onScrubGeo(long l, long l1) {
					}
	
					public void onStallWarning(StallWarning stallWarning) {
					}
	
					public void onException(Exception ex) {
						arrayList.add(ex.getMessage().toString());
					}
				};
	
				ConfigurationBuilder cb = new ConfigurationBuilder();
				cb.setDebugEnabled(true)
                .setOAuthConsumerKey("pJV4p6FS2yowhSZkkTwmW6gIL")
                .setOAuthConsumerSecret("RsxKpzRkrtB1K0SzblfIl8K6XnV3ZEhN55nxsysJYyDfgjPvHI")
                .setOAuthAccessToken("73952106-06WR8OVPMW5m8FN5dW0b9lI7yoJvNaWSqo1iVWhiQ")
                .setOAuthAccessTokenSecret("OJmx5AwxmDboMMdid9pGIoZDbSjZeeDF0mmXiIb4AohHJ");
				
//				cb.setDebugEnabled(true)
//					.setOAuthConsumerKey("SRxJLobhkJPG9NubkeK06Q3Kf")
//					.setOAuthConsumerSecret("7dJ1BxX8ZL6P6GkYSw7RqWz1jusVcKtsHSOkfUKPQ9oLc1GDDf")
//					.setOAuthAccessToken("73952106-B2sWzBrF85UIchjIg2Vluj2mMek9vyVUpYi3nsPay")
//					.setOAuthAccessTokenSecret("C4bGhJEBeFKVXGR0uXbxUqWDi8yrG602jy0JIXp1JV2w8");
//	
				TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
				twitterStream.addListener(listener);
				twitterStream.sample();
				while(true){
					//arrayList.add("olololo");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch(Exception e){
				arrayList.add(e.getMessage());
			}
		}
	});

	private class LimitedBlockingQueue extends LinkedList<Status> {

		final private int capacity;

		public LimitedBlockingQueue(int capacity) {
			super();
			this.capacity = capacity;
		}

		@Override
		public synchronized void push(Status e) {
			if (capacity == size()) {
				removeLast();
			}
			super.push(e);
		}

		public synchronized List<Status> getStatusesSinceDate(Date start) {
			List<Status> statuses = new LinkedList<Status>();
			if (isEmpty())
				return statuses;
			Iterator<Status> it = iterator();
			while(it.hasNext()) {
				Status status = it.next();
				if (status.getCreatedAt().after(start))
					statuses.add(status);
				else
					break;
			}
			return statuses;
		}



	}
	private LimitedBlockingQueue statuses = new LimitedBlockingQueue(QUEUE_CAPACITY);

	@Activate
	protected void activate(final Map<String, Object> config) {
		configure(config);
		arrayList.add("test");
		task.start();
	}
	
	public Thread getTask() {
		return task;
	}



	private void configure(final Map<String, Object> config) {
		query = PropertiesUtil.toString(config.get(QUERY), "java,GameofThrones");
		filters = query.split(",");
	}

	public String[] getFilters() {
		return filters;
	}

	public List<Status> getStatusesSinceDate(Date start) {
		return statuses.getStatusesSinceDate(start);
	}
	
	public List<String> getStatusesSinceDate() {
		return arrayList;
	}
}

