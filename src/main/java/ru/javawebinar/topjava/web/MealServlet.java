package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("redirect to meals");

        final int caloriesPerDay = 2000;
        List<Meal> meals = MealsUtil.getMealList();

        Map<LocalDate, Integer> caloriesByDates = meals.stream()
                .collect(Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories)));
        List<MealTo> mealTos = meals.stream()
                .map(meal -> MealsUtil.createTo(meal,
                        caloriesByDates.getOrDefault(meal.getDate(), 0) > caloriesPerDay))
                .sorted(Comparator.comparing(MealTo::getDateTime))
                .collect(Collectors.toList());

        request.setAttribute("list", mealTos);
        request.setAttribute("dateTimeFormatter", dateTimeFormatter);

        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }
}
