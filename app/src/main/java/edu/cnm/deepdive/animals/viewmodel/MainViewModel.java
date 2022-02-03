package edu.cnm.deepdive.animals.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import edu.cnm.deepdive.animals.model.Animal;
import edu.cnm.deepdive.animals.service.AnimalRepository;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class MainViewModel extends AndroidViewModel implements LifecycleObserver {

  private final AnimalRepository repository;
  private final MutableLiveData<List<Animal>> animals;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  public MainViewModel(@NonNull Application application) {
    super(application);
    repository = new AnimalRepository(application);
    animals = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
    load();
  }

  public LiveData<List<Animal>> getAnimals() {
    return animals;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  // TODO implement load method to get animals
  private void load() {
    pending.add(
        repository
            .getAnimals()
            .subscribe(
                new Consumer<List<Animal>>() {
                  @Override
                  public void accept(List<Animal> value) throws Throwable {
                    animals.postValue(value);
                  }
                },
                new Consumer<Throwable>() {
                  @Override
                  public void accept(Throwable value1) throws Throwable {
                    throwable.postValue(value1);
                  }
                }
            )
    );

  }

  // TODO better implementation needed
  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  private void clearPending() {
    pending.clear();
  }
}
