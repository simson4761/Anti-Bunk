package com.example.proj1

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.proj1.util.Constants.Preference_First_Launch
import com.example.proj1.util.Constants.Preference_Name
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private val Context.datastore by preferencesDataStore(Preference_Name)
@ViewModelScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private object PreferenceKey{
        val firstLaunch = booleanPreferencesKey(Preference_First_Launch)
    }


    private val datastore : DataStore<Preferences> = context.datastore

    suspend fun saveFirstLaunch(firstLaunch : Boolean){
        datastore.edit { prefernces->
            prefernces[PreferenceKey.firstLaunch] = firstLaunch
        }
    }

    val readFirstLaunch : Flow<Boolean> = datastore.data
        .catch { exception->
            if (exception is IOException){
                emit(emptyPreferences())
            }
            else{
                throw exception
            }

        }.map { preference ->
            val firstLaunch = preference[PreferenceKey.firstLaunch] ?: true
            firstLaunch

        }
}