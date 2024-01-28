package com.example.ProjektSM.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ProjektSM.database.dao.StatisticDao;
import com.example.ProjektSM.database.dao.SynonymDao;
import com.example.ProjektSM.database.dao.WordDao;
import com.example.ProjektSM.database.dao.WordSynonymDao;
import com.example.ProjektSM.database.entity.Statistic;
import com.example.ProjektSM.database.entity.Synonym;
import com.example.ProjektSM.database.entity.Word;
import com.example.ProjektSM.database.entity.WordSynonym;

@androidx.room.Database(entities = { Word.class, WordSynonym.class, Synonym.class, Statistic.class },
        version = 3, exportSchema = false)
public abstract class Database extends RoomDatabase {
    private static volatile Database INSTANCE;

    public abstract WordDao getWordDao();
    public abstract WordSynonymDao getWordSynonymDao();
    public abstract SynonymDao getSynonymDao();
    public abstract StatisticDao getStatisticDao();

    static Database getInstance(final Context applicationContext) {
        if (INSTANCE == null)
            INSTANCE = Room.databaseBuilder(applicationContext,
                            Database.class,
                            "word_database")
                    .fallbackToDestructiveMigration()
                    .build();
        return INSTANCE;
    }
}

