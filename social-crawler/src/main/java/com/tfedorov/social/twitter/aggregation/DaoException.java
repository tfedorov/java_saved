/**
 * 
 */
package com.tfedorov.social.twitter.aggregation;

import org.springframework.dao.DataAccessException;

/**
 * @author tfedorov
 * 
 */
public class DaoException extends DataAccessException {

	public DaoException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
