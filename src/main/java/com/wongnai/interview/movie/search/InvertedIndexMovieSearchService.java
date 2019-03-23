package com.wongnai.interview.movie.search;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.wongnai.interview.movie.Movie;
import com.wongnai.interview.movie.MovieRepository;
import com.wongnai.interview.movie.MovieSearchService;

@Component("invertedIndexMovieSearchService")
@DependsOn("movieDatabaseInitializer")
public class InvertedIndexMovieSearchService implements MovieSearchService {
    @Autowired
    private MovieRepository movieRepository;

    @Override
    public List<Movie> search(String queryText) {
        //TODO: Step 4 => Please implement in-memory inverted index to search movie by keyword.
        // You must find a way to build inverted index before you do an actual search.
        // Inverted index would looks like this:
        // -------------------------------
        // |  Term      | Movie Ids      |
        // -------------------------------
        // |  Star      |  5, 8, 1       |
        // |  War       |  5, 2          |
        // |  Trek      |  1, 8          |
        // -------------------------------
        // When you search with keyword "Star", you will know immediately, by looking at Term column, and see that
        // there are 3 movie ids contains this word -- 1,5,8. Then, you can use these ids to find full movie object from repository.
        // Another case is when you find with keyword "Star War", there are 2 terms, Star and War, then you lookup
        // from inverted index for Star and for War so that you get movie ids 1,5,8 for Star and 2,5 for War. The result that
        // you have to return can be union or intersection of those 2 sets of ids.
        // By the way, in this assignment, you must use intersection so that it left for just movie id 5.

        String[] wordsFromQueryText = queryText.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().split("\\s+");
        Map<String, Set<Long>> invertedIndex = getInvertedIndex();

        List<Set<Long>> setList = new ArrayList<>();
        boolean isAllWordsMatch = true;

        //Listed set of word index. If has at least one word that not match, break out of loop
        for (String word : wordsFromQueryText) {
            if (invertedIndex.containsKey(word)) {
                setList.add(invertedIndex.get(word));
            } else {
                isAllWordsMatch = false;
                break;
            }
        }

        List<Movie> movieList = new ArrayList<>();

        if (isAllWordsMatch) {
            Set<Long> movieId = setList.get(0);

            //Set Intersection
            for (int i = 1; i < setList.size(); i++) {
                movieId.retainAll(setList.get(i));
            }

            //Search movie by Id
            for (Long id : movieId) {
                movieList.add(movieRepository.findById(id).get());
            }

        }
        return movieList;
    }

    private Map<String, Set<Long>> getInvertedIndex() {

        Map<String, Set<Long>> invertedIndex = new HashMap<>();
        List<Movie> movieList = (List<Movie>) movieRepository.findAll();

        for (Movie movie : movieList) {
            String[] wordFromTitle = movie.getName().replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().split("\\s+");
            for (String word : wordFromTitle) {
                Set<Long> movieIdSet = new HashSet<>();
                if (invertedIndex.containsKey(word)) {
                    movieIdSet = invertedIndex.get(word);
                }
                movieIdSet.add(movie.getId());
                invertedIndex.put(word, movieIdSet);
            }
        }
        return invertedIndex;
    }
}
