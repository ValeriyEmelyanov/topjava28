package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public interface MealRepository {
    Meal creat(Meal meal);
    List<Meal> getAll();
    Meal getById(Long id);
    Meal save(Meal meal);
    void delete(Long id);
}
