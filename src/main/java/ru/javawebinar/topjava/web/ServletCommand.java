package ru.javawebinar.topjava.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ServletCommand {
    void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
