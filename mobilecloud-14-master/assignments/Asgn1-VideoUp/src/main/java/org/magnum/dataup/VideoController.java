/**
 * 
 */
package org.magnum.dataup;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletResponse;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author bwoo
 *
 */

@Controller
public class VideoController
{
	private Map<Long,Video> videos = new HashMap<Long, Video>();
	private static final AtomicLong currentId = new AtomicLong(0L);
	
	private VideoFileManager videoDataMgr;
	

	public VideoController()
	{
		try
		{
			videoDataMgr = VideoFileManager.get();
		}
		catch (IOException e)
		{
			System.err.println("Unable to initialize VideoFileManager!");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Get a list of videos in the video collection.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/video", method = RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList()
	{
		return videos.values();
	}


	/**
	 * Add Video to collection.  If id=0, create a new id for this video. 
	 * 
	 * @param video
	 * @return
	 */
	@RequestMapping(value = "/video", method = RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video video)
	{
		checkAndSetId(video);
		videos.put(video.getId(), video);
		return video;
	}

	
	/**
	 * Helper method to check and set/create id for the video. 
	 * 
	 * @param entity
	 */
	private void checkAndSetId(Video entity) 
	{
		if(entity.getId() == 0)
		{
			entity.setId(currentId.incrementAndGet());
		}
	}
	
	
	/**
	 * 
	 * @param id
	 * @param videoData
	 * @return
	 */
	@RequestMapping(value = "/video/{id}/data", method = RequestMethod.POST)
	public @ResponseBody VideoStatus setVideoData
										(
											@PathVariable("id") long id, 
											@RequestBody MultipartFile videoData
										)
	{
		// TODO Auto-generated method stub
		return null;
	}

	

	public void getVideo(long id, HttpServletResponse response)
	{
		
	}

}
