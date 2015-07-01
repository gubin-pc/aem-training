
package com.epam.aem_training.core.listeners;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;


@Component(
        label = "JCR Event Listener",
        description = "JCR Event Listener.",
        immediate = true
)
@Service
public class SampleJcrEventListener implements EventListener {
    private static final Logger log = LoggerFactory.getLogger(SampleJcrEventListener.class);
    private final int events = Event.PROPERTY_CHANGED;
    private final String absPath = "/content/aemtraining";
    private final boolean isDeep = true;
    private final boolean noLocal = false;
    private final String[] uuids = null;
    private final String[] nodeTypes = new String[]{"nt:unstructured", "nt:folder"};

    private Session observationSession = null;
    
    @Reference
    private SlingRepository repository;

    @Reference
    private EventAdmin eventAdmin;

    @Override
    public void onEvent(final EventIterator events) {
        while (events.hasNext()) {
            Event event = events.nextEvent();
			if (Event.PROPERTY_CHANGED == event.getType()) {
				Calendar cal = Calendar.getInstance();
				cal.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				log.info("A property was change at : " +cal.getTime());
			}
        }
    }

    @Activate
    public void activate(final Map<String, String> config) throws RepositoryException {
        observationSession = repository.loginAdministrative(null);
        final ObservationManager observationManager  = observationSession.getWorkspace().getObservationManager();
        observationManager.addEventListener(this, events, absPath, isDeep, uuids, nodeTypes, noLocal);
    }

    @Deactivate
    public void deactivate(final Map<String, String> config) throws RepositoryException {
        try {
            final ObservationManager observationManager = observationSession.getWorkspace().getObservationManager();
            if (observationManager != null) {
                observationManager.removeEventListener(this);
            }
        } finally {
            if (observationSession != null) {
                observationSession.logout();
            }
        }
    }
}