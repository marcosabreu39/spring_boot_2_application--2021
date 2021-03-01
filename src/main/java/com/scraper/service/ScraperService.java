package com.scraper.service;

import com.scraper.model.GithubRepository;

public interface ScraperService {

	GithubRepository getRepositoryStatistics(String urlRepository);
}
