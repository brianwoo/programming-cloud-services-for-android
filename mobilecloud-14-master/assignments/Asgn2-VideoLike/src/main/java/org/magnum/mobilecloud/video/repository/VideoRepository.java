/**
 * 
 */
package org.magnum.mobilecloud.video.repository;

import java.util.Collection;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author bwoo
 *
 */
@RepositoryRestResource(path = VideoSvcApi.VIDEO_SVC_PATH)
public interface VideoRepository extends CrudRepository<Video, Long>
{

	// Find all videos with a matching title (e.g., Video.name)
	public Collection<Video> findByName(
			// The @Param annotation tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "title" variable used to
			// search for Videos
			@Param(VideoSvcApi.TITLE_PARAMETER) String title);
	
	
	// Find all videos that are shorter than a specified duration
	public Collection<Video> findByDurationLessThan(
			// The @Param annotation tells tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "duration" variable used to
			// search for Videos
			@Param(VideoSvcApi.DURATION_PARAMETER) long maxduration);
	
}
