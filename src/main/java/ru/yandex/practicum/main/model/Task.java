package main.java.ru.yandex.practicum.main.model;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // если объекты одинаковые (ссылаются на один и тот же объект)
        if (o == null || getClass() != o.getClass()) return false;  // проверка на null и тип объекта
        Task task = (Task) o;
        return id == task.id &&  // сравниваем по id
                Objects.equals(name, task.name) &&  // сравниваем по имени
                Objects.equals(description, task.description) &&  // сравниваем по описанию
                status == task.status;  // сравниваем по статусу
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        return "Задача{" +
                "id=" + id +
                ", имя='" + name + '\'' +
                ", описание='" + description + '\'' +
                ", статус=" + status +
                '}';
    }
}

