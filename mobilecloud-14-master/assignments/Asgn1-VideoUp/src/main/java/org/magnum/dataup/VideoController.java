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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.model.VideoStatus.VideoState;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author bwoo
 *
 */

@Controller
public class VideoController
{
	private static final int NOT_FOUND = 404;
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
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.GET)
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
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video video)
	{
		checkAndSetIdAndUrl(video);
		videos.put(video.getId(), video);
		return video;
	}

	
	/**
	 * Helper method to check and set/create id for the video. 
	 * 
	 * @param entity
	 */
	private void checkAndSetIdAndUrl(Video entity) 
	{
		
		if(entity.getId() == 0)
		{
			long id = currentId.incrementAndGet();
			
			entity.setId(id);
			
			String dataUrl = getDataUrl(id);
			entity.setDataUrl(dataUrl);
		}
		

		
	}
	
	
    private String getDataUrl(long videoId){
        String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
        return url;
    }
    

 	private String getUrlBaseForLocalServer() 
 	{
	   HttpServletRequest request = 
	       ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	
	   String base = 
	      "http://"+request.getServerName() 
	      + ((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
	   
	   return base;
	}
	
	
	/**
	 * 
	 * @param id
	 * @param videoData
	 * @return
	 */
	
	@RequestMapping(value=VideoSvcApi.VIDEO_DATA_PATH, method=RequestMethod.POST)
	public @ResponseBody VideoStatus setVideoData
										(
											@PathVariable("id") long id, 
											@RequestParam("data") MultipartFile videoData,
											HttpServletResponse response
										
										) throws IOException
	{
		
		Video v = videos.get(id);
		if (null == v)
		{
			response.setStatus(NOT_FOUND);
			return null;
		}
		
		videoDataMgr.saveVideoData(v, videoData.getInputStream());
		VideoStatus videoStatus = new VideoStatus(VideoState.READY);
		
		return videoStatus;
	}
	

	
	@RequestMapping(value = VideoSvcApi.VIDEO_DATA_PATH, method = RequestMethod.GET)
	public void getVideo(
							@PathVariable("id") long id, 
							HttpServletResponse response
							
						) throws IOException
	{

		Video v = videos.get(id);
		if (null == v)
		{
			response.setStatus(NOT_FOUND);
			return;
		}
		
		videoDataMgr.copyVideoData(v, response.getOutputStream());
		
	}

}
