package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MapMealRepository implements MealRepository {
    private static final AtomicLong idGenerator = new AtomicLong(0);
    private static final Map<Long, Meal> meals = new ConcurrentHashMap<>();

    static {
        init();
    }

    private static void init() {
        Meal meal1 = new Meal(idGenerator.incrementAndGet(),
                LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500);
        Meal meal2 = new Meal(idGenerator.incrementAndGet(),
                LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000);
        Meal meal3 = new Meal(idGenerator.incrementAndGet(),
                LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500);
        Meal meal4 = new Meal(idGenerator.incrementAndGet(),
                LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100);
        Meal meal5 = new Meal(idGenerator.incrementAndGet(),
                LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000);
        Meal meal6 = new Meal(idGenerator.incrementAndGet(),
                LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500);
        Meal meal7 = new Meal(idGenerator.incrementAndGet(),
                LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410);

        meals.put(meal1.getId(), meal1);
        meals.put(meal2.getId(), meal2);
        meals.put(meal3.getId(), meal3);
        meals.put(meal4.getId(), meal4);
        meals.put(meal5.getId(), meal5);
        meals.put(meal6.getId(), meal6);
        meals.put(meal7.getId(), meal7);
    }

    @Override
    public List<Meal> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(meals.values()));
    }

    @Override
    public Meal getById(Long id) {
        return meals.get(id);
    }

    @Override
    public Meal save(Meal meal) {
        if (isNew(meal)) {
            Meal created = new Meal(idGenerator.incrementAndGet(),
                    meal.getDateTime(), meal.getDescription(), meal.getCalories());
            meals.put(created.getId(), created);
            return created;
        }

        return meals.computeIfPresent(meal.getId(), (id, old) -> meal);
    }

    @Override
    public boolean delete(Long id) {
        return meals.remove(id) != null;
    }

    private boolean isNew(Meal meal) {
        return meal.getId() == null;
    }
}
