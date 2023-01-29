package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByStreams2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay
    ) {
        Map<LocalDate, Integer> calories = new HashMap<>();
        for (UserMeal meal : meals) {
            LocalDate localDate = meal.getDateTime().toLocalDate();
            calories.put(localDate, calories.getOrDefault(localDate,0) + meal.getCalories());
        }

        List<UserMealWithExcess> filtered = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                filtered.add(new UserMealWithExcess(
                        meal.getDateTime(),
                        meal.getDescription(),
                        meal.getCalories(),
                        calories.getOrDefault(meal.getDateTime().toLocalDate(), 0) > caloriesPerDay
                ));
            }
        }
        return filtered;
    }

    public static List<UserMealWithExcess> filteredByStreams(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay
    ) {
        Map<LocalDate, Integer> calories = meals.stream()
                .collect(Collectors.groupingBy(
                        meal -> meal.getDateTime().toLocalDate(),
                        Collectors.summingInt(UserMeal::getCalories)));
        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> new UserMealWithExcess(
                        meal.getDateTime(),
                        meal.getDescription(),
                        meal.getCalories(),
                        calories.getOrDefault(meal.getDateTime().toLocalDate(), 0) > caloriesPerDay
                ))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByStreams2(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay
    ) {
        return meals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate()))
                .values().stream()
                .map(v -> new Pair<>(v, v.stream().mapToInt(UserMeal::getCalories).sum() > caloriesPerDay))
                .flatMap(p -> p.first().stream()
                        .filter(m -> TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime))
                        .map(meal -> new UserMealWithExcess(
                                meal.getDateTime(),
                                meal.getDescription(),
                                meal.getCalories(),
                                p.second())
                        ))
                .collect(Collectors.toList());
    }
}
