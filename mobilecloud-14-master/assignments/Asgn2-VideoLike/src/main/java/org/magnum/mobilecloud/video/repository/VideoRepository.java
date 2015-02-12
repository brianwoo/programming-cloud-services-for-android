/**
 * 
 */
package org.magnum.mobilecloud.video.repository;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author bwoo
 *
 */
@RepositoryRestResource(path = VideoSvcApi.VIDEO_SVC_PATH)
public interface VideoRepository extends CrudRepository<Video, Long>
{

}
