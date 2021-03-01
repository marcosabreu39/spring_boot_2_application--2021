package com.scraper.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.scraper.model.File;

@Component
public class ExtractionUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExtractionUtils.class);

	// Suffix to specific profile's branches
	private static final String SUFFIX_URL_ALL_BRANCHES = "/branches/all";

	// Suffix to specific URL repository's API file's names
	private static final String SUFFIX_REPOSITORY_FILE_NAMES = "/tree-list/1306030cbbce7b8eb49490f9ffec5bed0fc44fe7";

	// Appends repository name and branch suffix
	private static final String URL_BRANCH_APPENDER = "/find/";

	// Appends a repository URL branch name with a file from the same branch
	private static final String URL_FILE_APPENDER = "/blob/";

	/*
	 * Obtains all repository branches names
	 */
	public List<String> getSuffixRepositoryBranches(final String urlRepository)
			throws MalformedURLException, IOException {
		List<String> suffixBranches = new ArrayList<>();
		final URL url;
		String inputLine = null;
		try {
			url = new URL(urlRepository.concat(SUFFIX_URL_ALL_BRANCHES));
			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((inputLine = br.readLine()) != null) {
				if (inputLine != null && !inputLine.equals("") && !inputLine.equals(" ") && !inputLine.equals("null")) {
					String suffixBranch = getSuffixBranch(inputLine);
					if (null != suffixBranch) {
						suffixBranches.add(suffixBranch);
					}
				}
			}
			br.close();
		} catch (IOException e) {
			LOGGER.error("Error extracting the suffix repository branches URLs.");
			
			return suffixBranches;
		}
		return suffixBranches;
	}

	/*
	 * Obtains the branch suffix to append into profile URL
	 */
	public String getSuffixBranch(String line) {
		final String urlLineIdentifier = "branch-filter-item branch";
		if (line.indexOf(urlLineIdentifier) != -1) {
			return line.split("\"")[1];
		} else {
			return null;
		}
	}

	/*
	 * Obtains all file names of a branch from a Github API to handle them
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> getJSONRepositoryFileNames(final String baseUrl, final String suffixRepository) {
		List<String> repositoryFilesNames = null;
		URI uri = URI.create(baseUrl.concat(SUFFIX_REPOSITORY_FILE_NAMES));
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("referer", baseUrl.concat(URL_BRANCH_APPENDER.concat(suffixRepository)));
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		HttpEntity request = new HttpEntity(headers);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
		repositoryFilesNames = Arrays.asList(getRepositoryArrayFileNames(response.getBody()).split(","));

		return repositoryFilesNames;
	}

	/*
	 * Obtains all names of files of a repository branch from a json string
	 */
	public String getRepositoryArrayFileNames(String jsonArray) {
		String beginJsonArray = "{\"paths\":[";
		return jsonArray.substring(jsonArray.indexOf(beginJsonArray) + beginJsonArray.length(), jsonArray.length() - 2);
	}

	/*
	 * Extracts files names from Github API
	 */
	public File getBranchFileLines(final String urlRepository, final String suffixBranch, final String suffixRepositoryFile) {
		File file = new File();
		final URL url;
		String inputLine = null;
		try {
			url = new URL(urlRepository.concat(URL_FILE_APPENDER.concat(suffixBranch).concat("/").concat(suffixRepositoryFile.replace("\"", ""))));
			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			file.setBranchName(suffixBranch);
			String [] filesDot = suffixRepositoryFile.split("\\.");
			/*
			 * Separates the files by extension
			 */
			file.setExtension(filesDot.length > 1 ? filesDot[filesDot.length - 1] : filesDot[0]);
			while ((inputLine = br.readLine()) != null) {
				if (inputLine != null && !inputLine.equals("")) {
					File f = getBranchFileInfo(inputLine);
					if (null != f.getBytes() && !"".equals(f.getBytes())) {
						file.setBytes(f.getBytes());
					}
					if (null != f.getLines() && !"".equals(f.getLines())) {
						file.setLines(f.getLines());
					}
				}
			}
			br.close();

		} catch (IOException e) {
			LOGGER.error("Error extracting repository branches file lines.", e);
			
			return file;
		}
		return file;
	}

	/*
	 * Populates lines and bytes from file class
	 */
	public File getBranchFileInfo(String line) {
		File file = new File();
		final String lineIdentifier = " lines";
		final String byteIdentifier = "Bytes";
		if (line.indexOf(lineIdentifier) != -1) {
			String[] lineParts = line.split(" ");
			if(!lineParts[6].equals("")) {
			file.setLines(Integer.parseInt(lineParts[6]));
			}
		}
		if (line.indexOf(byteIdentifier) != -1) {
			String[] byteParts = line.split(" ");
			Double bytes = byteParts[5].equals("KB") ? Double.parseDouble(byteParts[4]) / 1000 : Double.parseDouble(byteParts[4]);
			file.setBytes(bytes);
		}

		return file;
	}
}
