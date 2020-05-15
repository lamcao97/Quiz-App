package com.example.englishappwithsqlite.Interface;

import com.example.englishappwithsqlite.Model.CurrentQuestion;

public interface IQuestion {
    CurrentQuestion getSelectedAnswer();
    void showCorrectAnswer();
    void disableAnswer();
    void resetQuestion();
}
