package com.wongnai.interview.movie.search;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.wongnai.interview.movie.Movie;
import com.wongnai.interview.movie.MovieSearchService;
import com.wongnai.interview.movie.external.MovieData;
import com.wongnai.interview.movie.external.MovieDataService;
import com.wongnai.interview.movie.external.MovieDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("simpleMovieSearchService")
public class SimpleMovieSearchService implements MovieSearchService {

    @Autowired
    private MovieDataService movieDataService;

    @Override
    public List<Movie> search(String queryText) {
        //TODO: Step 2 => Implement this method by using data from MovieDataService
        // All test in SimpleMovieSearchServiceIntegrationTest must pass.
        // Please do not change @Component annotation on this class

        movieDataService = new MovieDataServiceImpl();
        List<Movie> movieList = new ArrayList<>();
        if (!queryText.contains(" ")) {
            searchAndParseToMovie(queryText, movieList);
        }
        return movieList;
    }

    private void searchAndParseToMovie(String queryText, List<Movie> movieList) {
        List<MovieData> movieDataList = movieDataService.fetchAll().stream()
                .filter(title -> title.getTitle().toLowerCase().matches(".*\\b" + queryText.toLowerCase() + "\\b.*")).collect(Collectors.toList());
        long generateId = 0;
        for (MovieData movieData : movieDataList) {
            generateId++;
            movieList.add(new Movie(generateId, movieData.getTitle(), movieData.getCast()));
        }
    }
}
