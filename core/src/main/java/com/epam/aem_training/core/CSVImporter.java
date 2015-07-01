package com.epam.aem_training.core;

import java.util.Map;

import javax.jcr.RepositoryFactory;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.osgi.PropertiesUtil;

@Component(metatype = true, label = "CSV to JSR Importer")
public class CSVImporter {
	@Reference
	private RepositoryFactory repositoryFactory;
	
    	
}
