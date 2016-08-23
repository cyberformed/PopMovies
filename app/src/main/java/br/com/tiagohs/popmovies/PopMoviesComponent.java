package br.com.tiagohs.popmovies;

import javax.inject.Singleton;

import br.com.tiagohs.popmovies.presenter.PresenterModule;
import br.com.tiagohs.popmovies.view.fragment.ListMoviesFragment;
import dagger.Component;

@Singleton
@Component(modules = {PresenterModule.class})
public interface PopMoviesComponent {

    void inject(ListMoviesFragment listMoviesFragment);
}
