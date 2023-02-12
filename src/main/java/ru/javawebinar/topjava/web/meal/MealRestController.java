package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;

import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        log.info("get all meals");
        return service.getAll(authUserId());
    }

    public Meal get(int id) {
        log.info("get meal with id {}", id);
        return service.get(id, authUserId());
    }

    public Meal create(Meal meal) {
        log.info("create meal {}", meal);
        return service.create(meal, authUserId());
    }

    public void delete(int id) {
        log.info("delete meal with id {}", id);
        service.delete(id, authUserId());
    }

    public Meal update(Meal meal, int id) {
        log.info("update meal {} with id={}", meal, id);
        assureIdConsistent(meal, id);
        return service.update(meal, authUserId());
    }
}