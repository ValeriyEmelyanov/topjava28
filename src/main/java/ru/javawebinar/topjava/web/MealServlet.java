package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.repository.MapMealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final MealRepository mealRepository = new MapMealRepository();

    private final Map<String, ServletCommand> commands = new HashMap<>();

    {
        commands.put("list", this::list);
        commands.put("add", this::add);
        commands.put("edit", this::edit);
        commands.put("delete", this::delete);
        commands.put("create", this::create);
        commands.put("update", this::update);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        log.debug("handle GET request");

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        doAction(request, response, action);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        log.debug("handle POST request");

        String id = request.getParameter("id");
        String action = id == null || id.isEmpty() ? "create" : "update";

        doAction(request, response, action);

        response.sendRedirect("meals");
    }

    private void doAction(HttpServletRequest request,
                          HttpServletResponse response, String action) throws ServletException, IOException {
        ServletCommand command = commands.get(action);
        if (command != null) {
            command.execute(request, response);
        } else {
            log.debug("Wrong action: {}", action);
        }
    }

    private void list(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        log.debug("redirect to meals");

        List<MealTo> mealTos = MealsUtil.listTo(mealRepository.getAll());

        request.setAttribute("list", mealTos);
        request.setAttribute("dateTimeFormatter", dateTimeFormatter);

        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }

    private void add(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        log.debug("add meal");

        request.setAttribute("meal", null);

        request.getRequestDispatcher("mealform.jsp").forward(request, response);
    }

    private void edit(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        log.debug("edit meal");

        request.setAttribute("meal", mealRepository.getById(getId(request)));

        request.getRequestDispatcher("mealform.jsp").forward(request, response);
    }

    private void delete(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        log.debug("delete meal");

        Long id = getId(request);
        mealRepository.delete(id);
        log.debug("Deleted meal with id: {}", id);

        response.sendRedirect("meals");
    }

    private void create(HttpServletRequest request,
                     HttpServletResponse response) throws ServletException, IOException {
        log.debug("create meal");

        Meal meal = mealFromRequest(request);
        Meal created = mealRepository.save(meal);
        log.debug("Created meal: {}", created);
    }

    private void update(HttpServletRequest request,
                     HttpServletResponse response) throws ServletException, IOException {
        log.debug("update meal");

        Long id = getId(request);
        Meal fetched = mealRepository.getById(id);
        if (fetched == null) {
            log.warn("Meal not found, id: {}", id);
            return;
        }

        Meal meal = mealFromRequest(request);
        Meal updated = mealRepository.save(meal);
        log.debug("Update meal: {}", meal);
    }

    private Long getId(HttpServletRequest request) {
        String id = Objects.requireNonNull(request.getParameter("id"));
        return Long.parseLong(id);
    }

    private Meal mealFromRequest(HttpServletRequest request) {
        String id = request.getParameter("id");
        String dateTime = request.getParameter("dateTime");
        String description = request.getParameter("description");
        String calories = request.getParameter("calories");
        return new Meal(
                id == null ? null : Long.parseLong(id),
                LocalDateTime.parse(dateTime),
                description,
                Integer.parseInt(calories)
        );
    }
}
