package com.scraper.controller;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.scraper.model.File;
import com.scraper.model.GithubRepository;
import com.scraper.service.ScraperService;

@RestController
public class ScraperController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScraperController.class);

	@Autowired(required=false)
	private ScraperService scraperService;
	
	@Autowired
	HttpSession session;

	@PostMapping(value = "/api/repo-info")
	public ResponseEntity<Object> getRepositoryInfo(@RequestBody String urlRepository) {
		ResponseEntity<Object> response = null;
		final Map<String, Map<String, String>> statistics = new LinkedHashMap<String, Map<String, String>>();
		try {
			GithubRepository githubRepository = null;
			GithubRepository sessionGitHubRepository = (GithubRepository) session.getAttribute("GithubRepository");
			/*
			 * Checks if same URL was sent, if true, replace by already extracted GithubRepository POJO extracted from the session.
			 */
			if (sessionGitHubRepository == null || !sessionGitHubRepository.getUrlProfileGit().equals(urlRepository)) {
				String[] parts = urlRepository.split("\"");
				githubRepository = scraperService.getRepositoryStatistics(parts[3]);
			} else {
				githubRepository = sessionGitHubRepository;
			}
			/*
			 * Handles values extracted from Github using web scraping techniques to respond to the requisition.
			 */
			if (!githubRepository.getFiles().isEmpty()) {
				List<File> files = githubRepository.getFiles();
				Collections.sort(files);
				double totalBytes = 0.0;
				double totalLines = 0.0;
				for (int i = 0; i < files.size(); i++) {
					File f = files.get(i);
					if (i < files.size()) {
						if (f.getExtension().trim().equals(files.get(i + 1).getExtension().trim())) {
							totalBytes += f.getBytes();
							totalLines += f.getLines();
						} else {
							final Map<String, String> values = new LinkedHashMap<>();
							values.put(String.valueOf("Bytes: " + totalBytes), String.valueOf("Lines: " + totalLines));
							statistics.put(f.getExtension(), values);
							totalBytes = 0.0;
							totalLines = 0.0;
						}
					}
				}
				
				/*
				 * Put already extracted GithubRepository POJO into session to reuse it if possible.
				 */
				session.setAttribute("GithubRepository", githubRepository);

				response = new ResponseEntity<>(statistics, HttpStatus.OK);

			} else {
				response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

		} catch (final Exception e) {
			LOGGER.error("Error processing the requisition.", e);
			response = new ResponseEntity<>(statistics, HttpStatus.PARTIAL_CONTENT);
		}
		return response;
	}
}
