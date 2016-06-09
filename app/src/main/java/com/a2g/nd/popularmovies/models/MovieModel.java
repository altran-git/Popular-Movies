package com.a2g.nd.popularmovies.models;


import java.util.ArrayList;
import java.util.List;

public class MovieModel {
    private Integer page;
    private List<MovieResult> results = new ArrayList<MovieResult>();
    private Integer total_results;
    private Integer total_pages;

    /**
     *
     * @return
     * The page
     */
    public Integer getPage() {
        return page;
    }

    /**
     *
     * @param page
     * The page
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     *
     * @return
     * The movieResults
     */
    public List<MovieResult> getMovieResults() {
        return results;
    }

    /**
     *
     * @param results
     * The movieResults
     */
    public void setMovieResults(List<MovieResult> results) {
        this.results = results;
    }

    /**
     *
     * @return
     * The total_results
     */
    public Integer getTotal_results() {
        return total_results;
    }

    /**
     *
     * @param total_results
     * The total_results
     */
    public void setTotal_results(Integer total_results) {
        this.total_results = total_results;
    }

    /**
     *
     * @return
     * The total_pages
     */
    public Integer getTotal_pages() {
        return total_pages;
    }

    /**
     *
     * @param total_pages
     * The total_pages
     */
    public void setTotal_pages(Integer total_pages) {
        this.total_pages = total_pages;
    }
}
