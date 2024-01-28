package com.example.ProjektSM.quiz;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.ProjektSM.R;
import com.example.ProjektSM.WordLearning;
import com.example.ProjektSM.database.Repository;
import com.example.ProjektSM.database.entity.Synonym;
import com.example.ProjektSM.database.entity.Word;

import java.util.LinkedList;
import java.util.List;

import java.util.concurrent.Executors;

public class SynonymQuizActivity extends QuizActivity {
    private static final String KEY_QUESTION_ID = "questionId";
    private long questionId;

    @Override
    protected int getQuestionLabelStringId() {
        return R.string.pick_the_words_synonym;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_QUESTION_ID, questionId);
    }

    @Override
    protected void setupFrom(Bundle state) {
        super.setupFrom(state);
        questionId = state.getLong(KEY_QUESTION_ID, -1);
        Handler uiHandler = new Handler(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            Repository repo = new Repository(this.getApplication());
            Word question = repo.getWord(questionId);
            Synonym answer = repo.getSynonym(correctAnswerId);
            LinkedList<Synonym> answers = new LinkedList<>();
            for (long id : answerIds)
                answers.addLast(repo.getSynonym(id));
            saveIds(question, answer, answers);
            uiHandler.post(() -> fillWidgets(question, answers));
        });
    }

    private void saveIds(Word question, Synonym answer, List<Synonym> answers) {
        questionId = question.getId();
        correctAnswerId = answer.getId();
        answerIds = new long[answers.size()];
        int i = 0;
        for (Synonym synonym : answers)
            answerIds[i++] = synonym.getId();
    }

    private void fillWidgets(Word question, List<Synonym> answers) {
        questionContent.setText(question.getWord());
        int i = 0;
        for (Synonym answer : answers)
            answerButtons[i++].setText(answer.getSynonym());
    }

    @Override
    protected void noteCorrectAnswer(Repository repo) {
        repo.incrementStatistic(WordLearning.CORRECT_SYNONYM_MATCHES);
    }

    @Override
    protected void noteIncorrectAnswer(Repository repo) {} // nic nie robimy za nieprawidłową odpowiedź

    protected void setupNew(Handler uiHandler, Repository repo) {
        List<Word> allWordsWithSynonyms = repo.getWordsWithSynonyms();
        if (allWordsWithSynonyms.isEmpty()) {
            finishWithError(R.string.no_words);
            return;
        }
        Word question = pickNRandomElements(allWordsWithSynonyms, 1, random).get(0);
        List<Synonym> questionSynonyms = repo.getSynonymsFor(question.getId());
        Synonym answer = pickNRandomElements(questionSynonyms, 1, random).get(0);
        List<Synonym> notQuestionSynonyms = repo.getSynonymsNotFor(question.getId());
        if (notQuestionSynonyms.size() < 4) { // w bazie danych nie ma nawet 4 synonimów nienależących do pytania
            finishWithError(R.string.no_synonyms_for_answers);
            return;
        }
        List<Synonym> answers = generateAnswers(answer, notQuestionSynonyms);
        saveIds(question, answer, answers);
        uiHandler.post(() -> fillWidgets(question, answers));
    }

    private List<Synonym> generateAnswers(Synonym answer, List<Synonym> from) {
        List<Synonym> answers = pickNRandomElements(from, 3, random);
        int correctAnswerIndex = random.nextInt(4);
        answers.add(correctAnswerIndex, answer);
        return answers;
    }
}