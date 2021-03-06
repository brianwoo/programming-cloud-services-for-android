/**
 * 
 */
package org.magnum.mobilecloud.video.repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.common.base.Objects;

/**
 * @author bwoo
 *
 */
@Entity
public class User
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String username;
	
	public User() { }
	
	
	public User(String username)
	{
		this.username = username;
	}
	

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	
	
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(username);
	}
	
	

	
	@Override
	public boolean equals(Object obj) 
	{
		
		if (obj instanceof User) 
		{
			User other = (User) obj;
			
			// Google Guava provides great utilities for equals too!
			return Objects.equal(username, other.getUsername());
		} 
		else 
		{
			return false;
		}
	}
	
	
}
