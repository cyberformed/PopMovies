package br.com.tiagohs.popmovies.presenter;

import android.view.View;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.com.tiagohs.popmovies.model.credits.CreditMovieBasic;
import br.com.tiagohs.popmovies.model.dto.MovieListDTO;
import br.com.tiagohs.popmovies.model.person.PersonInfo;
import br.com.tiagohs.popmovies.server.ResponseListener;
import br.com.tiagohs.popmovies.server.methods.PersonsServer;
import br.com.tiagohs.popmovies.util.LocaleUtils;

import br.com.tiagohs.popmovies.util.ViewUtils;
import br.com.tiagohs.popmovies.util.enumerations.SubMethod;
import br.com.tiagohs.popmovies.view.PersonDetailView;

public class PersonDetailPresenterImpl implements PersonDetailPresenter, ResponseListener<PersonInfo> {
    private static final String LOCALE_US = "en";

    private PersonDetailView mPersonDetailView;
    private PersonsServer mPersonsServer;

    private final String[] mAppendToResponseValues = new String[]{SubMethod.MOVIE_CREDITS.getValue(),
                                                                  SubMethod.COMBINED_CREDITS.getValue(),
                                                                  SubMethod.EXTERNAL_IDS.getValue(),
                                                                  SubMethod.IMAGES.getValue(),
                                                                  SubMethod.TAGGED_IMAGES.getValue()};

    private int mPersonID;
    private String mTag;
    private boolean isFirstSearch;

    public PersonDetailPresenterImpl() {
        isFirstSearch = true;
        mPersonsServer = new PersonsServer();
    }

    @Override
    public void getPersonDetails(int personID, String tag) {

        mPersonID = personID;
        mTag = tag;

        if (mPersonDetailView.isInternetConnected()) {
            mPersonDetailView.setProgressVisibility(View.VISIBLE);
            mPersonsServer.getPersonDetails(personID, mAppendToResponseValues, tag, LocaleUtils.getLocaleLanguageAndCountry(), this);
        } else
            noConnectionError();

    }

    private void noConnectionError() {

        mPersonDetailView.onErrorNoConnection();
        mPersonDetailView.setProgressVisibility(View.GONE);
    }

    @Override
    public void setView(PersonDetailView view) {
        mPersonDetailView = view;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (mPersonDetailView.isAdded())
            mPersonDetailView.onErrorInServer();
    }

    @Override
    public void onResponse(PersonInfo personInfo) {

        if (mPersonDetailView.isAdded()) {

            if (ViewUtils.isEmptyValue(personInfo.getBiography()) && isFirstSearch) {
                isFirstSearch = false;

                mPersonsServer.getPersonDetails(mPersonID, mAppendToResponseValues, mTag, LOCALE_US, this);
            } else {
                mPersonDetailView.setPerson(personInfo);
                mPersonDetailView.updateImages();

                personInfo.setMoviesCarrer(createPersonKnowForMoviesDTO(personInfo.getMovieCredits().getCast(), personInfo.getMovieCredits().getCrew()));
                mPersonDetailView.updateAditionalInfo(personInfo.getMoviesCarrer().size(), (personInfo.getImages().size() + personInfo.getTaggedImages().size()));

                updateExternalLinks(personInfo);
                mPersonDetailView.setupTabs();
                mPersonDetailView.setProgressVisibility(View.GONE);
            }
        }

    }

    public List<MovieListDTO> createPersonKnowForMoviesDTO(List<CreditMovieBasic> cast, List<CreditMovieBasic> crew) {
        cast.addAll(crew);
        List<MovieListDTO> moviesMovieListDTO = new ArrayList<>();

        for (CreditMovieBasic c : cast) {
            MovieListDTO movie = new MovieListDTO(c.getId(), c.getTitle(), c.getArtworkPath(), null);

            if (moviesMovieListDTO.contains(movie))
                continue;
            else {
                moviesMovieListDTO.add(movie);
            }

        }

        return moviesMovieListDTO;
    }

    private void updateExternalLinks(PersonInfo personInfo) {

        if (personInfo.getExternalIDs().getFacebookId() != null)
            mPersonDetailView.setVisibilityFacebook(View.VISIBLE);

        if (personInfo.getExternalIDs().getTwitterId() != null)
            mPersonDetailView.setVisibilityTwitter(View.VISIBLE);

        if (personInfo.getExternalIDs().getInstagramID() != null)
            mPersonDetailView.setVisibilityInstagram(View.VISIBLE);

    }

}
