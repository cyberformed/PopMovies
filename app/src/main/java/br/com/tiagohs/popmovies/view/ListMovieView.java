package br.com.tiagohs.popmovies.view;

import java.util.List;

import br.com.tiagohs.popmovies.model.dto.MovieListDTO;


public interface ListMovieView {

    void hideDialogProgress();
    void showDialogProgress();
    void atualizarView(int currentPage, int totalPages, List<MovieListDTO> listMovies);
    void onError(String msg);
}
