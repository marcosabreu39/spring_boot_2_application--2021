package com.scraper.service;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ScraperServiceImplTest {

	@Autowired
	private ScraperService scraperService;
	
	final String urlRepository = "https://github.com/marcosabreu39/Aplicacao_Angularjs_Spring_Boot2-2017---2020";
	
	@Test
	public void testRepositoryStatistics() throws Exception {		
		assertFalse(scraperService.getRepositoryStatistics(urlRepository).getFiles().isEmpty(), "Empty List."); 
	}
}
