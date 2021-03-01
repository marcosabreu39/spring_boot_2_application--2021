package com.scraper.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scraper.model.File;
import com.scraper.model.GithubRepository;
import com.scraper.utils.ExtractionUtils;

@Service
public class ScraperServiceImpl implements ScraperService {

	//@Autowired
	//private GithubRepository githubRepository;
	
	@Autowired
	public ExtractionUtils extractionUtils;

	private static final Logger LOGGER = LoggerFactory.getLogger(ScraperServiceImpl.class);

	@Override
	public GithubRepository getRepositoryStatistics(String urlRepository) {
		GithubRepository githubRepository = new GithubRepository();
		try {
			githubRepository.setUrlProfileGit(urlRepository.split("/")[2] + "/" + urlRepository.split("/")[3]);
			githubRepository.setUrl(urlRepository);
			extractionUtils.getSuffixRepositoryBranches(urlRepository).forEach(suffixRepository -> {
				extractionUtils.getJSONRepositoryFileNames(urlRepository, suffixRepository)
						.forEach(suffixFileRepository -> {
							File f = extractionUtils.getBranchFileLines(urlRepository, suffixRepository, suffixFileRepository);
							if(f.getBytes() != null && f.getLines() != null) {
								githubRepository.getFiles().add(f);
							}
						});
			});
		} catch (Exception e) {
			LOGGER.error("Error extracting repository info.", e);
			
			return githubRepository;
		}

		return githubRepository;
	}
}
