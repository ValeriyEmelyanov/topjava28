package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int MEAL_ID = START_SEQ + 3;
    public static final int MEAL_NOT_FOUND_ID = START_SEQ + 1000;
    public static final int USER_MEAL_COUNT = 7;
    public static LocalDate START_DAY = LocalDate.of(2020,1, 30);
    public static final int USER_DAY_MEAL_COUNT = 3;

    public static final Meal meal = new Meal(
            MEAL_ID,
            LocalDateTime.of(2020, 01, 30, 10, 0),
            "Завтрак",
            500);

    public static Meal getNew() {
        return new Meal(
                null,
                LocalDateTime.of(2023, 02, 20, 10, 0),
                "Завтрак",
                700);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(meal);
        updated.setDateTime(LocalDateTime.of(2020, 01, 30, 11, 30));
        updated.setDescription("Поздний завтрак с добавкой");
        updated.setCalories(511);
        return updated;
    }
}
