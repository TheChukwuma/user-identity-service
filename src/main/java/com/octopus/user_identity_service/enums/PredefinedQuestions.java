package com.octopus.user_identity_service.enums;

public enum PredefinedQuestions {
    MOTHER_MAIDEN_NAME("What is your mother's maiden name?"),
    FIRST_PET_NAME("What is the name of your first pet?"),
    BIRTH_CITY("What city were you born in?"),
    FAVORITE_TEACHER("What was the name of your favorite teacher?"),
    CHILDHOOD_FRIEND("What was the name of your childhood best friend?"),
    FIRST_CAR("What was the model of your first car?"),
    FAVORITE_BOOK("What is your favorite book?"),
    DREAM_JOB("What was your dream job as a child?");

    private final String question;

    PredefinedQuestions(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }
}
