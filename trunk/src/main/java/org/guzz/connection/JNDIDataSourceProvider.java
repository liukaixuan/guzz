package org.guzz.connection;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.guzz.exception.InvalidConfigurationException;

public class JNDIDataSourceProvider implements DataSourceProvider {
	DataSource dataSource;
	
	public void configure(Properties props, int maxLoad) {
		Hashtable env = new Hashtable();
		String contextFactory = props.getProperty("contextFactory");
		// select a service provider factory
		if (contextFactory != null) {
			env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
		}
		
		// create the initial context
		try {
			String jndiName = props.getProperty("jndiName");
			Context contxt = new InitialContext(env);
			dataSource = (DataSource)contxt.lookup(jndiName);
		} catch (NamingException e) {
			throw new InvalidConfigurationException("unable to find dataSource for:" + props, e) ;
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void shutdown() {
		if (dataSource != null) {
		}
	}
}
