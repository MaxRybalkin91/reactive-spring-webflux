package com.reactivespring.moviesinfoservice.repository;

import com.reactivespring.moviesinfoservice.TestContainersConfig;
import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@DataMongoTest
@ActiveProfiles("test")
class MoviesInfoRepositoryTest extends TestContainersConfig {
    @Autowired
    private MovieInfoRepository movieInfoRepository;

    private static final List<MovieInfo> MOVIE_INFOS = List.of(
            new MovieInfo("terminator1986", "The Terminator", 1986,
                    List.of("Arnold Schwarzenegger", "Linda Hamilton", "Michael Biehn", "Earl Boen"),
                    LocalDate.of(1984, 10, 26)),
            new MovieInfo("matrix1999", "The Matrix", 1999,
                    List.of("Keanu Reeves", "Carrie-Anne Moss", "Laurence Fishburne", "Hugo Weaving"),
                    LocalDate.of(1999, 3, 31)),
            new MovieInfo("mortalKombat1995", "Mortal Kombat", 1995,
                    List.of("Christopher Lambert", "Robin Shou", "Bridgette Wilson", "Linden Ashby"),
                    LocalDate.of(1995, 7, 13))
    );

    @BeforeEach
    public void setUp() {
        movieInfoRepository.saveAll(MOVIE_INFOS).blockLast();
    }

    @AfterEach
    public void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void checkGettingAllMovies() {
        var allMovies = movieInfoRepository.findAll().log();

        StepVerifier.create(allMovies)
                .expectNextCount(MOVIE_INFOS.size())
                .verifyComplete();
    }

    @Test
    void checkGettingEachMovieById() {
        for (MovieInfo movie : MOVIE_INFOS) {
            assert movie.getMovieInfoId() != null;
            StepVerifier.create(movieInfoRepository.findById(movie.getMovieInfoId()))
                    .assertNext(movieInfo -> {
                        assert (Objects.equals(movieInfo.getMovieInfoId(), movie.getMovieInfoId()));
                        assert (movieInfo.getName().equals(movie.getName()));
                        assert (movieInfo.getYear().equals(movie.getYear()));
                        for (int i = 0; i < movie.getCast().size(); i++) {
                            assert (movieInfo.getCast().get(i).equals(movie.getCast().get(i)));
                        }
                    })
                    .verifyComplete();
        }
    }

    @Test
    void checkGettingEachMovieByYear() {
        for (MovieInfo movie : MOVIE_INFOS) {
            StepVerifier.create(movieInfoRepository.findByYear(movie.getYear()))
                    .assertNext(movieInfo -> {
                        assert (Objects.equals(movieInfo.getMovieInfoId(), movie.getMovieInfoId()));
                        assert (movieInfo.getName().equals(movie.getName()));
                        assert (movieInfo.getYear().equals(movie.getYear()));
                        for (int i = 0; i < movie.getCast().size(); i++) {
                            assert (movieInfo.getCast().get(i).equals(movie.getCast().get(i)));
                        }
                    })
                    .verifyComplete();
        }
    }

    @Test
    void checkGettingEachMovieByName() {
        for (MovieInfo movie : MOVIE_INFOS) {
            StepVerifier.create(movieInfoRepository.findByName(movie.getName()))
                    .assertNext(movieInfo -> {
                        assert (Objects.equals(movieInfo.getMovieInfoId(), movie.getMovieInfoId()));
                        assert (movieInfo.getName().equals(movie.getName()));
                        assert (movieInfo.getYear().equals(movie.getYear()));
                        for (int i = 0; i < movie.getCast().size(); i++) {
                            assert (movieInfo.getCast().get(i).equals(movie.getCast().get(i)));
                        }
                    })
                    .verifyComplete();
        }
    }

    @Test
    @DirtiesContext
    void checkMovieSaving() {
        var newMovieInfo = new MovieInfo(null, "Test", 2024,
                List.of("John A", "Robert B", "Barbara C"),
                LocalDate.of(2024, 1, 1));

        var saving = movieInfoRepository.save(newMovieInfo).log();

        StepVerifier.create(saving)
                .assertNext(movieInfo -> {
                    assert (movieInfo.getMovieInfoId() != null);
                    assert (movieInfo.getName().equals(newMovieInfo.getName()));
                    assert (movieInfo.getYear().equals(newMovieInfo.getYear()));
                    for (int i = 0; i < movieInfo.getCast().size(); i++) {
                        assert (movieInfo.getCast().get(i).equals(newMovieInfo.getCast().get(i)));
                    }
                })
                .verifyComplete();
    }

    @Test
    @DirtiesContext
    void checkMovieUpdating() {
        assert !MOVIE_INFOS.isEmpty();
        assert MOVIE_INFOS.get(0) != null;
        var movieId = MOVIE_INFOS.get(0).getMovieInfoId();
        assert movieId != null;
        var movie = movieInfoRepository.findById(movieId).block();
        assert movie != null;

        movie.setYear(2024);

        var movieUpdated = movieInfoRepository.save(movie).log();

        StepVerifier.create(movieUpdated)
                .assertNext(movieInfo -> {
                    assert (!movieInfo.getYear().equals(MOVIE_INFOS.get(0).getYear()));
                    assert (movieInfo.getYear().equals(movie.getYear()));
                })
                .verifyComplete();
    }

    @Test
    @DirtiesContext
    void checkMovieDeleting() {
        movieInfoRepository.delete(MOVIE_INFOS.get(0)).block();
        var movies = movieInfoRepository.findAll().log();

        StepVerifier.create(movies)
                .expectNextCount(MOVIE_INFOS.size() - 1)
                .verifyComplete();
    }

    @Test
    @DirtiesContext
    void checkMovieDeletingById() {
        assert !MOVIE_INFOS.isEmpty();
        assert MOVIE_INFOS.get(0) != null;
        var movieId = MOVIE_INFOS.get(0).getMovieInfoId();
        assert movieId != null;

        movieInfoRepository.deleteById(movieId).block();
        var movies = movieInfoRepository.findAll().log();

        StepVerifier.create(movies)
                .expectNextCount(MOVIE_INFOS.size() - 1)
                .verifyComplete();
    }
}
