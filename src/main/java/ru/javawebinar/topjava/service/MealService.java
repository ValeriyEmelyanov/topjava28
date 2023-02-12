package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.Collection;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal, int userId) {
        Meal newMeal = new Meal(
                meal.getDateTime(),
                meal.getDescription(),
                meal.getCalories(),
                userId
        );
        return repository.save(newMeal, userId);
    }

    public void delete(int id, int userId) {
        checkNotFoundWithId(repository.delete(id, userId), id);
    }

    public Meal get(int id, int userId) {
        return checkNotFoundWithId(repository.get(id, userId), id);
    }

    public List<MealTo> getAll(int userId) {
        Collection<Meal> all = repository.getAll(userId);
        return MealsUtil.getTos(all, MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public Meal update(Meal meal, int userId) {
        return checkNotFoundWithId(repository.save(meal, userId), meal.getId());
    }
}