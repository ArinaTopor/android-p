package com.example.profile.repository;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class ProfileRepository_Factory implements Factory<ProfileRepository> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public ProfileRepository_Factory(Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public ProfileRepository get() {
    return newInstance(dataStoreProvider.get());
  }

  public static ProfileRepository_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new ProfileRepository_Factory(dataStoreProvider);
  }

  public static ProfileRepository newInstance(DataStore<Preferences> dataStore) {
    return new ProfileRepository(dataStore);
  }
}
