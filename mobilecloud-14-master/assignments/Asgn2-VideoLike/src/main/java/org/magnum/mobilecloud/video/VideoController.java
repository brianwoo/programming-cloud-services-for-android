/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.repository.User;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VideoController {
	

	@Autowired
	private VideoRepository videos;
	
	
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody List<Video> getVideos() 
	{
				
		List<Video> videoList = new ArrayList<Video>();
		Iterator<Video>  videoIterator = videos.findAll().iterator();
		
		while (videoIterator.hasNext())
		{
			videoList.add(videoIterator.next());
		}
		
		return videoList;
	}
	
	
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Video saveVideo(@RequestBody Video v) 
	{
				
		System.out.println("#### video saved=" + v.getName());
		
		Video savedVideo = videos.save(v);
		return savedVideo;
	}
	
	
	
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH+"/{id}", method=RequestMethod.GET)
	public @ResponseBody Video getVideo(@PathVariable("id") Long id, HttpServletResponse response) 
	{
				
		Video v = videos.findOne(id);
		if (null == v)
		{
			response.setStatus(404);
			return null;
		}
		
		response.setStatus(200);
		return v;
	}
	
	
	
	
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH+"/{id}/like", method=RequestMethod.POST)
	public void setLike(@PathVariable("id") Long id, HttpServletResponse response) 
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String name = auth.getName(); //get logged in username
				
		Video v = videos.findOne(id);
		
		System.out.println("########### V = " + v);
		
		if (null == v)
		{
			response.setStatus(404);
			return;
		}
		else if (v.isUserAleadySetLike(name))
		{
			response.setStatus(400);
			return;
		}
		
		// add the user who likes this video.
		v.addUserToLikes(name);
		videos.save(v);
		response.setStatus(200);
	}
	
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH+"/{id}/unlike", method=RequestMethod.POST)
	public void setUnlike(@PathVariable("id") Long id, HttpServletResponse response) 
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String name = auth.getName(); //get logged in username
				
		Video v = videos.findOne(id);
		if (null == v)
		{
			response.setStatus(404);
			return;
		}
		else if (!v.isUserAleadySetLike(name))
		{
			response.setStatus(400);
			return;
		}
		
		// add the user who likes this video.
		v.removeUserFromLikes(name);
		videos.save(v);
		response.setStatus(200);
	}
	
	
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH+"/{id}/likedby", method=RequestMethod.GET)
	public @ResponseBody List<String> getLikedBy(@PathVariable("id") Long id, HttpServletResponse response) 
	{
				
		Video v = videos.findOne(id);
		if (null == v)
		{
			response.setStatus(404);
			return null;
		}
		
		List<String> usernameList = new ArrayList<String>();
		
		Set<User> userLikesSet = v.getUserLikesSet();
		for (User user : userLikesSet)
		{
			usernameList.add(user.getUsername());
		}
		
		response.setStatus(200);
		return usernameList;
	}
	
}
