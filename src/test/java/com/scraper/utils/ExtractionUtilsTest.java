package com.scraper.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ExtractionUtilsTest {

	@Autowired
	ExtractionUtils extractionUtils;

	// Github repository URL
	final String baseUrl = "https://github.com/marcosabreu39/Aplicacao_Angularjs_Spring_Boot2-2017---2020";

	@Test
	public void testGetSuffixRepositoryBranches() throws NullPointerException, MalformedURLException, IOException {
		assertFalse(extractionUtils.getSuffixRepositoryBranches(baseUrl).isEmpty(), "Empty list.");
	}

	@Test
	public void testGetJSONRepositoryFileNames() throws NullPointerException {
		assertFalse(extractionUtils.getJSONRepositoryFileNames(baseUrl, baseUrl + "/find" + "/master").isEmpty(),
				"Empty list.");
	}

	@Test
	public void testGetBranchFileLines() throws NullPointerException {
		assertFalse(extractionUtils.getBranchFileLines(baseUrl, "master", ".classpath") == null, "File null.");
	}
}
