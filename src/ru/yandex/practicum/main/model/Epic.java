package ru.yandex.practicum.main.model;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private List<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subTasks = new ArrayList<>();
    }

    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("Подзадача пуста");
            return;
        }
        subTask.setEpic(this);
        subTasks.add(subTask);
    }

    public List<SubTask> getSubTask() {
        return subTasks;
    }

    @Override
    public String toString() {
        return "Эпик{" +
                "id=" + getId() +
                ", имя='" + getName() + '\'' +
                ", описание='" + getDescription() + '\'' +
                ", статус=" + getStatus() +
                '}';
    }
}
