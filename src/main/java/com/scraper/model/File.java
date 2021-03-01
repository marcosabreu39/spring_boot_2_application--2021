package com.scraper.model;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class File implements Serializable, Comparable<File> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private String branchName;

	private String extension;

	private Integer lines;

	private Double bytes;

	private GithubRepository repository;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public Integer getLines() {
		return lines;
	}

	public void setLines(Integer lines) {
		this.lines = lines;
	}

	public Double getBytes() {
		return bytes;
	}

	public void setBytes(Double bytes) {
		this.bytes = bytes;
	}

	public GithubRepository getRepository() {
		return repository;
	}

	public void setRepository(GithubRepository repository) {
		this.repository = repository;
	}

	@Override
	public int compareTo(File f) {
		if (getExtension() == null || f.getExtension() == null) {
			return 0;
		}
		return getExtension().compareTo(f.getExtension());
	}
}
