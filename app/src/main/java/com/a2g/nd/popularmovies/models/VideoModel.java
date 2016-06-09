package com.a2g.nd.popularmovies.models;

import java.util.ArrayList;
import java.util.List;

public class VideoModel {

    private Integer id;
    private List<VideoResult> results = new ArrayList<VideoResult>();

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The results
     */
    public List<VideoResult> getVideoResults() {
        return results;
    }

    /**
     * @param results The results
     */
    public void setVideoResults(List<VideoResult> results) {
        this.results = results;
    }
}
