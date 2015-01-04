package com.autentia.tutoriales;

import javax.inject.Inject;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class TwitterController {

	private final ConnectionRepository connectionRepository;
	private final Twitter twitter;

	@Inject
	public TwitterController(Twitter twitter, ConnectionRepository connectionRepository) {
		this.twitter = twitter;
		this.connectionRepository = connectionRepository;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getTimeline(Model model) {
		if (!isTwitterLogged()) {
			return "redirect:/connect/twitter";
		}

		retrieveTweets(model);

		return "home";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String createTweet(Model model, @RequestBody String text) {
		twitter.timelineOperations().updateStatus(text);
		
		retrieveTweets(model);
		
		return "home";
	}

	private void retrieveTweets(Model model) {
		model.addAttribute("twitterProfile", twitter.userOperations().getUserProfile().getName());
		model.addAttribute("timeline", twitter.timelineOperations().getHomeTimeline());
		model.addAttribute("myTweets", twitter.timelineOperations().getUserTimeline());
		model.addAttribute("favorites", twitter.timelineOperations().getFavorites());
	}
	
	private boolean isTwitterLogged() {
		return connectionRepository.findPrimaryConnection(Twitter.class) != null;
	}

}