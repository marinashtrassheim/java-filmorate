package ru.yandex.practicum.filmorate.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaDbStorage mpaStorage;

    public MpaController(MpaDbStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @GetMapping
    public Collection<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable int id) {
       return mpaStorage.getMpa(id);
    }
}
