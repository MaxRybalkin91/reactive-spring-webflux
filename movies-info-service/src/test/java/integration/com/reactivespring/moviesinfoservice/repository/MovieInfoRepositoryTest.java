package com.reactivespring.moviesinfoservice.repository;

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

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryTest extends AbstractRepositoryTest {
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
    void checkGettingEachMovie() {
        for (MovieInfo movie : MOVIE_INFOS) {
            StepVerifier.create(movieInfoRepository.findById(movie.getMovieInfoId()))
                    .assertNext(movieInfo -> {
                        assert (movieInfo.getMovieInfoId().equals(movie.getMovieInfoId()));
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
        var movie = movieInfoRepository.findById(MOVIE_INFOS.get(0).getMovieInfoId()).block();
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
        movieInfoRepository.deleteById(MOVIE_INFOS.get(0).getMovieInfoId()).block();
        var movies = movieInfoRepository.findAll().log();

        StepVerifier.create(movies)
                .expectNextCount(MOVIE_INFOS.size() - 1)
                .verifyComplete();
    }
}
